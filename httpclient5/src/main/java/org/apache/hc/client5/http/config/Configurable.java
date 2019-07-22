
package org.apache.hc.client5.http.config;

/**
 * Configuration interface for HTTP requests.
 *
 * @since 4.3
 */
public interface Configurable {

    /**
     * Returns actual request configuration.
     */
    RequestConfig getConfig();

}
