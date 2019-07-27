package org.apache.hc.client5.http.examples;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class QuickStart {

    public static void main(final String[] args) throws Exception {
        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final HttpGet httpGet = new HttpGet("http://httpbin.org/get");
            try (final CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
                System.out.println(response1.getCode() + " " + response1.getReasonPhrase());
                final HttpEntity entity1 = response1.getEntity();
                EntityUtils.consume(entity1);
            }


            // 创建post方式请求对象，这个类似于我们打开的浏览器，再开个页面
            // http请求包括：请求行（request line）、请求头部（header）、空行和请求数据（body的数据）
            // 这个就是构造一个http请数据
            final HttpPost httpPost = new HttpPost("http://httpbin.org/post");
            final List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("username", "vip"));
            nvps.add(new BasicNameValuePair("password", "secret"));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            // 解析http请求的body数据
            // http响应也由四个部分组成，分别是：状态行、消息报头、空行和响应正文
            try (final CloseableHttpResponse response2 = httpclient.execute(httpPost)) {
                System.out.println(response2.getCode() + " " + response2.getReasonPhrase());
                final HttpEntity entity2 = response2.getEntity();
                EntityUtils.consume(entity2);
            }
        }
    }

}
