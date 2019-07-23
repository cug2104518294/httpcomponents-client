package org.apache.hc.client5.http.impl.cache;

import org.apache.hc.client5.http.classic.ExecChain;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;

import java.io.IOException;

public class DummyBackend implements ExecChain {

    private ClassicHttpRequest request;
    private ClassicHttpResponse response = new BasicClassicHttpResponse(HttpStatus.SC_OK, "OK");
    private int executions = 0;

    public void setResponse(final ClassicHttpResponse resp) {
        response = resp;
    }

    public HttpRequest getCapturedRequest() {
        return request;
    }

    @Override
    public ClassicHttpResponse proceed(
            final ClassicHttpRequest request,
            final Scope scope) throws IOException, HttpException {
        this.request = request;
        executions++;
        return response;
    }

    public int getExecutions() {
        return executions;
    }
}
