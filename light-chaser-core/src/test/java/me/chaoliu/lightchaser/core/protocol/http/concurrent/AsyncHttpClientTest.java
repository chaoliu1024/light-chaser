/*
 * Copyright (c) 2017, Chao Liu (chaoliu1024@gmail.com). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.chaoliu.lightchaser.core.protocol.http.concurrent;

import me.chaoliu.lightchaser.core.protocol.http.RequestMessage;
import me.chaoliu.lightchaser.core.protocol.http.RequestMethod;
import me.chaoliu.lightchaser.core.protocol.http.ResponseMessage;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncHttpClientTest {

    @Test
    public void testAsyncHttpClient() {

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        // Increase max total connection to 200
        connectionManager.setMaxTotal(200);
        // Increase default max connection per route to 20
        connectionManager.setDefaultMaxPerRoute(20);

        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();

        RequestMessage requestMsg = new RequestMessage();
        requestMsg.setURL("http://longmans1985.blog.163.com/blog/static/70605475201101424324384/");
        RequestMethod requestMethod = RequestMethod.GET;

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient(httpClient, requestMsg, requestMethod);

        ExecutorService exec = Executors.newFixedThreadPool(4);

        Future<ResponseMessage> results = exec.submit(asyncHttpClient);

        try {
            System.out.println(results.get().getBody());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
