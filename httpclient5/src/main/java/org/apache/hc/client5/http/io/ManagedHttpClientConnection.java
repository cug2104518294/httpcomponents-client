package org.apache.hc.client5.http.io;

import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.http.io.HttpClientConnection;

import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.Socket;

/**
 * Represents a managed connection whose state and life cycle is managed by
 * a connection manager. This interface extends {@link HttpClientConnection}
 * with methods to bind the connection to an arbitrary socket and
 * to obtain SSL session details.
 *
 * @since 4.3
 */
@Internal
public interface ManagedHttpClientConnection extends HttpClientConnection {

    /**
     * Binds this connection to the given socket. The connection
     * is considered open if it is bound and the underlying socket
     * is connection to a remote host.
     *
     * @param socket the socket to bind the connection to.
     */
    void bind(Socket socket) throws IOException;

    /**
     * Returns the underlying socket.
     */
    Socket getSocket();

    /**
     * Obtains the SSL session of the underlying connection, if any.
     * If this connection is open, and the underlying socket is an
     * {@link javax.net.ssl.SSLSocket SSLSocket}, the SSL session of
     * that socket is obtained. This is a potentially blocking operation.
     *
     * @return the underlying SSL session if available,
     * {@code null} otherwise
     */
    @Override
    SSLSession getSSLSession();

    /**
     * Puts the connection into idle mode.
     *
     * @since 5.0
     */
    void passivate();

    /**
     * Restores the connection from idle mode.
     *
     * @since 5.0
     */
    void activate();

}
