package org.apache.hc.client5.http;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.LangUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Connection route definition for HTTP requests.
 * <p>
 * 指示对方服务器地址。HttpRoutePlanner用来创建HttpRoute。
 * 后者代表客户端request的对端服务器，主要包含rout的host以及proxy信息
 *
 * @since 4.0
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public final class HttpRoute implements RouteInfo, Cloneable {

    /**
     * The target host to connect to.
     */
    private final HttpHost targetHost;

    /**
     * The local address to connect from.
     * {@code null} indicates that the default should be used.
     */
    private final InetAddress localAddress;

    /**
     * The proxy servers, if any. Never null.
     */
    private final List<HttpHost> proxyChain;

    /**
     * Whether the the route is tunnelled through the proxy.
     */
    private final TunnelType tunnelled;

    /**
     * Whether the route is layered.
     */
    private final LayerType layered;

    /**
     * Whether the route is (supposed to be) secure.
     */
    private final boolean secure;

    private HttpRoute(final HttpHost targetHost, final InetAddress local, final List<HttpHost> proxies,
                      final boolean secure, final TunnelType tunnelled, final LayerType layered) {
        Args.notNull(targetHost, "Target host");
        Args.notNegative(targetHost.getPort(), "Target port");
        this.targetHost = targetHost;
        this.localAddress = local;
        if (proxies != null && !proxies.isEmpty()) {
            this.proxyChain = new ArrayList<>(proxies);
        } else {
            this.proxyChain = null;
        }
        if (tunnelled == TunnelType.TUNNELLED) {
            Args.check(this.proxyChain != null, "Proxy required if tunnelled");
        }
        this.secure = secure;
        this.tunnelled = tunnelled != null ? tunnelled : TunnelType.PLAIN;
        this.layered = layered != null ? layered : LayerType.PLAIN;
    }

    /**
     * Creates a new route with all attributes specified explicitly.
     *
     * @param target    the host to which to route
     * @param local     the local address to route from, or
     *                  {@code null} for the default
     * @param proxies   the proxy chain to use, or
     *                  {@code null} for a direct route
     * @param secure    {@code true} if the route is (to be) secure,
     *                  {@code false} otherwise
     * @param tunnelled the tunnel type of this route
     * @param layered   the layering type of this route
     */
    public HttpRoute(final HttpHost target, final InetAddress local, final HttpHost[] proxies,
                     final boolean secure, final TunnelType tunnelled, final LayerType layered) {
        this(target, local, proxies != null ? Arrays.asList(proxies) : null,
                secure, tunnelled, layered);
    }

    /**
     * Creates a new route with at most one proxy.
     *
     * @param target    the host to which to route
     * @param local     the local address to route from, or
     *                  {@code null} for the default
     * @param proxy     the proxy to use, or
     *                  {@code null} for a direct route
     * @param secure    {@code true} if the route is (to be) secure,
     *                  {@code false} otherwise
     * @param tunnelled {@code true} if the route is (to be) tunnelled
     *                  via the proxy,
     *                  {@code false} otherwise
     * @param layered   {@code true} if the route includes a
     *                  layered protocol,
     *                  {@code false} otherwise
     */
    public HttpRoute(final HttpHost target, final InetAddress local, final HttpHost proxy,
                     final boolean secure, final TunnelType tunnelled, final LayerType layered) {
        this(target, local, proxy != null ? Collections.singletonList(proxy) : null,
                secure, tunnelled, layered);
    }

    /**
     * Creates a new direct route.
     * That is a route without a proxy.
     *
     * @param target the host to which to route
     * @param local  the local address to route from, or
     *               {@code null} for the default
     * @param secure {@code true} if the route is (to be) secure,
     *               {@code false} otherwise
     */
    public HttpRoute(final HttpHost target, final InetAddress local, final boolean secure) {
        this(target, local, Collections.<HttpHost>emptyList(), secure,
                TunnelType.PLAIN, LayerType.PLAIN);
    }

    /**
     * Creates a new direct insecure route.
     *
     * @param target the host to which to route
     */
    public HttpRoute(final HttpHost target) {
        this(target, null, Collections.<HttpHost>emptyList(), false,
                TunnelType.PLAIN, LayerType.PLAIN);
    }

    /**
     * Creates a new route through a proxy.
     * When using this constructor, the {@code proxy} MUST be given.
     * For convenience, it is assumed that a secure connection will be
     * layered over a tunnel through the proxy.
     *
     * @param target the host to which to route
     * @param local  the local address to route from, or
     *               {@code null} for the default
     * @param proxy  the proxy to use
     * @param secure {@code true} if the route is (to be) secure,
     *               {@code false} otherwise
     */
    public HttpRoute(final HttpHost target, final InetAddress local, final HttpHost proxy,
                     final boolean secure) {
        this(target, local, Collections.singletonList(Args.notNull(proxy, "Proxy host")), secure,
                secure ? TunnelType.TUNNELLED : TunnelType.PLAIN,
                secure ? LayerType.LAYERED : LayerType.PLAIN);
    }

    /**
     * Creates a new plain route through a proxy.
     *
     * @param target the host to which to route
     * @param proxy  the proxy to use
     * @since 4.3
     */
    public HttpRoute(final HttpHost target, final HttpHost proxy) {
        this(target, null, proxy, false);
    }

    @Override
    public final HttpHost getTargetHost() {
        return this.targetHost;
    }

    @Override
    public final InetAddress getLocalAddress() {
        return this.localAddress;
    }

    public final InetSocketAddress getLocalSocketAddress() {
        return this.localAddress != null ? new InetSocketAddress(this.localAddress, 0) : null;
    }

    @Override
    public final int getHopCount() {
        return proxyChain != null ? proxyChain.size() + 1 : 1;
    }

    @Override
    public final HttpHost getHopTarget(final int hop) {
        Args.notNegative(hop, "Hop index");
        final int hopcount = getHopCount();
        Args.check(hop < hopcount, "Hop index exceeds tracked route length");
        if (hop < hopcount - 1) {
            return this.proxyChain.get(hop);
        } else {
            return this.targetHost;
        }
    }

    @Override
    public final HttpHost getProxyHost() {
        return proxyChain != null && !this.proxyChain.isEmpty() ? this.proxyChain.get(0) : null;
    }

    @Override
    public final TunnelType getTunnelType() {
        return this.tunnelled;
    }

    @Override
    public final boolean isTunnelled() {
        return (this.tunnelled == TunnelType.TUNNELLED);
    }

    @Override
    public final LayerType getLayerType() {
        return this.layered;
    }

    @Override
    public final boolean isLayered() {
        return (this.layered == LayerType.LAYERED);
    }

    @Override
    public final boolean isSecure() {
        return this.secure;
    }

    /**
     * Compares this route to another.
     *
     * @param obj the object to compare with
     * @return {@code true} if the argument is the same route,
     * {@code false}
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof HttpRoute) {
            final HttpRoute that = (HttpRoute) obj;
            return
                    // Do the cheapest tests first
                    (this.secure == that.secure) &&
                            (this.tunnelled == that.tunnelled) &&
                            (this.layered == that.layered) &&
                            LangUtils.equals(this.targetHost, that.targetHost) &&
                            LangUtils.equals(this.localAddress, that.localAddress) &&
                            LangUtils.equals(this.proxyChain, that.proxyChain);
        }
        return false;
    }


    /**
     * Generates a hash code for this route.
     *
     * @return the hash code
     */
    @Override
    public final int hashCode() {
        int hash = LangUtils.HASH_SEED;
        hash = LangUtils.hashCode(hash, this.targetHost);
        hash = LangUtils.hashCode(hash, this.localAddress);
        if (this.proxyChain != null) {
            for (final HttpHost element : this.proxyChain) {
                hash = LangUtils.hashCode(hash, element);
            }
        }
        hash = LangUtils.hashCode(hash, this.secure);
        hash = LangUtils.hashCode(hash, this.tunnelled);
        hash = LangUtils.hashCode(hash, this.layered);
        return hash;
    }

    /**
     * Obtains a description of this route.
     *
     * @return a human-readable representation of this route
     */
    @Override
    public final String toString() {
        final StringBuilder cab = new StringBuilder(50 + getHopCount() * 30);
        if (this.localAddress != null) {
            cab.append(this.localAddress);
            cab.append("->");
        }
        cab.append('{');
        if (this.tunnelled == TunnelType.TUNNELLED) {
            cab.append('t');
        }
        if (this.layered == LayerType.LAYERED) {
            cab.append('l');
        }
        if (this.secure) {
            cab.append('s');
        }
        cab.append("}->");
        if (this.proxyChain != null) {
            for (final HttpHost aProxyChain : this.proxyChain) {
                cab.append(aProxyChain);
                cab.append("->");
            }
        }
        cab.append(this.targetHost);
        return cab.toString();
    }

    // default implementation of clone() is sufficient
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
