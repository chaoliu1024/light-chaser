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

package me.chaoliu.lightchaser.core.test;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 * Concurrent operations of HTTP
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */

// http://codereview.stackexchange.com/questions/128367/efficiently-using-apache-httpclient-in-multithreaded-environment

// http://www.cnblogs.com/abinxm/p/4929629.html
public class ConcurrentHttpClient {

    public static PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

    public static HttpClientBuilder httpclient = HttpClients.custom();

    public static void main(String[] args) throws Exception {

        // Create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will
        // be using the HttpClient.
        cm.setMaxTotal(100);

        CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();

        try {
            // create an array of URIs to perform GETs on
            String[] urisToGet = {
                    "http://hc.apache.org/",
                    "http://hc.apache.org/httpcomponents-core-ga/",
                    "http://hc.apache.org/httpcomponents-client-ga/",
            };

            // create a thread for each URI
            GetThread[] threads = new GetThread[urisToGet.length];
            for (int i = 0; i < threads.length; i++) {
                HttpGet httpget = new HttpGet(urisToGet[i]);
                threads[i] = new GetThread(httpclient, httpget, i + 1);
            }

            // start the threads
            for (int j = 0; j < threads.length; j++) {
                threads[j].start();
            }

            // join the threads
            for (int j = 0; j < threads.length; j++) {
                threads[j].join();
            }

        } finally {
            httpclient.close();
        }
    }

    /**
     * A thread that performs a GET.
     */
    static class GetThread extends Thread {

        private final CloseableHttpClient httpClient;
        private final HttpContext context;
        private final HttpGet httpget;
        private final int id;

        public GetThread(CloseableHttpClient httpClient, HttpGet httpget, int id) {
            this.httpClient = httpClient;
            this.context = new BasicHttpContext();
            this.httpget = httpget;
            this.id = id;
        }

        /**
         * Executes the GetMethod and prints some status information.
         */
        @Override
        public void run() {
            try {
                System.out.println(id + " - about to get something from " + httpget.getURI());
                CloseableHttpResponse response = httpClient.execute(httpget, context);
                try {
                    System.out.println(id + " - get executed");
                    // get the response body as an array of bytes
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        byte[] bytes = EntityUtils.toByteArray(entity);
                        System.out.println(id + " - " + bytes.length + " bytes read");
                    }
                } finally {
                    response.close();
                }
            } catch (Exception e) {
                System.out.println(id + " - error: " + e);
            }
        }

    }
}
