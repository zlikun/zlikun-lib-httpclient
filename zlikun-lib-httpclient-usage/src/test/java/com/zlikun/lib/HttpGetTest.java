package com.zlikun.lib;

import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.InMemoryDnsResolver;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/6/5 17:15
 */
public class HttpGetTest {

    private Logger log = LoggerFactory.getLogger(HttpGetTest.class);
    private CloseableHttpClient client;

    @Before
    public void init() throws UnknownHostException {

        // 扩展内存DNS解析器，当预定Host不匹配时，使用系统默认DNS解析器
        InMemoryDnsResolver dnsResolver = new InMemoryDnsResolver() {
            @Override
            public InetAddress[] resolve(String host) throws UnknownHostException {
                try {
                    return super.resolve(host);
                } catch (UnknownHostException e) {
                    // 如果内存中未找到，使用系统DNS
                    return new InetAddress[]{Inet4Address.getByName(host)};
                }
            }
        };
//          // zlikun/192.168.70.57
//        dnsResolver.add("api.zlikun.com", Inet4Address.getLocalHost());
//          // /127.0.0.1
//        dnsResolver.add("api.zlikun.com", Inet4Address.getByAddress(new byte [] {127 ,0 ,0 ,1}));
//          // zlikun/127.0.0.1
        dnsResolver.add("api.zlikun.com", Inet4Address.getByAddress("zlikun" ,new byte [] {127 ,0 ,0 ,1}));

        this.client = HttpClientBuilder.create()
                .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0")
                .setConnectionTimeToLive(500, TimeUnit.MILLISECONDS)
                .setMaxConnTotal(20)
//                .setDnsResolver(new SystemDefaultDnsResolver())
                .setDnsResolver(dnsResolver)
                .build();
    }

    @Test
    public void test() {

//        HttpGet request = new HttpGet("http://api.zlikun.com/hello?uname=kevin");
        HttpGet request = new HttpGet("http://localhost/hello?uname=kevin");
        request.addHeader(new BasicHeader("Referer", "http://zlikun.com"));
        request.addHeader("Connection", "keep-alive");

        request.setProtocolVersion(HttpVersion.HTTP_1_1);

        log.info("RequestLine = {}", request.getRequestLine());

        try {
            CloseableHttpResponse response = client.execute(request);

            log.info("StatusLine = {}", response.getStatusLine());

            HttpEntity entity = response.getEntity();
            log.info("response = {}", EntityUtils.toString(entity));
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
