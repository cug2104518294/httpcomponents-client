package org.apache.hc.client5.http.impl.auth;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.CredentialsStore;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Args;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of {@link CredentialsStore}.
 *
 * @since 4.0
 */
@Contract(threading = ThreadingBehavior.SAFE)
public class BasicCredentialsProvider implements CredentialsStore {

    private final ConcurrentHashMap<AuthScope, Credentials> credMap;

    /**
     * Default constructor.
     */
    public BasicCredentialsProvider() {
        super();
        this.credMap = new ConcurrentHashMap<>();
    }

    @Override
    public void setCredentials(
            final AuthScope authscope,
            final Credentials credentials) {
        Args.notNull(authscope, "Authentication scope");
        credMap.put(authscope, credentials);
    }

    /**
     * Find matching {@link Credentials credentials} for the given authentication scope.
     *
     * @param map       the credentials hash map
     * @param authscope the {@link AuthScope authentication scope}
     * @return the credentials
     */
    private static Credentials matchCredentials(
            final Map<AuthScope, Credentials> map,
            final AuthScope authscope) {
        // see if we get a direct hit
        Credentials creds = map.get(authscope);
        if (creds == null) {
            // Nope.
            // Do a full scan
            int bestMatchFactor = -1;
            AuthScope bestMatch = null;
            for (final AuthScope current : map.keySet()) {
                final int factor = authscope.match(current);
                if (factor > bestMatchFactor) {
                    bestMatchFactor = factor;
                    bestMatch = current;
                }
            }
            if (bestMatch != null) {
                creds = map.get(bestMatch);
            }
        }
        return creds;
    }

    @Override
    public Credentials getCredentials(final AuthScope authscope,
                                      final HttpContext httpContext) {
        Args.notNull(authscope, "Authentication scope");
        return matchCredentials(this.credMap, authscope);
    }

    @Override
    public void clear() {
        this.credMap.clear();
    }

    @Override
    public String toString() {
        return credMap.toString();
    }

}
