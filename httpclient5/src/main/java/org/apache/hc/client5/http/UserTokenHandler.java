package org.apache.hc.client5.http;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.protocol.HttpContext;

/**
 * A handler for determining if the given execution context is user specific
 * or not. The token object returned by this handler is expected to uniquely
 * identify the current user if the context is user specific or to be
 * {@code null} if the context does not contain any resources or details
 * specific to the current user.
 * <p>
 * The user token will be used to ensure that user specific resources will not
 * be shared with or reused by other users.
 * </p>
 *
 * @since 4.0
 */
@Contract(threading = ThreadingBehavior.STATELESS)
public interface UserTokenHandler {

    /**
     * The token object returned by this method is expected to uniquely
     * identify the current user if the context is user specific or to be
     * {@code null} if it is not.
     *
     * @param route   HTTP route
     * @param context the execution context
     * @return user token that uniquely identifies the user or
     * {@code null} if the context is not user specific.
     */
    Object getUserToken(HttpRoute route, HttpContext context);

}
