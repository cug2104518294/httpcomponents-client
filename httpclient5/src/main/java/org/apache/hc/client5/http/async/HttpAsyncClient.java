package org.apache.hc.client5.http.async;

import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.util.concurrent.Future;

/**
 * This interface represents only the most basic contract for HTTP request
 * execution. It imposes no restrictions or particular details on the request
 * execution process and leaves the specifics of state management,
 * authentication and redirect handling up to individual implementations.
 *
 * @since 4.0
 */
public interface HttpAsyncClient {

    /**
     * Initiates asynchronous HTTP request execution using the given context.
     * <p>
     * The request producer passed to this method will be used to generate
     * a request message and stream out its content without buffering it
     * in memory. The response consumer passed to this method will be used
     * to process a response message without buffering its content in memory.
     * <p>
     * Please note it may be unsafe to interact with the context instance
     * while the request is still being executed.
     *
     * @param <T>                the result type of request execution.
     * @param requestProducer    request producer callback.
     * @param responseConsumer   response consumer callback.
     * @param pushHandlerFactory the push handler factory. Optional and may be {@code null}.
     * @param context            HTTP context. Optional and may be {@code null}.
     * @param callback           future callback. Optional and may be {@code null}.
     * @return future representing pending completion of the operation.
     */
    <T> Future<T> execute(
            AsyncRequestProducer requestProducer,
            AsyncResponseConsumer<T> responseConsumer,
            HandlerFactory<AsyncPushConsumer> pushHandlerFactory,
            HttpContext context,
            FutureCallback<T> callback);

}
