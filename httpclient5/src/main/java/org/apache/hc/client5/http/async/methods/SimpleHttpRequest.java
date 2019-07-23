package org.apache.hc.client5.http.async.methods;

import org.apache.hc.core5.http.*;
import org.apache.hc.core5.util.Args;

import java.net.URI;
import java.util.Iterator;

/**
 * HTTP request that can enclose a body represented as a simple text string or an array of bytes.
 *
 * @see SimpleBody
 * @since 5.0
 */
public final class SimpleHttpRequest extends ConfigurableHttpRequest {

    private static final long serialVersionUID = 1L;
    private SimpleBody body;

    public static SimpleHttpRequest copy(final HttpRequest original) {
        Args.notNull(original, "HTTP request");
        final SimpleHttpRequest copy = new SimpleHttpRequest(original.getMethod(), original.getRequestUri());
        copy.setVersion(original.getVersion());
        for (final Iterator<Header> it = original.headerIterator(); it.hasNext(); ) {
            copy.addHeader(it.next());
        }
        copy.setScheme(original.getScheme());
        copy.setAuthority(original.getAuthority());
        return copy;
    }

    public SimpleHttpRequest(final String method, final String path) {
        super(method, path);
    }

    public SimpleHttpRequest(final String method, final HttpHost host, final String path) {
        super(method, host, path);
    }

    public SimpleHttpRequest(final String method, final URI requestUri) {
        super(method, requestUri);
    }

    SimpleHttpRequest(final Methods method, final URI requestUri) {
        this(method.name(), requestUri);
    }

    SimpleHttpRequest(final Methods method, final HttpHost host, final String path) {
        this(method.name(), host, path);
    }

    public void setBody(final SimpleBody body) {
        this.body = body;
    }

    public void setBodyBytes(final byte[] bodyBytes, final ContentType contentType) {
        this.body = SimpleBody.create(bodyBytes, contentType);
    }

    public void setBodyText(final String bodyText, final ContentType contentType) {
        this.body = SimpleBody.create(bodyText, contentType);
    }

    public SimpleBody getBody() {
        return body;
    }

    public ContentType getContentType() {
        return body != null ? body.getContentType() : null;
    }

    public String getBodyText() {
        return body != null ? body.getBodyText() : null;
    }

    public byte[] getBodyBytes() {
        return body != null ? body.getBodyBytes() : null;
    }

}

