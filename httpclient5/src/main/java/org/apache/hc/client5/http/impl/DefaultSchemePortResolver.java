package org.apache.hc.client5.http.impl;

import org.apache.hc.client5.http.SchemePortResolver;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.util.Args;

/**
 * Default {@link SchemePortResolver}.
 *
 * @since 4.3
 */
@Contract(threading = ThreadingBehavior.STATELESS)
public class DefaultSchemePortResolver implements SchemePortResolver {

    public static final DefaultSchemePortResolver INSTANCE = new DefaultSchemePortResolver();

    @Override
    public int resolve(final HttpHost host) {
        Args.notNull(host, "HTTP host");
        final int port = host.getPort();
        if (port > 0) {
            return port;
        }
        final String name = host.getSchemeName();
        if (URIScheme.HTTP.same(name)) {
            return 80;
        } else if (URIScheme.HTTPS.same(name)) {
            return 443;
        } else {
            return -1;
        }
    }

}
