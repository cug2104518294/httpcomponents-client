package org.apache.hc.client5.http.impl.classic;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.SchemePortResolver;
import org.apache.hc.client5.http.classic.ExecRuntime;
import org.apache.hc.client5.http.config.Configurable;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.ConnectionShutdownException;
import org.apache.hc.client5.http.impl.DefaultSchemePortResolver;
import org.apache.hc.client5.http.impl.ExecSupport;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.protocol.RequestClientConnControl;
import org.apache.hc.client5.http.routing.RoutingSupport;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.concurrent.CancellableDependency;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy;
import org.apache.hc.core5.http.impl.io.HttpRequestExecutor;
import org.apache.hc.core5.http.protocol.*;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.net.URIAuthority;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.VersionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * Minimal implementation of {@link CloseableHttpClient}. This client is
 * optimized for HTTP/1.1 message transport and does not support advanced
 * HTTP protocol functionality such as request execution via a proxy, state
 * management, authentication and request redirects.
 * <p>
 * Concurrent message exchanges executed by this client will get assigned to
 * separate connections leased from the connection pool.
 * </p>
 *
 * @since 4.3
 */
@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public class MinimalHttpClient extends CloseableHttpClient {

    private final Logger log = LoggerFactory.getLogger(getClass());

    //连接管理器
    private final HttpClientConnectionManager connManager;
    private final ConnectionReuseStrategy reuseStrategy;
    private final SchemePortResolver schemePortResolver;
    //执行器
    private final HttpRequestExecutor requestExecutor;
    private final HttpProcessor httpProcessor;

    MinimalHttpClient(final HttpClientConnectionManager connManager) {
        super();
        this.connManager = Args.notNull(connManager, "HTTP connection manager");
        this.reuseStrategy = DefaultConnectionReuseStrategy.INSTANCE;
        this.schemePortResolver = DefaultSchemePortResolver.INSTANCE;
        this.requestExecutor = new HttpRequestExecutor(this.reuseStrategy);
        this.httpProcessor = new DefaultHttpProcessor(
                new RequestContent(),
                new RequestTargetHost(),
                new RequestClientConnControl(),
                new RequestUserAgent(VersionInfo.getSoftwareInfo(
                        "Apache-HttpClient", "org.apache.hc.client5", getClass())));
    }

    @Override
    protected CloseableHttpResponse doExecute(
            final HttpHost target,
            final ClassicHttpRequest request,
            final HttpContext context) throws IOException {
        Args.notNull(target, "Target host");
        Args.notNull(request, "HTTP request");
        if (request.getScheme() == null) {
            request.setScheme(target.getSchemeName());
        }
        if (request.getAuthority() == null) {
            request.setAuthority(new URIAuthority(target));
        }
        //请求上下文
        final HttpClientContext clientContext = HttpClientContext.adapt(
                context != null ? context : new BasicHttpContext());
        //请求相关参数
        RequestConfig config = null;
        if (request instanceof Configurable) {
            config = ((Configurable) request).getConfig();
        }
        if (config != null) {
            clientContext.setRequestConfig(config);
        }
        //
        final HttpRoute route = new HttpRoute(RoutingSupport.normalize(target, schemePortResolver));
        final String exchangeId = ExecSupport.getNextExchangeId();
        final ExecRuntime execRuntime = new InternalExecRuntime(log, connManager, requestExecutor,
                request instanceof CancellableDependency ? (CancellableDependency) request : null);
        try {
            if (!execRuntime.isEndpointAcquired()) {
                execRuntime.acquireEndpoint(exchangeId, route, null, clientContext);
            }
            if (!execRuntime.isEndpointConnected()) {
                execRuntime.connectEndpoint(clientContext);
            }

            context.setAttribute(HttpCoreContext.HTTP_REQUEST, request);
            context.setAttribute(HttpClientContext.HTTP_ROUTE, route);

            httpProcessor.process(request, request.getEntity(), context);
            final ClassicHttpResponse response = execRuntime.execute(exchangeId, request, clientContext);
            httpProcessor.process(response, response.getEntity(), context);

            if (reuseStrategy.keepAlive(request, response, context)) {
                execRuntime.markConnectionReusable(null, TimeValue.NEG_ONE_MILLISECONDS);
            } else {
                execRuntime.markConnectionNonReusable();
            }

            // check for entity, release connection if possible
            final HttpEntity entity = response.getEntity();
            if (entity == null || !entity.isStreaming()) {
                // connection not needed and (assumed to be) in re-usable state
                execRuntime.releaseEndpoint();
                return new CloseableHttpResponse(response, null);
            }
            ResponseEntityProxy.enhance(response, execRuntime);
            return new CloseableHttpResponse(response, execRuntime);
        } catch (final ConnectionShutdownException ex) {
            final InterruptedIOException ioex = new InterruptedIOException("Connection has been shut down");
            ioex.initCause(ex);
            execRuntime.discardEndpoint();
            throw ioex;
        } catch (final HttpException httpException) {
            execRuntime.discardEndpoint();
            throw new ClientProtocolException(httpException);
        } catch (final RuntimeException | IOException ex) {
            execRuntime.discardEndpoint();
            throw ex;
        } catch (final Error error) {
            connManager.close(CloseMode.IMMEDIATE);
            throw error;
        }
    }

    @Override
    public void close() throws IOException {
        this.connManager.close();
    }

}
