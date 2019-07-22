package org.apache.hc.client5.http.classic.methods;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.concurrent.Cancellable;
import org.apache.hc.core5.concurrent.CancellableDependency;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;

import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class HttpUriRequestBase extends BasicClassicHttpRequest implements HttpUriRequest, CancellableDependency {

    private static final long serialVersionUID = 1L;

    private RequestConfig requestConfig;
    private final AtomicBoolean cancelled;
    private final AtomicReference<Cancellable> cancellableRef;

    public HttpUriRequestBase(final String method, final URI requestUri) {
        super(method, requestUri);
        this.cancelled = new AtomicBoolean(false);
        this.cancellableRef = new AtomicReference<>(null);
    }

    @Override
    public boolean cancel() {
        if (this.cancelled.compareAndSet(false, true)) {
            final Cancellable cancellable = this.cancellableRef.getAndSet(null);
            if (cancellable != null) {
                cancellable.cancel();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isCancelled() {
        return cancelled.get();
    }

    /**
     * @since 4.2
     */
    @Override
    public void setDependency(final Cancellable cancellable) {
        if (!this.cancelled.get()) {
            this.cancellableRef.set(cancellable);
        }
    }

    /**
     * Resets internal state of the request making it reusable.
     *
     * @since 4.2
     */
    public void reset() {
        final Cancellable cancellable = this.cancellableRef.getAndSet(null);
        if (cancellable != null) {
            cancellable.cancel();
        }
        this.cancelled.set(false);
    }

    @Override
    public void abort() throws UnsupportedOperationException {
        cancel();
    }

    @Override
    public boolean isAborted() {
        return isCancelled();
    }

    public void setConfig(final RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }

    @Override
    public RequestConfig getConfig() {
        return requestConfig;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getMethod()).append(" ").append(getRequestUri());
        return sb.toString();
    }

}
