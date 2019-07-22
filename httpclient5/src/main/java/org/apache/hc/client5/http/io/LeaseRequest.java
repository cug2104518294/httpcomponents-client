package org.apache.hc.client5.http.io;

import org.apache.hc.core5.concurrent.Cancellable;
import org.apache.hc.core5.util.Timeout;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Represents a request for a {@link ConnectionEndpoint} whose life cycle
 * is managed by a connection manager.
 *
 * @since 5.0
 */
public interface LeaseRequest extends Cancellable {

    /**
     * Returns {@link ConnectionEndpoint} within a given time.
     * This method will block until a connection becomes available,
     * the timeout expires, or the connection manager is shut down.
     * Timeouts are handled with millisecond precision.
     * <p>
     * If {@link #cancel()} is called while this is blocking or
     * before this began, an {@link InterruptedException} will
     * be thrown.
     *
     * @param timeout the operation timeout.
     * @return a connection that can be used to communicate
     * along the given route
     * @throws TimeoutException     in case of a timeout
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    ConnectionEndpoint get(Timeout timeout)
            throws InterruptedException, ExecutionException, TimeoutException;

}