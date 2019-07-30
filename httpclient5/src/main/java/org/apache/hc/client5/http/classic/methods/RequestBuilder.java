package org.apache.hc.client5.http.classic.methods;

import org.apache.hc.client5.http.StandardMethods;
import org.apache.hc.client5.http.config.Configurable;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.http.message.HeaderGroup;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.util.Args;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Builder for {@link ClassicHttpRequest} instances.
 * <p>
 * Please note that this class treats parameters differently depending on composition
 * of the request: if the request has a content entity explicitly set with
 * {@link #setEntity(org.apache.hc.core5.http.HttpEntity)} or it is not an entity enclosing
 * method (such as POST or PUT), parameters will be added to the query component
 * of the request URI. Otherwise, parameters will be added as a URL encoded
 * {@link UrlEncodedFormEntity entity}.
 * </p>
 *
 * @since 4.3
 * @deprecated Use {@link org.apache.hc.core5.http.io.support.ClassicRequestBuilder}
 */
@Deprecated
public class RequestBuilder {

    private String method;
    private Charset charset;
    private ProtocolVersion version;
    private URI uri;
    private HeaderGroup headerGroup;
    private HttpEntity entity;
    private List<NameValuePair> parameters;
    private RequestConfig config;

    RequestBuilder(final String method) {
        super();
        this.charset = StandardCharsets.UTF_8;
        this.method = method;
    }

    RequestBuilder() {
    }

    RequestBuilder(final StandardMethods method) {
        this(method.name());
    }

    RequestBuilder(final String method, final URI uri) {
        super();
        this.method = method;
        this.uri = uri;
    }

    RequestBuilder(final StandardMethods method, final URI uri) {
        this(method.name(), uri);
    }

    RequestBuilder(final String method, final String uri) {
        super();
        this.method = method;
        this.uri = uri != null ? URI.create(uri) : null;
    }

    RequestBuilder(final StandardMethods method, final String uri) {
        this(method.name(), uri);
    }

    public static RequestBuilder create(final String method) {
        Args.notBlank(method, "HTTP method");
        return new RequestBuilder(method);
    }

    public static RequestBuilder get() {
        return new RequestBuilder(StandardMethods.GET.name());
    }

    /**
     * @since 4.4
     */
    public static RequestBuilder get(final URI uri) {
        return new RequestBuilder(StandardMethods.GET, uri);
    }

    /**
     * @since 4.4
     */
    public static RequestBuilder get(final String uri) {
        return new RequestBuilder(StandardMethods.GET, uri);
    }

    public static RequestBuilder head() {
        return new RequestBuilder(StandardMethods.HEAD);
    }

    /**
     * @since 4.4
     */
    public static RequestBuilder head(final URI uri) {
        return new RequestBuilder(StandardMethods.HEAD, uri);
    }

    /**
     * @since 4.4
     */
    public static RequestBuilder head(final String uri) {
        return new RequestBuilder(StandardMethods.HEAD, uri);
    }

    /**
     * @since 4.4
     */
    public static RequestBuilder patch() {
        return new RequestBuilder(StandardMethods.PATCH.name());
    }

    /**
     * @since 4.4
     */
    public static RequestBuilder patch(final URI uri) {
        return new RequestBuilder(StandardMethods.PATCH, uri);
    }

    /**
     * @since 4.4
     */
    public static RequestBuilder patch(final String uri) {
        return new RequestBuilder(StandardMethods.PATCH, uri);
    }

    public static RequestBuilder post() {
        return new RequestBuilder(StandardMethods.POST);
    }

    /**
     * @since 4.4
     */
    public static RequestBuilder post(final URI uri) {
        return new RequestBuilder(StandardMethods.POST, uri);
    }

    /**
     * @since 4.4
     */
    public static RequestBuilder post(final String uri) {
        return new RequestBuilder(StandardMethods.POST, uri);
    }

    public static RequestBuilder put() {
        return new RequestBuilder(StandardMethods.PUT);
    }

    /**
     * @since 4.4
     */
    public static RequestBuilder put(final URI uri) {
        return new RequestBuilder(StandardMethods.PUT, uri);
    }

    /**
     * @since 4.4
     */
    public static RequestBuilder put(final String uri) {
        return new RequestBuilder(StandardMethods.PUT, uri);
    }

    public static RequestBuilder delete() {
        return new RequestBuilder(StandardMethods.DELETE);
    }

    /**
     * @since 4.4
     */
    public static RequestBuilder delete(final URI uri) {
        return new RequestBuilder(StandardMethods.DELETE, uri);
    }

    /**
     * @since 4.4
     */
    public static RequestBuilder delete(final String uri) {
        return new RequestBuilder(StandardMethods.DELETE, uri);
    }

    public static RequestBuilder trace() {
        return new RequestBuilder(StandardMethods.TRACE);
    }

    /**
     * @since 4.4
     */
    public static RequestBuilder trace(final URI uri) {
        return new RequestBuilder(StandardMethods.TRACE, uri);
    }

    /**
     * @since 4.4
     */
    public static RequestBuilder trace(final String uri) {
        return new RequestBuilder(StandardMethods.TRACE, uri);
    }

    public static RequestBuilder options() {
        return new RequestBuilder(StandardMethods.OPTIONS);
    }

    /**
     * @since 4.4
     */
    public static RequestBuilder options(final URI uri) {
        return new RequestBuilder(StandardMethods.OPTIONS, uri);
    }

    /**
     * @since 4.4
     */
    public static RequestBuilder options(final String uri) {
        return new RequestBuilder(StandardMethods.OPTIONS, uri);
    }

    public static RequestBuilder copy(final ClassicHttpRequest request) {
        Args.notNull(request, "HTTP request");
        return new RequestBuilder().doCopy(request);
    }

    private RequestBuilder doCopy(final ClassicHttpRequest request) {
        if (request == null) {
            return this;
        }
        method = request.getMethod();
        version = request.getVersion();

        if (headerGroup == null) {
            headerGroup = new HeaderGroup();
        }
        headerGroup.clear();
        headerGroup.setHeaders(request.getHeaders());

        parameters = null;
        entity = null;

        final HttpEntity originalEntity = request.getEntity();
        if (originalEntity != null) {
            final ContentType contentType = ContentType.parse(originalEntity.getContentType());
            if (contentType != null &&
                    contentType.getMimeType().equals(ContentType.APPLICATION_FORM_URLENCODED.getMimeType())) {
                try {
                    final List<NameValuePair> formParams = EntityUtils.parse(originalEntity);
                    if (!formParams.isEmpty()) {
                        parameters = formParams;
                    }
                } catch (final IOException ignore) {
                }
            } else {
                entity = originalEntity;
            }
        }

        try {
            uri = request.getUri();
        } catch (final URISyntaxException ignore) {
        }
        if (request instanceof Configurable) {
            config = ((Configurable) request).getConfig();
        } else {
            config = null;
        }
        return this;
    }

    /**
     * @since 4.4
     */
    public RequestBuilder setCharset(final Charset charset) {
        this.charset = charset;
        return this;
    }

    /**
     * @since 4.4
     */
    public Charset getCharset() {
        return charset;
    }

    public String getMethod() {
        return method;
    }

    public ProtocolVersion getVersion() {
        return version;
    }

    public RequestBuilder setVersion(final ProtocolVersion version) {
        this.version = version;
        return this;
    }

    public URI getUri() {
        return uri;
    }

    public RequestBuilder setUri(final URI uri) {
        this.uri = uri;
        return this;
    }

    public RequestBuilder setUri(final String uri) {
        this.uri = uri != null ? URI.create(uri) : null;
        return this;
    }

    public Header getFirstHeader(final String name) {
        return headerGroup != null ? headerGroup.getFirstHeader(name) : null;
    }

    public Header getLastHeader(final String name) {
        return headerGroup != null ? headerGroup.getLastHeader(name) : null;
    }

    public Header[] getHeaders(final String name) {
        return headerGroup != null ? headerGroup.getHeaders(name) : null;
    }

    public RequestBuilder addHeader(final Header header) {
        if (headerGroup == null) {
            headerGroup = new HeaderGroup();
        }
        headerGroup.addHeader(header);
        return this;
    }

    public RequestBuilder addHeader(final String name, final String value) {
        if (headerGroup == null) {
            headerGroup = new HeaderGroup();
        }
        this.headerGroup.addHeader(new BasicHeader(name, value));
        return this;
    }

    public RequestBuilder removeHeader(final Header header) {
        if (headerGroup == null) {
            headerGroup = new HeaderGroup();
        }
        headerGroup.removeHeader(header);
        return this;
    }

    public RequestBuilder removeHeaders(final String name) {
        if (name == null || headerGroup == null) {
            return this;
        }
        for (final Iterator<Header> i = headerGroup.headerIterator(); i.hasNext(); ) {
            final Header header = i.next();
            if (name.equalsIgnoreCase(header.getName())) {
                i.remove();
            }
        }
        return this;
    }

    public RequestBuilder setHeader(final Header header) {
        if (headerGroup == null) {
            headerGroup = new HeaderGroup();
        }
        this.headerGroup.setHeader(header);
        return this;
    }

    public RequestBuilder setHeader(final String name, final String value) {
        if (headerGroup == null) {
            headerGroup = new HeaderGroup();
        }
        this.headerGroup.setHeader(new BasicHeader(name, value));
        return this;
    }

    public HttpEntity getEntity() {
        return entity;
    }

    public RequestBuilder setEntity(final HttpEntity entity) {
        this.entity = entity;
        return this;
    }

    public List<NameValuePair> getParameters() {
        return parameters != null ? new ArrayList<>(parameters) :
                new ArrayList<NameValuePair>();
    }

    public RequestBuilder addParameter(final NameValuePair nvp) {
        Args.notNull(nvp, "Name value pair");
        if (parameters == null) {
            parameters = new LinkedList<>();
        }
        parameters.add(nvp);
        return this;
    }

    public RequestBuilder addParameter(final String name, final String value) {
        return addParameter(new BasicNameValuePair(name, value));
    }

    public RequestBuilder addParameters(final NameValuePair... nvps) {
        for (final NameValuePair nvp : nvps) {
            addParameter(nvp);
        }
        return this;
    }

    public RequestConfig getConfig() {
        return config;
    }

    public RequestBuilder setConfig(final RequestConfig config) {
        this.config = config;
        return this;
    }

    public ClassicHttpRequest build() {
        URI uriNotNull = this.uri != null ? this.uri : URI.create("/");
        HttpEntity entityCopy = this.entity;
        if (parameters != null && !parameters.isEmpty()) {
            if (entityCopy == null && (StandardMethods.POST.name().equalsIgnoreCase(method)
                    || StandardMethods.PUT.name().equalsIgnoreCase(method))) {
                entityCopy = new UrlEncodedFormEntity(parameters, charset != null ? charset : StandardCharsets.ISO_8859_1);
            } else {
                try {
                    uriNotNull = new URIBuilder(uriNotNull)
                            .setCharset(this.charset)
                            .addParameters(parameters)
                            .build();
                } catch (final URISyntaxException ex) {
                    // should never happen
                }
            }
        }

        if (entityCopy != null && StandardMethods.TRACE.name().equalsIgnoreCase(method)) {
            throw new IllegalStateException(StandardMethods.TRACE.name() + " requests may not include an entity.");
        }

        final HttpUriRequestBase result = new HttpUriRequestBase(method, uriNotNull);
        result.setVersion(this.version != null ? this.version : HttpVersion.HTTP_1_1);
        if (this.headerGroup != null) {
            result.setHeaders(this.headerGroup.getHeaders());
        }
        result.setEntity(entityCopy);
        result.setConfig(this.config);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("RequestBuilder [method=");
        builder.append(method);
        builder.append(", charset=");
        builder.append(charset);
        builder.append(", version=");
        builder.append(version);
        builder.append(", uri=");
        builder.append(uri);
        builder.append(", headerGroup=");
        builder.append(headerGroup);
        builder.append(", entity=");
        builder.append(entity);
        builder.append(", parameters=");
        builder.append(parameters);
        builder.append(", config=");
        builder.append(config);
        builder.append("]");
        return builder.toString();
    }

}
