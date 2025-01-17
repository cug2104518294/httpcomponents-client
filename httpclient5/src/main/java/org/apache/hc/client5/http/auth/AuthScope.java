package org.apache.hc.client5.http.auth;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.LangUtils;

import java.util.Locale;

/**
 * {@code AuthScope} represents an authentication scope consisting of
 * an application protocol, a host name, a port number, a realm name
 * and an authentication scheme name.
 *
 * @since 4.0
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class AuthScope {

    private final String protocol;
    private final String host;
    private final int port;
    private final String realm;
    private final String authScheme;

    /**
     * Defines auth scope with the given {@code protocol}, {@code host}, {@code port},
     * {@code realm}, and {@code schemeName}.
     *
     * @param protocol   application protocol. May be {@code null} if applies
     *                   to any protocol.
     * @param host       authentication host. May be {@code null} if applies
     *                   to any host.
     * @param port       authentication port. May be {@code -1} if applies
     *                   to any port of the host.
     * @param realm      authentication realm. May be {@code null} if applies
     *                   to any realm on the host.
     * @param authScheme authentication scheme. May be {@code null} if applies
     *                   to any authScheme supported by the host.
     */
    public AuthScope(
            final String protocol,
            final String host,
            final int port,
            final String realm,
            final String authScheme) {
        this.protocol = protocol != null ? protocol.toLowerCase(Locale.ROOT) : null;
        this.host = host != null ? host.toLowerCase(Locale.ROOT) : null;
        this.port = port >= 0 ? port : -1;
        this.realm = realm;
        this.authScheme = authScheme != null ? authScheme.toUpperCase(Locale.ROOT) : null;
    }

    /**
     * Defines auth scope for a specific host of origin.
     *
     * @param origin     host of origin
     * @param realm      authentication realm. May be {@code null} if applies
     *                   to any realm on the host.
     * @param schemeName authentication authScheme. May be {@code null} if applies
     *                   to any authScheme supported by the host.
     * @since 4.2
     */
    public AuthScope(
            final HttpHost origin,
            final String realm,
            final String schemeName) {
        Args.notNull(origin, "Host");
        this.protocol = origin.getSchemeName().toLowerCase(Locale.ROOT);
        this.host = origin.getHostName().toLowerCase(Locale.ROOT);
        this.port = origin.getPort() >= 0 ? origin.getPort() : -1;
        this.realm = realm;
        this.authScheme = schemeName != null ? schemeName.toUpperCase(Locale.ROOT) : null;
    }

    /**
     * Defines auth scope for a specific host of origin.
     *
     * @param origin host of origin
     * @since 4.2
     */
    public AuthScope(final HttpHost origin) {
        this(origin, null, null);
    }

    /**
     * Defines auth scope with the given {@code host} and {@code port}.
     *
     * @param host authentication host. May be {@code null} if applies
     *             to any host.
     * @param port authentication port. May be {@code -1} if applies
     *             to any port of the host.
     */
    public AuthScope(final String host, final int port) {
        this(null, host, port, null, null);
    }

    /**
     * Creates a copy of the given credentials scope.
     */
    public AuthScope(final AuthScope authscope) {
        super();
        Args.notNull(authscope, "Scope");
        this.protocol = authscope.getProtocol();
        this.host = authscope.getHost();
        this.port = authscope.getPort();
        this.realm = authscope.getRealm();
        this.authScheme = authscope.getAuthScheme();
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getRealm() {
        return this.realm;
    }

    public String getAuthScheme() {
        return this.authScheme;
    }

    /**
     * Tests if the authentication scopes match.
     *
     * @return the match factor. Negative value signifies no match.
     * Non-negative signifies a match. The greater the returned value
     * the closer the match.
     */
    public int match(final AuthScope that) {
        int factor = 0;
        if (LangUtils.equals(this.authScheme, that.authScheme)) {
            factor += 1;
        } else {
            if (this.authScheme != null && that.authScheme != null) {
                return -1;
            }
        }
        if (LangUtils.equals(this.realm, that.realm)) {
            factor += 2;
        } else {
            if (this.realm != null && that.realm != null) {
                return -1;
            }
        }
        if (this.port == that.port) {
            factor += 4;
        } else {
            if (this.port != -1 && that.port != -1) {
                return -1;
            }
        }
        if (LangUtils.equals(this.protocol, that.protocol)) {
            factor += 8;
        } else {
            if (this.protocol != null && that.protocol != null) {
                return -1;
            }
        }
        if (LangUtils.equals(this.host, that.host)) {
            factor += 16;
        } else {
            if (this.host != null && that.host != null) {
                return -1;
            }
        }
        return factor;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof AuthScope) {
            final AuthScope that = (AuthScope) obj;
            return LangUtils.equals(this.protocol, that.protocol)
                    && LangUtils.equals(this.host, that.host)
                    && this.port == that.port
                    && LangUtils.equals(this.realm, that.realm)
                    && LangUtils.equals(this.authScheme, that.authScheme);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = LangUtils.HASH_SEED;
        hash = LangUtils.hashCode(hash, this.protocol);
        hash = LangUtils.hashCode(hash, this.host);
        hash = LangUtils.hashCode(hash, this.port);
        hash = LangUtils.hashCode(hash, this.realm);
        hash = LangUtils.hashCode(hash, this.authScheme);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        if (this.authScheme != null) {
            buffer.append(this.authScheme);
        } else {
            buffer.append("<any auth scheme>");
        }
        buffer.append(' ');
        if (this.realm != null) {
            buffer.append('\'');
            buffer.append(this.realm);
            buffer.append('\'');
        } else {
            buffer.append("<any realm>");
        }
        buffer.append(' ');
        if (this.protocol != null) {
            buffer.append(this.protocol);
        } else {
            buffer.append("<any protocol>");
        }
        buffer.append("://");
        if (this.host != null) {
            buffer.append(this.host);
        } else {
            buffer.append("<any host>");
        }
        buffer.append(':');
        if (this.port >= 0) {
            buffer.append(this.port);
        } else {
            buffer.append("<any port>");
        }
        return buffer.toString();
    }

}
