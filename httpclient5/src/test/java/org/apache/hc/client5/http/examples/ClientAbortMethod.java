package org.apache.hc.client5.http.examples;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;

/**
 * This example demonstrates how to abort an HTTP method before its normal completion.
 */
public class ClientAbortMethod {

    public static void main(final String[] args) throws Exception {
        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final HttpGet httpget = new HttpGet("http://httpbin.org/get");
            System.out.println("Executing request " + httpget.getMethod() + " " + httpget.getUri());
            try (final CloseableHttpResponse response = httpclient.execute(httpget)) {
                System.out.println("----------------------------------------");
                System.out.println(response.getCode() + " " + response.getReasonPhrase());
                // Do not feel like reading the response body
                // Call cancel on the request object
                httpget.cancel();
            }
        }
    }

}

