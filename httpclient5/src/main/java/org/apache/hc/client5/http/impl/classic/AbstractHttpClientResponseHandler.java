package org.apache.hc.client5.http.impl.classic;

import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;

/**
 * A generic {@link HttpClientResponseHandler} that works with the response entity
 * for successful (2xx) responses.
 * If the response code was &gt;= 300, the response
 * body is consumed and an {@link HttpResponseException} is thrown.
 * <p>
 * If this is used with
 * {@link org.apache.hc.client5.http.classic.HttpClient#execute(
 *org.apache.hc.core5.http.ClassicHttpRequest, HttpClientResponseHandler)},
 * HttpClient may handle redirects (3xx responses) internally.
 * </p>
 *
 * @since 4.4
 */
@Contract(threading = ThreadingBehavior.STATELESS)
public abstract class AbstractHttpClientResponseHandler<T> implements HttpClientResponseHandler<T> {

    /**
     * Read the entity from the response body and pass it to the entity handler
     * method if the response was successful (a 2xx status code). If no response
     * body exists, this returns null. If the response was unsuccessful (&gt;= 300
     * status code), throws an {@link HttpResponseException}.
     */
    @Override
    public T handleResponse(final ClassicHttpResponse response) throws IOException {
        final HttpEntity entity = response.getEntity();
        if (response.getCode() >= HttpStatus.SC_REDIRECTION) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(response.getCode(), response.getReasonPhrase());
        }
        return entity == null ? null : handleEntity(entity);
    }

    /**
     * Handle the response entity and transform it into the actual response
     * object.
     */
    public abstract T handleEntity(HttpEntity entity) throws IOException;

}
