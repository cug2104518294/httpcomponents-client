package org.apache.hc.client5.http.routing;

import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.protocol.HttpContext;

/**
 * Encapsulates logic to compute a {@link HttpRoute} to a target host.
 * Implementations may for example be based on parameters, or on the
 * standard Java system properties.
 * <p>
 * Implementations of this interface must be thread-safe. Access to shared
 * data must be synchronized as methods of this interface may be executed
 * from multiple threads.
 * </p>
 *
 * @since 4.0
 */
@Contract(threading = ThreadingBehavior.STATELESS)
public interface HttpRoutePlanner {

    /**
     * Determines the route for the given host.
     *
     * @param target    the target host for the request.
     * @param context   the context to use for the subsequent execution.
     *                  Implementations may accept {@code null}.
     *
     * @return  the route that the request should take
     *
     * @throws HttpException    in case of a problem
     */
    HttpRoute determineRoute(HttpHost target, HttpContext context) throws HttpException;

}
