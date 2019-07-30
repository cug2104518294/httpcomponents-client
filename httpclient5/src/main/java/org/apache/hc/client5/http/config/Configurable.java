
package org.apache.hc.client5.http.config;

/**
 * Configuration interface for HTTP requests.
 *
 * 请求的配置参数类
 *
 * @since 4.3
 */
public interface Configurable {

    /**
     * Returns actual request configuration.
     */
    RequestConfig getConfig();

}
