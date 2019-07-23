package org.apache.hc.client5.http.async.methods;

import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.Methods;

import java.net.URI;

/**
 * Common HTTP methods using {@link SimpleHttpRequest} as a HTTP request message representation.
 *
 * @since 5.0
 */
public enum SimpleHttpRequests {

    DELETE {
        @Override
        public SimpleHttpRequest create(final URI uri) {
            return new SimpleHttpRequest(Methods.DELETE, uri);
        }

        @Override
        public SimpleHttpRequest create(final HttpHost host, final String path) {
            return new SimpleHttpRequest(Methods.DELETE, host, path);
        }
    },

    GET {
        @Override
        public SimpleHttpRequest create(final URI uri) {
            return new SimpleHttpRequest(Methods.GET, uri);
        }

        @Override
        public SimpleHttpRequest create(final HttpHost host, final String path) {
            return new SimpleHttpRequest(Methods.GET, host, path);
        }
    },

    HEAD {
        @Override
        public SimpleHttpRequest create(final URI uri) {
            return new SimpleHttpRequest(Methods.HEAD, uri);
        }

        @Override
        public SimpleHttpRequest create(final HttpHost host, final String path) {
            return new SimpleHttpRequest(Methods.HEAD, host, path);
        }
    },

    OPTIONS {
        @Override
        public SimpleHttpRequest create(final URI uri) {
            return new SimpleHttpRequest(Methods.OPTIONS, uri);
        }

        @Override
        public SimpleHttpRequest create(final HttpHost host, final String path) {
            return new SimpleHttpRequest(Methods.OPTIONS, host, path);
        }
    },

    PATCH {
        @Override
        public SimpleHttpRequest create(final URI uri) {
            return new SimpleHttpRequest(Methods.PATCH, uri);
        }

        @Override
        public SimpleHttpRequest create(final HttpHost host, final String path) {
            return new SimpleHttpRequest(Methods.PATCH, host, path);
        }
    },

    POST {
        @Override
        public SimpleHttpRequest create(final URI uri) {
            return new SimpleHttpRequest(Methods.POST, uri);
        }

        @Override
        public SimpleHttpRequest create(final HttpHost host, final String path) {
            return new SimpleHttpRequest(Methods.POST, host, path);
        }
    },

    PUT {
        @Override
        public SimpleHttpRequest create(final URI uri) {
            return new SimpleHttpRequest(Methods.PUT, uri);
        }

        @Override
        public SimpleHttpRequest create(final HttpHost host, final String path) {
            return new SimpleHttpRequest(Methods.PUT, host, path);
        }
    },

    TRACE {
        @Override
        public SimpleHttpRequest create(final URI uri) {
            return new SimpleHttpRequest(Methods.TRACE, uri);
        }

        @Override
        public SimpleHttpRequest create(final HttpHost host, final String path) {
            return new SimpleHttpRequest(Methods.TRACE, host, path);
        }
    };

    /**
     * Creates a request object of the exact subclass of {@link SimpleHttpRequest}.
     *
     * @param uri a non-null URI String.
     * @return a new subclass of SimpleHttpRequest
     */
    public SimpleHttpRequest create(final String uri) {
        return create(URI.create(uri));
    }

    /**
     * Creates a request object of the exact subclass of {@link SimpleHttpRequest}.
     *
     * @param uri a non-null URI.
     * @return a new subclass of SimpleHttpRequest
     */
    public abstract SimpleHttpRequest create(URI uri);

    /**
     * Creates a request object of the exact subclass of {@link SimpleHttpRequest}.
     *
     * @param host HTTP host.
     * @param path request path.
     * @return a new subclass of SimpleHttpRequest
     */
    public abstract SimpleHttpRequest create(final HttpHost host, final String path);

}
