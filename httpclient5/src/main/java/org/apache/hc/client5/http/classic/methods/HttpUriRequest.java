package org.apache.hc.client5.http.classic.methods;

import org.apache.hc.client5.http.config.Configurable;
import org.apache.hc.core5.http.ClassicHttpRequest;

/**
 * Extended version of the {@link ClassicHttpRequest} interface that provides
 * convenience methods to access request properties such as request URI
 * and method type.
 *
 * Configurable 接口主要是http请求的相关基础  重点在于RequestConfig 代理 超时等
 * ClassicHttpRequest 其实就是请求路径 协议版本等基本的请求
 *
 * HttpUriRequest 继承上面两个接口  并添加了是否丢弃对应的请求
 *
 * @since 4.0
 */
public interface HttpUriRequest extends ClassicHttpRequest, Configurable {

    /**
     * Aborts execution of the request.
     *
     * @throws UnsupportedOperationException if the abort operation
     *                                       is not supported / cannot be implemented.
     */
    void abort() throws UnsupportedOperationException;

    /**
     * Tests if the request execution has been aborted.
     *
     * @return {@code true} if the request execution has been aborted,
     * {@code false} otherwise.
     */
    boolean isAborted();

}
