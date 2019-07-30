package org.apache.hc.client5.http.examples;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class QuickStartForPost {

    public static void main(final String[] args) throws Exception {
        // 获取连接客户端工具
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String entityStr = null;
        CloseableHttpResponse response = null;

        try {

            // 创建POST请求对象
            HttpPost httpPost = new HttpPost("http://www.baidu.com");

            /*
             * 添加请求参数
             */
            // 创建请求参数
            List<NameValuePair> list = new LinkedList<>();
            BasicNameValuePair param1 = new BasicNameValuePair("name", "root");
            BasicNameValuePair param2 = new BasicNameValuePair("password", "123456");
            list.add(param1);
            list.add(param2);
            // 使用URL实体转换工具
            UrlEncodedFormEntity entityParam = new UrlEncodedFormEntity(list, Charset.defaultCharset());
            httpPost.setEntity(entityParam);

            /*
             * 添加请求头信息
             */
            // 浏览器表示
            httpPost.addHeader("User-Agent", "Chrome/51.0.2704.103");
            // 传输的类型
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

            // 执行请求
            response = httpClient.execute(httpPost);
            // 获得响应的实体对象
            HttpEntity entity = response.getEntity();
            // 使用Apache提供的工具类进行转换成字符串
            entityStr = EntityUtils.toString(entity, "UTF-8");

            //[Accept-Ranges: bytes, Cache-Control: max-age=86400, Connection: Keep-Alive,
            //Content-Length: 15852, Content-Type: text/html, Date: Sun, 28 Jul 2019 07:16:39 GMT, Etag: "3dec-57b3a9a43af80",
            //Expires: Mon, 29 Jul 2019 07:16:39 GMT, Last-Modified: Thu, 22 Nov 2018 06:01:50 GMT, P3p: CP=" OTI DSP COR IVA OUR IND COM ", Server: Apache, Set-Cookie: BAIDUID=21439439D71DC8873884411941755280:FG=1;
            //expires=Mon, 27-Jul-20 07:16:39 GMT; max-age=31536000; path=/; domain=.baidu.com; version=1, Vary: Accept-Encoding,User-Agent]
            System.out.println(Arrays.toString(response.getHeaders()));

        } catch (ClientProtocolException e) {
            System.err.println("Http协议出现问题");
            e.printStackTrace();
        } catch (ParseException e) {
            System.err.println("解析错误");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IO异常");
            e.printStackTrace();
        } finally {
            // 释放连接
            if (null != response) {
                try {
                    response.close();
                    httpClient.close();
                } catch (IOException e) {
                    System.err.println("释放连接出错");
                    e.printStackTrace();
                }
            }
        }
        // 打印响应内容
        System.out.println(entityStr);
    }
}
