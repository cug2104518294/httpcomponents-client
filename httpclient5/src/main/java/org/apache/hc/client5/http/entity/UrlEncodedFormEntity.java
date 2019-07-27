package org.apache.hc.client5.http.entity;

import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.nio.charset.Charset;
import java.util.List;

/**
 * An entity composed of a list of url-encoded pairs.
 * This is typically useful while sending an HTTP POST request.
 *
 * @since 4.0
 */
public class UrlEncodedFormEntity extends StringEntity {

    /**
     * Constructs a new {@link UrlEncodedFormEntity} with the list
     * of parameters in the specified encoding.
     *
     * @param parameters iterable collection of name/value pairs
     * @param charset    encoding the name/value pairs be encoded with
     * @since 4.2
     */
    public UrlEncodedFormEntity(
            final Iterable<? extends NameValuePair> parameters,
            final Charset charset) {
        super(URLEncodedUtils.format(
                parameters,
                charset != null ? charset : ContentType.APPLICATION_FORM_URLENCODED.getCharset()),
                charset != null ? ContentType.APPLICATION_FORM_URLENCODED.withCharset(charset) : ContentType.APPLICATION_FORM_URLENCODED);
    }

    /**
     * Constructs a new {@link UrlEncodedFormEntity} with the list
     * of parameters with the default encoding of {@link ContentType#APPLICATION_FORM_URLENCODED}
     *
     * @param parameters list of name/value pairs
     */
    public UrlEncodedFormEntity(final List<? extends NameValuePair> parameters) {
        this(parameters, null);
    }

    /**
     * Constructs a new {@link UrlEncodedFormEntity} with the list
     * of parameters with the default encoding of {@link ContentType#APPLICATION_FORM_URLENCODED}
     *
     * @param parameters iterable collection of name/value pairs
     * @since 4.2
     */
    public UrlEncodedFormEntity(
            final Iterable<? extends NameValuePair> parameters) {
        this(parameters, null);
    }

}
