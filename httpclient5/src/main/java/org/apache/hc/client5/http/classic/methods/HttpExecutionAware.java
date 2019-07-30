package org.apache.hc.client5.http.classic.methods;

import org.apache.hc.core5.concurrent.Cancellable;

/**
 * Interface to be implemented by any object that wishes to be notified of blocking I/O operations
 * that could be cancelled.
 *
 * @since 4.3
 */
public interface HttpExecutionAware {

    boolean isAborted();

    /**
     * Sets {@link Cancellable} for the ongoing operation.
     *
     * @param cancellable {@link Cancellable} for the ongoing operation.
     */
    void setCancellable(Cancellable cancellable);

}
