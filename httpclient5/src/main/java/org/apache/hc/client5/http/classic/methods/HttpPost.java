package org.apache.hc.client5.http.classic.methods;

import java.net.URI;

/**
 * HTTP POST method.
 */
public class HttpPost extends HttpUriRequestBase {

    private static final long serialVersionUID = 1L;

    public final static String METHOD_NAME = "POST";

    /**
     * Creates a new instance initialized with the given URI.
     *
     * @param uri a non-null request URI.
     * @throws IllegalArgumentException if the uri is null.
     */
    public HttpPost(final URI uri) {
        super(METHOD_NAME, uri);
    }

    /**
     * Creates a new instance initialized with the given URI.
     *
     * @param uri a non-null request URI.
     * @throws IllegalArgumentException if the uri is invalid.
     */
    public HttpPost(final String uri) {
        this(URI.create(uri));
    }

}
