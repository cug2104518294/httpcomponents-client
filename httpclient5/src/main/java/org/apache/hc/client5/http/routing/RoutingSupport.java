package org.apache.hc.client5.http.routing;

import org.apache.hc.client5.http.SchemePortResolver;
import org.apache.hc.client5.http.impl.DefaultSchemePortResolver;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.net.URIAuthority;

public final class RoutingSupport {

    public static HttpHost determineHost(final HttpRequest request) throws HttpException {
        if (request == null) {
            return null;
        }
        final URIAuthority authority = request.getAuthority();
        if (authority != null) {
            final String scheme = request.getScheme();
            if (scheme == null) {
                throw new ProtocolException("Protocol scheme is not specified");
            }
            return new HttpHost(scheme, authority);
        }
        return null;
    }

    public static HttpHost normalize(final HttpHost host, final SchemePortResolver schemePortResolver) {
        if (host == null) {
            return null;
        }
        if (host.getPort() < 0) {
            final int port = (schemePortResolver != null ? schemePortResolver : DefaultSchemePortResolver.INSTANCE).resolve(host);
            if (port > 0) {
                return new HttpHost(host.getSchemeName(), host.getHostName(), port);
            }
        }
        return host;
    }

}
