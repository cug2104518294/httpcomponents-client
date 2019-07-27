package org.apache.hc.client5.http.impl;

import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.HeaderElements;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.message.MessageSupport;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TimeValue;

import java.util.Iterator;

/**
 * Default implementation of a strategy deciding duration
 * that a connection can remain idle.
 * <p>
 * The default implementation looks solely at the 'Keep-Alive'
 * header's timeout token.
 * </p>
 *
 * @since 4.0
 */
@Contract(threading = ThreadingBehavior.STATELESS)
public class DefaultConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {

    public static final DefaultConnectionKeepAliveStrategy INSTANCE = new DefaultConnectionKeepAliveStrategy();

    @Override
    public TimeValue getKeepAliveDuration(final HttpResponse response, final HttpContext context) {
        Args.notNull(response, "HTTP response");
        final Iterator<HeaderElement> it = MessageSupport.iterate(response, HeaderElements.KEEP_ALIVE);
        while (it.hasNext()) {
            final HeaderElement he = it.next();
            final String param = he.getName();
            final String value = he.getValue();
            if (value != null && param.equalsIgnoreCase("timeout")) {
                try {
                    return TimeValue.ofSeconds(Long.parseLong(value));
                } catch (final NumberFormatException ignore) {
                }
            }
        }
        return TimeValue.NEG_ONE_MILLISECONDS;
    }

}
