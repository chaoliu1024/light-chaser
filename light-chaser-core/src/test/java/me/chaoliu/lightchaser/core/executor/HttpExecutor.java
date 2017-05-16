package me.chaoliu.lightchaser.core.executor;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpExecutor {

    ExecutorService executor = Executors.newFixedThreadPool(10);

    HttpClientBuilder httpClientBuilder = HttpClients.custom();


}
