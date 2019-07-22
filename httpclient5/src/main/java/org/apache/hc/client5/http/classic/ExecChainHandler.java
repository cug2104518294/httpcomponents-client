package org.apache.hc.client5.http.classic;

import java.io.IOException;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;

/**
 * Abstract request execution handler in a classic client side request execution chain.
 * Handlers can either be a decorator around another element that implements a cross
 * cutting aspect or a self-contained executor capable of producing a response
 * for the given request.
 * <p>
 * Important: please note it is required for decorators that implement post execution aspects
 * or response post-processing of any sort to release resources associated with the response
 * by calling {@link ClassicHttpResponse#close()} methods in case of an I/O, protocol or
 * runtime exception, or in case the response is not propagated to the caller.
 * </p>
 *
 * @since 4.3
 */
@Contract(threading = ThreadingBehavior.STATELESS)
public interface ExecChainHandler {

    /**
     * Executes the actual HTTP request. The handler can choose to return
     * a response message or delegate request execution to the next element
     * in the execution chain.
     *
     * @param request the actual request.
     * @param scope the execution scope .
     * @param chain the next element in the request execution chain.
     */
    ClassicHttpResponse execute(
            ClassicHttpRequest request,
            ExecChain.Scope scope,
            ExecChain chain) throws IOException, HttpException;

}
