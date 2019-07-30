package org.apache.hc.client5.http.fluent;

import org.apache.hc.client5.http.impl.classic.AbstractHttpClientResponseHandler;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;

/**
 * {@link org.apache.hc.core5.http.io.HttpClientResponseHandler} implementation
 * that converts {@link org.apache.hc.core5.http.HttpResponse} messages
 * to {@link Content} instances.
 *
 * @see Content
 * @since 4.4
 */
public class ContentResponseHandler extends AbstractHttpClientResponseHandler<Content> {

    @Override
    public Content handleEntity(final HttpEntity entity) throws IOException {
        return entity != null ?
                new Content(EntityUtils.toByteArray(entity), ContentType.parse(entity.getContentType())) :
                Content.NO_CONTENT;
    }

}
