package org.apache.hc.client5.http.impl;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.concurrent.DefaultThreadFactory;
import org.apache.hc.core5.pool.ConnPoolControl;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import java.util.concurrent.ThreadFactory;

/**
 * This class maintains a background thread to enforce an eviction policy for expired / idle
 * persistent connections kept alive in the connection pool.
 * <p>
 * IdleConnectionEvictor类进行对失效线程和超时闲置线程的处理
 *
 * @since 4.4
 */
@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public final class IdleConnectionEvictor {

    private final ThreadFactory threadFactory;
    private final Thread thread;

    public IdleConnectionEvictor(final ConnPoolControl<?> connectionManager, final ThreadFactory threadFactory,
                                 final TimeValue sleepTime, final TimeValue maxIdleTime) {
        Args.notNull(connectionManager, "Connection manager");
        this.threadFactory = threadFactory != null ? threadFactory : new DefaultThreadFactory("idle-connection-evictor", true);
        final TimeValue localSleepTime = sleepTime != null ? sleepTime : TimeValue.ofSeconds(5);
        this.thread = this.threadFactory.newThread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        Thread.sleep(localSleepTime.toMillis());
                        connectionManager.closeExpired();
                        if (maxIdleTime != null) {
                            connectionManager.closeIdle(maxIdleTime);
                        }
                    }
                } catch (final InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (final Exception ex) {
                }

            }
        });
    }

    public IdleConnectionEvictor(final ConnPoolControl<?> connectionManager, final TimeValue sleepTime, final TimeValue maxIdleTime) {
        this(connectionManager, null, sleepTime, maxIdleTime);
    }

    public IdleConnectionEvictor(final ConnPoolControl<?> connectionManager, final TimeValue maxIdleTime) {
        this(connectionManager, null, maxIdleTime, maxIdleTime);
    }

    public void start() {
        thread.start();
    }

    public void shutdown() {
        thread.interrupt();
    }

    public boolean isRunning() {
        return thread.isAlive();
    }

    public void awaitTermination(final Timeout timeout) throws InterruptedException {
        thread.join(timeout != null ? timeout.toMillis() : Long.MAX_VALUE);
    }

}
