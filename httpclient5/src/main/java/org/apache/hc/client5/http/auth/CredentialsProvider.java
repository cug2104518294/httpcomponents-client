package org.apache.hc.client5.http.auth;

import org.apache.hc.core5.http.protocol.HttpContext;

/**
 * Provider of authentication credentials.
 * <p>
 * Implementations of this interface must be thread-safe. Access to shared
 * data must be synchronized as methods of this interface may be executed
 * from multiple threads.
 * </p>
 *
 * @since 4.0
 */
public interface CredentialsProvider {

    /**
     * Returns {@link Credentials credentials} for the given authentication scope,
     * if available.
     *
     * @param authscope the {@link AuthScope authentication scope}
     * @param context   the {@link HttpContext http context}
     * @return the credentials
     * @since 5.0
     */
    Credentials getCredentials(AuthScope authscope, HttpContext context);

}
