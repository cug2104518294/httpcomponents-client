package org.apache.hc.client5.http.io;

import java.io.IOException;

import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.io.ModalCloseable;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

/**
 * Represents a manager of persistent client connections.
 * <p>
 * The purpose of an HTTP connection manager is to serve as a factory for new
 * HTTP connections, manage persistent connections and synchronize access to
 * persistent connections making sure that only one thread of execution can
 * have access to a connection at a time.
 * </p>
 * <p>
 * Implementations of this interface must be thread-safe. Access to shared
 * data must be synchronized as methods of this interface may be executed
 * from multiple threads.
 * </p>
 *
 * 连接池组件，管理连接的整个生命周期。连接在连接池中创建、复用以及移除。
 * Connection manager封装了对连接池的具体操作，比如向连接池租用和归还连接。
 * Connection被创建出来后处于闲置状态，由连接池管理，调用时会校验是否是open状态，不是的话会进行connect。
 * connect的过程就是 基于不同schema(主要是http和https)创建不同的socket连接(ssl和plain)并且将http请求（连接）绑定到socket。
 * 同时连接也会因为心跳或者过期等原因被close变成stale状态，直至被下一次get到时或者连接满时被清理出去。
 * 同时连接池还能对连接进行限流–全局和单route连接数。
 *
 * @since 4.3
 */
public interface HttpClientConnectionManager extends ModalCloseable {

    /**
     * Returns a {@link LeaseRequest} object which can be used to obtain
     * a {@link ConnectionEndpoint} to cancel the request by calling
     * {@link LeaseRequest#cancel()}.
     * <p>
     * Please note that newly allocated endpoints can be leased
     * {@link ConnectionEndpoint#isConnected() disconnected}. The consumer of the endpoint
     * is responsible for fully establishing the route to the endpoint target
     * by calling {@link #connect(ConnectionEndpoint, TimeValue, HttpContext)}
     * in order to connect directly to the target or to the first proxy hop,
     * and optionally calling {@link #upgrade(ConnectionEndpoint, HttpContext)} method
     * to upgrade the underlying transport to Transport Layer Security after having
     * executed a {@code CONNECT} method to all intermediate proxy hops.
     *
     * @param id unique operation ID or {@code null}.
     * @param route HTTP route of the requested connection.
     * @param requestTimeout lease request timeout.
     * @param state expected state of the connection or {@code null}
     *              if the connection is not expected to carry any state.
     * @since 5.0
     */
    LeaseRequest lease(String id, HttpRoute route, Timeout requestTimeout, Object state);

    /**
     * Releases the endpoint back to the manager making it potentially
     * re-usable by other consumers. Optionally, the maximum period
     * of how long the manager should keep the connection alive can be
     * defined using {@code validDuration} and {@code timeUnit}
     * parameters.
     *
     * @param endpoint      the managed endpoint.
     * @param newState      the new connection state of {@code null} if state-less.
     * @param validDuration the duration of time this connection is valid for reuse.
     */
    void release(ConnectionEndpoint endpoint, Object newState, TimeValue validDuration);

    /**
     * Connects the endpoint to the initial hop (connection target in case
     * of a direct route or to the first proxy hop in case of a route via a proxy
     * or multiple proxies).
     *
     * @param endpoint      the managed endpoint.
     * @param connectTimeout connect timeout.
     * @param context the actual HTTP context.
     */
    void connect(ConnectionEndpoint endpoint, TimeValue connectTimeout, HttpContext context) throws IOException;

    /**
     * Upgrades transport security of the given endpoint by using the TLS security protocol.
     *
     * @param endpoint      the managed endpoint.
     * @param context the actual HTTP context.
     */
    void upgrade(ConnectionEndpoint endpoint, HttpContext context) throws IOException;

}
