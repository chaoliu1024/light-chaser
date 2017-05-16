/*
 * Copyright (c) 2015, Chao Liu (chaoliu1024@gmail.com). All rights reserved.
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

package me.chaoliu.lightchaser.core.protocol.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.HashMap;

/**
 * Operations of HTTP
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class HttpClient extends BasicHttpClient {

    private CloseableHttpClient httpClient;

    public HttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public HttpClient() {
        this.httpClient = HttpClients.custom().build();
    }

    public ResponseMessage GET(String url) {
        RequestMessage requestMsg = new RequestMessage(url, new HashMap<>(), null);
        return this.GET(requestMsg);
    }

    @Override
    public ResponseMessage GET(RequestMessage requestMsg) {

        ResponseMessage rspMsg = new ResponseMessage();

        String url = requestMsg.getURL();

        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig());
        addRequestHeader(httpGet, requestMsg.getHeaders());

        try {
            HttpClientContext context = HttpClientContext.create();
            addCookies(context, url, requestMsg.getCookie());

            CloseableHttpResponse response = httpClient.execute(httpGet, context);

            rspMsg = responseHandler(requestMsg, response);

            response.close();
        } catch (IOException e) {
            log.error("Http GET method, IOException exception {}.", e.toString());
        }
        return rspMsg;
    }

    public void close() {
        try {
            this.httpClient.close();
        } catch (IOException e) {
            log.error("close http client error.");
        }
    }

    @Override
    public void HEAD() {
        // TODO Auto-generated method stub
    }

    @Override
    public void PUT(String url) {
        // TODO Auto-generated method stub

    }

    @Override
    public void POST(String url) {
        // TODO Auto-generated method stub

    }

    @Override
    public void TRACE() {
        // TODO Auto-generated method stub

    }

    @Override
    public void OPTIONS() {
        // TODO Auto-generated method stub

    }

    @Override
    public void DELETE() {
        // TODO Auto-generated method stub

    }
}
