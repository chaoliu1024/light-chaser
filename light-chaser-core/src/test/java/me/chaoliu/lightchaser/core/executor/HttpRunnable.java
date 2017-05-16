package me.chaoliu.lightchaser.core.executor;


import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

public class HttpRunnable implements Runnable {

    private String url;
    private HttpClientBuilder httpClientBuilder;
    public HttpRunnable(String url, HttpClientBuilder httpClientBuilder) {
        this.url = url;
        this.httpClientBuilder = httpClientBuilder;
    }

    @Override
    public void run() {


    }
}
