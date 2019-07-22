package org.apache.hc.client5.http.io;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Connection operator that performs connection connect and upgrade operations.
 *
 * @since 4.4
 */
@Contract(threading = ThreadingBehavior.STATELESS)
@Internal
public interface HttpClientConnectionOperator {

    /**
     * Connect the given managed connection to the remote endpoint.
     *
     * @param conn           the managed connection.
     * @param host           the address of the opposite endpoint.
     * @param localAddress   the address of the local endpoint.
     * @param connectTimeout the timeout of the connect operation.
     * @param socketConfig   the socket configuration.
     * @param context        the execution context.
     */
    void connect(
            ManagedHttpClientConnection conn,
            HttpHost host,
            InetSocketAddress localAddress,
            TimeValue connectTimeout,
            SocketConfig socketConfig,
            HttpContext context) throws IOException;

    /**
     * Upgrades transport security of the given managed connection
     * by using the TLS security protocol.
     *
     * @param conn    the managed connection.
     * @param host    the address of the opposite endpoint with TLS security.
     * @param context the execution context.
     */
    void upgrade(
            ManagedHttpClientConnection conn,
            HttpHost host,
            HttpContext context) throws IOException;

}
