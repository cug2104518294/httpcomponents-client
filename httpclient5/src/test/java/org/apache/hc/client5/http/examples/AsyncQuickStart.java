package org.apache.hc.client5.http.examples;

import org.apache.hc.client5.http.async.methods.AbstractCharResponseConsumer;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequests;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.support.AsyncRequestBuilder;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

public class AsyncQuickStart {

    public static void main(final String[] args) throws Exception {
        final CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        try {
            // Start the client
            httpclient.start();

            // Execute request
            final SimpleHttpRequest request1 = SimpleHttpRequests.GET.create("http://httpbin.org/get");
            final Future<SimpleHttpResponse> future = httpclient.execute(request1, null);
            // and wait until response is received
            final SimpleHttpResponse response1 = future.get();
            System.out.println(request1.getRequestUri() + "->" + response1.getCode());

            // One most likely would want to use a callback for operation result
            final CountDownLatch latch1 = new CountDownLatch(1);
            final SimpleHttpRequest request2 = SimpleHttpRequests.GET.create("http://httpbin.org/get");
            httpclient.execute(request2, new FutureCallback<SimpleHttpResponse>() {

                @Override
                public void completed(final SimpleHttpResponse response2) {
                    latch1.countDown();
                    System.out.println(request2.getRequestUri() + "->" + response2.getCode());
                }

                @Override
                public void failed(final Exception ex) {
                    latch1.countDown();
                    System.out.println(request2.getRequestUri() + "->" + ex);
                }

                @Override
                public void cancelled() {
                    latch1.countDown();
                    System.out.println(request2.getRequestUri() + " cancelled");
                }

            });
            latch1.await();

            // In real world one most likely would want also want to stream
            // request and response body content
            final CountDownLatch latch2 = new CountDownLatch(1);
            final AsyncRequestProducer producer3 = AsyncRequestBuilder.get("http://httpbin.org/get").build();
            final AbstractCharResponseConsumer<HttpResponse> consumer3 = new AbstractCharResponseConsumer<HttpResponse>() {

                HttpResponse response;

                @Override
                protected void start(final HttpResponse response, final ContentType contentType) throws HttpException, IOException {
                    this.response = response;
                }

                @Override
                protected int capacityIncrement() {
                    return Integer.MAX_VALUE;
                }

                @Override
                protected void data(final CharBuffer data, final boolean endOfStream) throws IOException {
                    // Do something useful
                }

                @Override
                protected HttpResponse buildResult() throws IOException {
                    return response;
                }

                @Override
                public void releaseResources() {
                }

            };
            httpclient.execute(producer3, consumer3, new FutureCallback<HttpResponse>() {

                @Override
                public void completed(final HttpResponse response3) {
                    latch2.countDown();
                    System.out.println(request2.getRequestUri() + "->" + response3.getCode());
                }

                @Override
                public void failed(final Exception ex) {
                    latch2.countDown();
                    System.out.println(request2.getRequestUri() + "->" + ex);
                }

                @Override
                public void cancelled() {
                    latch2.countDown();
                    System.out.println(request2.getRequestUri() + " cancelled");
                }

            });
            latch2.await();

        } finally {
            httpclient.close();
        }
    }

}