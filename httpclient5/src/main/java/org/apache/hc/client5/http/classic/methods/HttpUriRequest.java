package org.apache.hc.client5.http.classic.methods;

import org.apache.hc.client5.http.config.Configurable;
import org.apache.hc.core5.http.ClassicHttpRequest;

/**
 * Extended version of the {@link ClassicHttpRequest} interface that provides
 * convenience methods to access request properties such as request URI
 * and method type.
 *
 * @since 4.0
 */
public interface HttpUriRequest extends ClassicHttpRequest, Configurable {

    /**
     * Aborts execution of the request.
     *
     * @throws UnsupportedOperationException if the abort operation
     *                                       is not supported / cannot be implemented.
     */
    void abort() throws UnsupportedOperationException;

    /**
     * Tests if the request execution has been aborted.
     *
     * @return {@code true} if the request execution has been aborted,
     * {@code false} otherwise.
     */
    boolean isAborted();

}
