package org.apache.hc.client5.http.impl;

/**
 * Signals that the connection has been shut down or released back to the
 * the connection pool
 *
 * @since 4.1
 */
public class ConnectionShutdownException extends IllegalStateException {

    private static final long serialVersionUID = 5868657401162844497L;

    /**
     * Creates a new ConnectionShutdownException with a {@code null} detail message.
     */
    public ConnectionShutdownException() {
        super();
    }

}
