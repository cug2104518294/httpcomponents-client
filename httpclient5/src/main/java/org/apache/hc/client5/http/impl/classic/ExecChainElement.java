package org.apache.hc.client5.http.impl.classic;

import org.apache.hc.client5.http.classic.ExecChain;
import org.apache.hc.client5.http.classic.ExecChainHandler;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;

import java.io.IOException;

class ExecChainElement {

    private final ExecChainHandler handler;
    private final ExecChainElement next;

    ExecChainElement(final ExecChainHandler handler, final ExecChainElement next) {
        this.handler = handler;
        this.next = next;
    }

    public ClassicHttpResponse execute(
            final ClassicHttpRequest request,
            final ExecChain.Scope scope) throws IOException, HttpException {
        return handler.execute(request, scope, new ExecChain() {
            @Override
            public ClassicHttpResponse proceed(
                    final ClassicHttpRequest request,
                    final Scope scope) throws IOException, HttpException {
                return next.execute(request, scope);
            }

        });
    }

    @Override
    public String toString() {
        return "{" +
                "handler=" + handler.getClass() +
                ", next=" + (next != null ? next.handler.getClass() : "null") +
                '}';
    }
}