package org.apache.hc.client5.http;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpHost;

/**
 * Strategy for default port resolution for protocol schemes.
 *
 * @since 4.3
 */
@Contract(threading = ThreadingBehavior.STATELESS)
public interface SchemePortResolver {

    /**
     * Returns the actual port for the host based on the protocol scheme.
     */
    int resolve(HttpHost host);

}
