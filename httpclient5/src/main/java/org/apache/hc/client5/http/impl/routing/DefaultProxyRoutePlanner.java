package org.apache.hc.client5.http.impl.routing;

import org.apache.hc.client5.http.SchemePortResolver;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Args;

/**
 * Implementation of an {@link org.apache.hc.client5.http.routing.HttpRoutePlanner}
 * that routes requests through a default proxy.
 *
 * @since 4.3
 */
@Contract(threading = ThreadingBehavior.STATELESS)
public class DefaultProxyRoutePlanner extends DefaultRoutePlanner {

    private final HttpHost proxy;

    public DefaultProxyRoutePlanner(final HttpHost proxy, final SchemePortResolver schemePortResolver) {
        super(schemePortResolver);
        this.proxy = Args.notNull(proxy, "Proxy host");
    }

    public DefaultProxyRoutePlanner(final HttpHost proxy) {
        this(proxy, null);
    }

    @Override
    protected HttpHost determineProxy(
            final HttpHost target,
            final HttpContext context) throws HttpException {
        return proxy;
    }

}
