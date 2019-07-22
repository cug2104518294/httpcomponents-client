package org.apache.hc.client5.http.classic;

import java.io.IOException;

import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.util.Args;

/**
 * Represents a single element in the client side classic request execution chain.
 *
 * @since 5.0
 */
@Contract(threading = ThreadingBehavior.STATELESS)
public interface ExecChain {

    final class Scope {

        public final String exchangeId;
        public final HttpRoute route;
        public final ClassicHttpRequest originalRequest;
        public final ExecRuntime execRuntime;
        public final HttpClientContext clientContext;

        public Scope(final String exchangeId, final HttpRoute route, final ClassicHttpRequest originalRequest, final ExecRuntime execRuntime, final HttpClientContext clientContext) {
            this.exchangeId = Args.notNull(exchangeId, "Exchange id");
            this.route = Args.notNull(route, "Route");
            this.originalRequest = Args.notNull(originalRequest, "Original request");
            this.execRuntime = Args.notNull(execRuntime, "Exec runtime");
            this.clientContext = clientContext != null ? clientContext : HttpClientContext.create();
        }

    }

    ClassicHttpResponse proceed(
            ClassicHttpRequest request,
            Scope scope) throws IOException, HttpException;

}
