package org.apache.hc.client5.http.impl.routing;

import java.net.InetAddress;

import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.SchemePortResolver;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultSchemePortResolver;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.routing.HttpRoutePlanner;
import org.apache.hc.client5.http.routing.RoutingSupport;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.protocol.HttpContext;

/**
 * Default implementation of an {@link HttpRoutePlanner}. It will not make use of
 * any Java system properties, nor of system or browser proxy settings.
 *
 * @since 4.3
 */
@Contract(threading = ThreadingBehavior.STATELESS)
public class DefaultRoutePlanner implements HttpRoutePlanner {

    private final SchemePortResolver schemePortResolver;

    public DefaultRoutePlanner(final SchemePortResolver schemePortResolver) {
        super();
        this.schemePortResolver = schemePortResolver != null ? schemePortResolver : DefaultSchemePortResolver.INSTANCE;
    }

    @Override
    public final HttpRoute determineRoute(final HttpHost host, final HttpContext context) throws HttpException {
        if (host == null) {
            throw new ProtocolException("Target host is not specified");
        }
        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        final RequestConfig config = clientContext.getRequestConfig();
        HttpHost proxy = config.getProxy();
        if (proxy == null) {
            proxy = determineProxy(host, context);
        }
        final HttpHost target = RoutingSupport.normalize(host, schemePortResolver);
        if (target.getPort() < 0) {
            throw new ProtocolException("Unroutable protocol scheme: " + target);
        }
        final boolean secure = target.getSchemeName().equalsIgnoreCase("https");
        if (proxy == null) {
            return new HttpRoute(target, determineLocalAddress(target, context), secure);
        } else {
            return new HttpRoute(target, determineLocalAddress(proxy, context), proxy, secure);
        }
    }

    /**
     * This implementation returns null.
     *
     * @throws HttpException may be thrown if overridden
     */
    protected HttpHost determineProxy(
            final HttpHost target,
            final HttpContext context) throws HttpException {
        return null;
    }

    /**
     * This implementation returns null.
     *
     * @throws HttpException may be thrown if overridden
     */
    protected InetAddress determineLocalAddress(
            final HttpHost firstHop,
            final HttpContext context) throws HttpException {
        return null;
    }

}
