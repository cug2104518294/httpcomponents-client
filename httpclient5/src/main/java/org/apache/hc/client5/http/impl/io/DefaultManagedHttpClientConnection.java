package org.apache.hc.client5.http.impl.io;

import org.apache.hc.client5.http.io.ManagedHttpClientConnection;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentLengthStrategy;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.http.impl.io.DefaultBHttpClientConnection;
import org.apache.hc.core5.http.impl.io.SocketHolder;
import org.apache.hc.core5.http.io.HttpMessageParserFactory;
import org.apache.hc.core5.http.io.HttpMessageWriterFactory;
import org.apache.hc.core5.http.message.RequestLine;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.util.Identifiable;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.concurrent.atomic.AtomicBoolean;

final class DefaultManagedHttpClientConnection
        extends DefaultBHttpClientConnection implements ManagedHttpClientConnection, Identifiable {

    private final Logger log = LoggerFactory.getLogger(DefaultManagedHttpClientConnection.class);
    private final Logger headerLog = LoggerFactory.getLogger("org.apache.hc.client5.http.headers");
    private final Logger wireLog = LoggerFactory.getLogger("org.apache.hc.client5.http.wire");

    private final String id;
    private final AtomicBoolean closed;

    private Timeout socketTimeout;

    public DefaultManagedHttpClientConnection(
            final String id,
            final CharsetDecoder charDecoder,
            final CharsetEncoder charEncoder,
            final Http1Config h1Config,
            final ContentLengthStrategy incomingContentStrategy,
            final ContentLengthStrategy outgoingContentStrategy,
            final HttpMessageWriterFactory<ClassicHttpRequest> requestWriterFactory,
            final HttpMessageParserFactory<ClassicHttpResponse> responseParserFactory) {
        super(h1Config, charDecoder, charEncoder, incomingContentStrategy, outgoingContentStrategy, requestWriterFactory, responseParserFactory);
        this.id = id;
        this.closed = new AtomicBoolean();
    }

    public DefaultManagedHttpClientConnection(final String id) {
        this(id, null, null, null, null, null, null, null);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void bind(final SocketHolder socketHolder) throws IOException {
        if (this.closed.get()) {
            final Socket socket = socketHolder.getSocket();
            socket.close(); // allow this to throw...
            // ...but if it doesn't, explicitly throw one ourselves.
            throw new InterruptedIOException("Connection already shutdown");
        }
        super.bind(socketHolder);
        socketTimeout = Timeout.ofMilliseconds(socketHolder.getSocket().getSoTimeout());
    }

    @Override
    public Socket getSocket() {
        final SocketHolder socketHolder = getSocketHolder();
        return socketHolder != null ? socketHolder.getSocket() : null;
    }

    @Override
    public SSLSession getSSLSession() {
        final Socket socket = getSocket();
        if (socket instanceof SSLSocket) {
            return ((SSLSocket) socket).getSession();
        } else {
            return null;
        }
    }

    @Override
    public void close() throws IOException {
        if (this.closed.compareAndSet(false, true)) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(this.id + ": Close connection");
            }
            super.close();
        }
    }

    @Override
    public void setSocketTimeout(final Timeout timeout) {
        if (this.log.isDebugEnabled()) {
            this.log.debug(this.id + ": set socket timeout to " + timeout);
        }
        super.setSocketTimeout(timeout);
    }

    @Override
    public void close(final CloseMode closeMode) {
        if (this.closed.compareAndSet(false, true)) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(this.id + ": close connection " + closeMode);
            }
            super.close(closeMode);
        }
    }

    @Override
    public void bind(final Socket socket) throws IOException {
        super.bind(this.wireLog.isDebugEnabled() ? new LoggingSocketHolder(socket, this.id, this.wireLog) : new SocketHolder(socket));
        socketTimeout = Timeout.ofMilliseconds(socket.getSoTimeout());
    }

    @Override
    protected void onResponseReceived(final ClassicHttpResponse response) {
        if (response != null && this.headerLog.isDebugEnabled()) {
            this.headerLog.debug(this.id + " << " + new StatusLine(response));
            final Header[] headers = response.getHeaders();
            for (final Header header : headers) {
                this.headerLog.debug(this.id + " << " + header.toString());
            }
        }
    }

    @Override
    protected void onRequestSubmitted(final ClassicHttpRequest request) {
        if (request != null && this.headerLog.isDebugEnabled()) {
            this.headerLog.debug(this.id + " >> " + new RequestLine(request));
            final Header[] headers = request.getHeaders();
            for (final Header header : headers) {
                this.headerLog.debug(this.id + " >> " + header.toString());
            }
        }
    }

    @Override
    public void passivate() {
        super.setSocketTimeout(Timeout.ZERO_MILLISECONDS);
    }

    @Override
    public void activate() {
        super.setSocketTimeout(socketTimeout);
    }

}
