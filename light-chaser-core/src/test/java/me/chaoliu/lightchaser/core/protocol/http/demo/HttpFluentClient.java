package me.chaoliu.lightchaser.core.protocol.http.demo;

import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;

import java.io.IOException;

public class HttpFluentClient {

    public static void main(String[] args) throws IOException {
        // Execute a GET with timeout settings and return response content as String.
        System.out.println(Request.Get("https://www.baidu.com/")
                .connectTimeout(1000)
                .socketTimeout(1000)
                .execute().returnContent().asString());

        Executor executor = Executor.newInstance();
//                .auth(new HttpHost("somehost"), "username", "password")
//                .auth(new HttpHost("myproxy", 8080), "username", "password")
//                .authPreemptive(new HttpHost("myproxy", 8080));
        System.out.println(executor.execute(Request.Get("https://www.baidu.com/")).returnContent().asString());
//        executor.execute(Request.Post("http://somehost/do-stuff").useExpectContinue()
//                .bodyString("Important stuff", ContentType.DEFAULT_TEXT))
//                .returnContent().asString();
    }
}
