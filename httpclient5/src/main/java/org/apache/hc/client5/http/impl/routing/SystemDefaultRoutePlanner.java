package org.apache.hc.client5.http.impl.routing;

import org.apache.hc.client5.http.SchemePortResolver;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.net.*;
import java.util.List;

/**
 * {@link org.apache.hc.client5.http.routing.HttpRoutePlanner} implementation
 * based on {@link ProxySelector}. By default, this class will pick up
 * the proxy settings of the JVM, either from system properties
 * or from the browser running the application.
 *
 * @since 4.3
 */
@Contract(threading = ThreadingBehavior.STATELESS)
public class SystemDefaultRoutePlanner extends DefaultRoutePlanner {

    private final ProxySelector proxySelector;

    /**
     * @param proxySelector the proxy selector, or {@code null} for the system default
     */
    public SystemDefaultRoutePlanner(
            final SchemePortResolver schemePortResolver,
            final ProxySelector proxySelector) {
        super(schemePortResolver);
        this.proxySelector = proxySelector;
    }

    /**
     * @param proxySelector the proxy selector, or {@code null} for the system default
     */
    public SystemDefaultRoutePlanner(final ProxySelector proxySelector) {
        this(null, proxySelector);
    }

    @Override
    protected HttpHost determineProxy(final HttpHost target, final HttpContext context) throws HttpException {
        final URI targetURI;
        try {
            targetURI = new URI(target.toURI());
        } catch (final URISyntaxException ex) {
            throw new HttpException("Cannot convert host to URI: " + target, ex);
        }
        ProxySelector proxySelectorInstance = this.proxySelector;
        if (proxySelectorInstance == null) {
            proxySelectorInstance = ProxySelector.getDefault();
        }
        if (proxySelectorInstance == null) {
            //The proxy selector can be "unset", so we must be able to deal with a null selector
            return null;
        }
        final List<Proxy> proxies = proxySelectorInstance.select(targetURI);
        final Proxy p = chooseProxy(proxies);
        HttpHost result = null;
        if (p.type() == Proxy.Type.HTTP) {
            // convert the socket address to an HttpHost
            if (!(p.address() instanceof InetSocketAddress)) {
                throw new HttpException("Unable to handle non-Inet proxy address: " + p.address());
            }
            final InetSocketAddress isa = (InetSocketAddress) p.address();
            // assume default scheme (http)
            result = new HttpHost(null, isa.getAddress(), isa.getHostString(), isa.getPort());
        }

        return result;
    }

    private Proxy chooseProxy(final List<Proxy> proxies) {
        Proxy result = null;
        // check the list for one we can use
        for (int i = 0; (result == null) && (i < proxies.size()); i++) {
            final Proxy p = proxies.get(i);
            switch (p.type()) {

                case DIRECT:
                case HTTP:
                    result = p;
                    break;

                case SOCKS:
                    // SOCKS hosts are not handled on the route level.
                    // The socket may make use of the SOCKS host though.
                    break;
            }
        }
        if (result == null) {
            //@@@ log as warning or info that only a socks proxy is available?
            // result can only be null if all proxies are socks proxies
            // socks proxies are not handled on the route planning level
            result = Proxy.NO_PROXY;
        }
        return result;
    }

}
