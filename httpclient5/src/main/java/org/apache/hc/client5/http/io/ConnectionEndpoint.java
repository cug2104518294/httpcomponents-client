package org.apache.hc.client5.http.io;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.impl.io.HttpRequestExecutor;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.io.ModalCloseable;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;

/**
 * Client endpoint leased from a connection manager. Client points can be used
 * to execute HTTP requests.
 * 客户端端点从连接管理器租用。
 * 客户端点可用于执行HTTP请求。
 *
 * <p>
 * Once the endpoint is no longer needed it MUST be released with {@link #close(org.apache.hc.core5.io.CloseMode)} )}.
 * </p>
 *
 * @since 5.0
 */
@Contract(threading = ThreadingBehavior.SAFE)
public abstract class ConnectionEndpoint implements ModalCloseable {

    /**
     * Executes HTTP request using the provided request executor.
     * <p>
     * Once the endpoint is no longer needed it MUST be released with {@link #close(org.apache.hc.core5.io.CloseMode)}.
     * </p>
     *
     * @param id       unique operation ID or {@code null}.
     * @param request  the request message.
     * @param executor the request executor.
     * @param context  the execution context.
     */
    public abstract ClassicHttpResponse execute(
            String id,
            ClassicHttpRequest request,
            HttpRequestExecutor executor,
            HttpContext context) throws IOException, HttpException;

    /**
     * Determines if the connection to the remote endpoint is still open and valid.
     */
    public abstract boolean isConnected();

    /**
     * Sets the socket timeout value.
     *
     * @param timeout timeout value
     */
    public abstract void setSocketTimeout(Timeout timeout);

}
