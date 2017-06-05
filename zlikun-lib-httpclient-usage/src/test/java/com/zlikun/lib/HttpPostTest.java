package com.zlikun.lib;

import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.InMemoryDnsResolver;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/6/5 17:36
 */
public class HttpPostTest {

    private Logger log = LoggerFactory.getLogger(HttpGetTest.class);
    private CloseableHttpClient client;

    @Before
    public void init() throws UnknownHostException {

        this.client = HttpClientBuilder.create()
                .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0")
                .setConnectionTimeToLive(500, TimeUnit.MILLISECONDS)
                .setMaxConnTotal(20)
                .setDnsResolver(new SystemDefaultDnsResolver())
                .build();
    }

    @Test
    public void test() {

        HttpPost request = new HttpPost("http://localhost/hello") ;
        // 添加参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>() ;
        nvps.add(new BasicNameValuePair("uname", "kevin"));
        try {
            request.setEntity(new UrlEncodedFormEntity(nvps));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        request.addHeader(new BasicHeader("Referer", "http://zlikun.com"));
        request.addHeader("Accept" ,"application/json");
        request.addHeader("Connection", "keep-alive");

        request.setProtocolVersion(HttpVersion.HTTP_1_1);

        log.info("RequestLine = {}" ,request.getRequestLine());

        try {
            CloseableHttpResponse response = client.execute(request) ;

            log.info("StatusLine = {}" ,response.getStatusLine());

            HttpEntity entity = response.getEntity() ;
            log.info("response = {}" , EntityUtils.toString(entity));
            EntityUtils.consumeQuietly(entity);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @After
    public void destroy() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
