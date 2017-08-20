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

package group.chaoliu.lightchaser.core.protocol.http.concurrent;

import group.chaoliu.lightchaser.core.protocol.http.BasicHttpClient;
import group.chaoliu.lightchaser.core.protocol.http.RequestMessage;
import group.chaoliu.lightchaser.core.protocol.http.RequestMethod;
import group.chaoliu.lightchaser.core.protocol.http.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * TODO
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class AsyncHttpClient extends BasicHttpClient implements Callable<ResponseMessage> {

    private CloseableHttpClient httpClient;
    private HttpContext context;
    private HttpGet httpGet;

    private RequestMessage requestMsg;

    public AsyncHttpClient(RequestMessage requestMsg, RequestMethod requestMethod) {

        this(HttpClients.custom().build(), requestMsg, requestMethod);
    }

    public AsyncHttpClient(CloseableHttpClient httpClient, RequestMessage requestMsg,
                           RequestMethod requestMethod) {

        String url = requestMsg.getURL();
        Map<String, String> requestHeaders = requestMsg.getHeaders();

        this.requestMsg = requestMsg;

        if (RequestMethod.method(requestMethod.getRequestMethod()) == RequestMethod.GET) {
            // "Get" request method
            this.httpGet = new HttpGet(url);
            httpGet.setConfig(requestConfig());
            addRequestHeader(httpGet, requestHeaders);
        } else {
            // TODO !!!!!! other request method
            this.httpGet = new HttpGet(url);
        }
        this.httpClient = httpClient;
        this.context = HttpClientContext.create();
    }

    @Override
    public ResponseMessage call() throws Exception {
        return GET(requestMsg);
    }

    @Override
    public ResponseMessage GET(RequestMessage requestMsg) {

        ResponseMessage rspMsg = null;
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet, context);

            rspMsg = responseHandler(requestMsg, response);

            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rspMsg;
    }
}
