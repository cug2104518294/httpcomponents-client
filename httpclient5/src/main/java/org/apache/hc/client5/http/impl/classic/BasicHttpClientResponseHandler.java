package org.apache.hc.client5.http.impl.classic;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;

/**
 * A {@link org.apache.hc.core5.http.io.HttpClientResponseHandler} that returns
 * the response body as a String for successful (2xx) responses. If the response
 * code was &gt;= 300, the response body is consumed
 * and an {@link org.apache.hc.client5.http.HttpResponseException} is thrown.
 * <p>
 * If this is used with
 * {@link org.apache.hc.client5.http.classic.HttpClient#execute(
 *org.apache.hc.core5.http.ClassicHttpRequest,
 * org.apache.hc.core5.http.io.HttpClientResponseHandler)},
 * HttpClient may handle redirects (3xx responses) internally.
 * </p>
 *
 * @since 4.0
 */
@Contract(threading = ThreadingBehavior.STATELESS)
public class BasicHttpClientResponseHandler extends AbstractHttpClientResponseHandler<String> {

    /**
     * Returns the entity as a body as a String.
     */
    @Override
    public String handleEntity(final HttpEntity entity) throws IOException {
        try {
            return EntityUtils.toString(entity);
        } catch (final ParseException ex) {
            throw new ClientProtocolException(ex);
        }
    }

    @Override
    public String handleResponse(final ClassicHttpResponse response) throws IOException {
        return super.handleResponse(response);
    }

}
