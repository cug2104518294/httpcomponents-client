package org.apache.hc.client5.http.async.methods;

import org.apache.hc.client5.http.config.Configurable;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.message.BasicHttpRequest;

import java.net.URI;

/**
 * HTTP request message with a custom configuration.
 *
 * @since 5.0
 */
public class ConfigurableHttpRequest extends BasicHttpRequest implements Configurable {

    private static final long serialVersionUID = 1L;
    private RequestConfig requestConfig;

    public ConfigurableHttpRequest(final String method, final String path) {
        super(method, path);
    }

    public ConfigurableHttpRequest(final String method, final HttpHost host, final String path) {
        super(method, host, path);
    }

    public ConfigurableHttpRequest(final String method, final URI requestUri) {
        super(method, requestUri);
    }

    @Override
    public RequestConfig getConfig() {
        return requestConfig;
    }

    public void setConfig(final RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }

}

