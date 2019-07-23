package org.apache.hc.client5.http.impl.classic;

import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.UserTokenHandler;
import org.apache.hc.client5.http.classic.ExecChain;
import org.apache.hc.client5.http.classic.ExecChainHandler;
import org.apache.hc.client5.http.classic.ExecRuntime;
import org.apache.hc.client5.http.impl.ConnectionShutdownException;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.message.RequestLine;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * Usually the last request execution handler in the classic request execution
 * chain that is responsible for execution of request / response exchanges with
 * the opposite endpoint.
 *
 * @since 4.3
 */
@Contract(threading = ThreadingBehavior.STATELESS)
@Internal
public final class MainClientExec implements ExecChainHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final HttpClientConnectionManager connectionManager;
    private final ConnectionReuseStrategy reuseStrategy;
    private final ConnectionKeepAliveStrategy keepAliveStrategy;
    private final UserTokenHandler userTokenHandler;

    /**
     * @since 4.4
     */
    public MainClientExec(
            final HttpClientConnectionManager connectionManager,
            final ConnectionReuseStrategy reuseStrategy,
            final ConnectionKeepAliveStrategy keepAliveStrategy,
            final UserTokenHandler userTokenHandler) {
        this.connectionManager = Args.notNull(connectionManager, "Connection manager");
        this.reuseStrategy = Args.notNull(reuseStrategy, "Connection reuse strategy");
        this.keepAliveStrategy = Args.notNull(keepAliveStrategy, "Connection keep alive strategy");
        this.userTokenHandler = Args.notNull(userTokenHandler, "User token handler");
    }

    @Override
    public ClassicHttpResponse execute(
            final ClassicHttpRequest request,
            final ExecChain.Scope scope,
            final ExecChain chain) throws IOException, HttpException {
        Args.notNull(request, "HTTP request");
        Args.notNull(scope, "Scope");
        final String exchangeId = scope.exchangeId;
        final HttpRoute route = scope.route;
        final HttpClientContext context = scope.clientContext;
        final ExecRuntime execRuntime = scope.execRuntime;

        if (log.isDebugEnabled()) {
            log.debug(exchangeId + ": executing " + new RequestLine(request));
        }
        try {
            RequestEntityProxy.enhance(request);
            final ClassicHttpResponse response = execRuntime.execute(exchangeId, request, context);
            Object userToken = context.getUserToken();
            if (userToken == null) {
                userToken = userTokenHandler.getUserToken(route, context);
                context.setAttribute(HttpClientContext.USER_TOKEN, userToken);
            }
            // The connection is in or can be brought to a re-usable state.
            if (reuseStrategy.keepAlive(request, response, context)) {
                // Set the idle duration of this connection
                final TimeValue duration = keepAliveStrategy.getKeepAliveDuration(response, context);
                if (this.log.isDebugEnabled()) {
                    final String s;
                    if (duration != null) {
                        s = "for " + duration;
                    } else {
                        s = "indefinitely";
                    }
                    this.log.debug(exchangeId + ": connection can be kept alive " + s);
                }
                execRuntime.markConnectionReusable(userToken, duration);
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
            final InterruptedIOException ioex = new InterruptedIOException(
                    "Connection has been shut down");
            ioex.initCause(ex);
            execRuntime.discardEndpoint();
            throw ioex;
        } catch (final HttpException | RuntimeException | IOException ex) {
            execRuntime.discardEndpoint();
            throw ex;
        } catch (final Error error) {
            connectionManager.close(CloseMode.IMMEDIATE);
            throw error;
        }

    }
}
