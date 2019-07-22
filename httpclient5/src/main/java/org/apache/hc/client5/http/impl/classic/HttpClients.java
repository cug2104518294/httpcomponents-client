package org.apache.hc.client5.http.impl.classic;

import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;

/**
 * Factory methods for {@link CloseableHttpClient} instances.
 *
 * @since 4.3
 */
public final class HttpClients {

    private HttpClients() {
        super();
    }

    /**
     * Creates builder object for construction of custom
     * {@link CloseableHttpClient} instances.
     */
    public static HttpClientBuilder custom() {
        return HttpClientBuilder.create();
    }

    /**
     * Creates {@link CloseableHttpClient} instance with default
     * configuration.
     */
    public static CloseableHttpClient createDefault() {
        return HttpClientBuilder.create().build();
    }

    /**
     * Creates {@link CloseableHttpClient} instance with default
     * configuration based on system properties.
     */
    public static CloseableHttpClient createSystem() {
        return HttpClientBuilder.create().useSystemProperties().build();
    }

    /**
     * Creates {@link CloseableHttpClient} instance that implements
     * the most basic HTTP protocol support.
     */
    public static MinimalHttpClient createMinimal() {
        return new MinimalHttpClient(new PoolingHttpClientConnectionManager());
    }

    /**
     * Creates {@link CloseableHttpClient} instance that implements
     * the most basic HTTP protocol support.
     */
    public static MinimalHttpClient createMinimal(final HttpClientConnectionManager connManager) {
        return new MinimalHttpClient(connManager);
    }

}
