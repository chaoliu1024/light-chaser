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

package group.chaoliu.lightchaser.core.protocol.http;

import group.chaoliu.lightchaser.core.persistence.ImageStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.tika.detect.EncodingDetector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.html.HtmlEncodingDetector;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public abstract class BasicHttpClient implements HttpMethods {

    private static final int SOCKET_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 5000;
    private static final int CONNECTION_REQUEST_TIMEOUT = 5000;

    private static final int MAX_TOTAL = 200;
    private static final int MAX_PER_ROUTE = 20;

    protected RequestConfig requestConfig() {
        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setConnectTimeout(SOCKET_TIMEOUT);
        builder.setSocketTimeout(CONNECT_TIMEOUT);
        builder.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT);
        return builder.build();
    }

    /**
     * Setting configuration of Pool Connection.
     *
     * @return PoolingHttpClientConnectionManager
     */
    private PoolingHttpClientConnectionManager poolConnectionManagerConfig() {

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

        // Increase max total connection to 200
        connectionManager.setMaxTotal(MAX_TOTAL);
        // Increase default max connection per route to 20
        connectionManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);
        return connectionManager;
    }

    /**
     * set response header to response message
     *
     * @param rspMsg     response message
     * @param allHeaders response headers
     */
    protected void responseHeaders(ResponseMessage rspMsg, Header[] allHeaders) {
        Map<String, String> responseHeaders = new HashMap<>();
        for (Header h : allHeaders) {
            responseHeaders.put(h.getName(), h.getValue());
        }
        rspMsg.setResponseHeaders(responseHeaders);
    }

    /**
     * add cookies to http client
     *
     * @param context HttpClientContext
     * @param cookies cookies
     */
    protected void addCookies(HttpContext context, String url, Map<String, String> cookies) {

        if (null != cookies && !cookies.isEmpty()) {
            BasicCookieStore cookieStore = new BasicCookieStore();
            BasicClientCookie cookie = null;
            for (Map.Entry<String, String> ck : cookies.entrySet()) {
                cookie = new BasicClientCookie(ck.getKey(), ck.getValue());
                cookie.setAttribute(ck.getKey(), ck.getValue());
            }

            cookie.setDomain(getDomainName(url));
            cookie.setPath("/");

            cookieStore.addCookie(cookie);
            context.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
        }
    }

    protected void addRequestHeader(HttpRequestBase httpRequest, Map<String, String> headers) {
        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * encode the response page by response header "Content-Type"
     * like is "Content-Type:text/html; charset=gb2312"
     *
     * @param entity http entity
     * @return page content
     */
    public String encodeContentByResponseHeader(HttpEntity entity) {

        String charset;
        String page = "";

        if (null != entity.getContentEncoding()) {
            String contentEncoding = entity.getContentEncoding().getValue();
            log.debug("ContentEncoding in HttpEntity is: {}", contentEncoding);
        }
        if (null != entity.getContentType()) {
            String contentType = entity.getContentType().getValue();
            log.debug("ContentType in HttpEntity is: {}", contentType);
            Pattern p = Pattern.compile(".*charset=(.*)");
            Matcher m = p.matcher(contentType);
            if (m.matches()) {
                charset = m.group(1);
                try {
                    page = EntityUtils.toString(entity, charset);
                } catch (IOException e) {
                    log.error("encode page exception {}", e.getMessage());
                }
            }
        }
        return page;
    }

    /**
     * encode the response page by html head mete
     * like is "<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />"
     *
     * @param content byte of web page content
     * @return String of web page content
     */
    public String encodeContentByHTMLMeta(byte[] content) {
        EncodingDetector encodingDetector = new HtmlEncodingDetector();
        String page = "";
        try {
            Charset charset = encodingDetector.detect(new ByteArrayInputStream(content), new Metadata());

            if (charset == null) {
                page = new String(content);
            } else {
                page = new String(content, charset);
            }
        } catch (IOException e) {
            log.debug("Fail to encode page by html meta.");
        }
        return page;
    }

    protected ResponseMessage responseHandler(RequestMessage requestMsg, CloseableHttpResponse response) {

        ResponseMessage rspMsg = new ResponseMessage();

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode > 400) {
            log.error("{} request error, http code is {}", requestMsg.getURL(), statusCode);
        }

        responseHeaders(rspMsg, response.getAllHeaders());

        HttpEntity entity = response.getEntity();

        if (ImageStore.isImageURL(requestMsg.getURL())) {
            try {
                InputStream content = entity.getContent();
                ImageStore.save(content, requestMsg.getURL());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return rspMsg;
        }


        if (entity != null) {
            // response body
            // first encode page content by response header
            String body = encodeContentByResponseHeader(entity);
            if (StringUtils.isBlank(body)) {
                // if encode response header is null, than encode page by mete <> of response page
                byte[] entityBytes = new byte[0];
                try {
                    entityBytes = EntityUtils.toByteArray(entity);
                } catch (IOException e) {
                    log.debug("html entity to byte array error.");
                }
                body = encodeContentByHTMLMeta(entityBytes);
            }
            if (StringUtils.isNotBlank(body)) {
                rspMsg.setBody(body);
            }
        }

        return rspMsg;
    }

    /**
     * get domain name of an url
     *
     * @param url url
     * @return domain name
     */
    public static String getDomainName(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /**
     * using proxy to connect
     *
     * @param host   proxy host
     * @param port   proxy port
     * @param scheme proxy scheme
     */
    public static void setProxy(HttpClientBuilder httpClientBuilder, String host, int port, String scheme) {
        HttpHost proxy;
        if (StringUtils.isNotBlank(scheme)) {
            proxy = new HttpHost(host, port, scheme);
        } else {
            proxy = new HttpHost(host, port);
        }
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        httpClientBuilder.setRoutePlanner(routePlanner);
    }

    /**
     * using proxy to connect
     *
     * @param host proxy host
     * @param port proxy port
     */
    public static void setProxy(HttpClientBuilder httpClientBuilder, String host, int port) {
        setProxy(httpClientBuilder, host, port, "");
    }

    /**
     * Create http client which using proxy.
     *
     * @param proxy proxy
     * @return http client
     */
    public static HttpClient buildProxyHttpClient(Proxy proxy) {
        HttpClientBuilder builder = HttpClients.custom();
        setProxy(builder, proxy.getHost(), proxy.getPort());
        CloseableHttpClient client = builder.build();
        return new HttpClient(client);
    }

    @Override
    public ResponseMessage GET(RequestMessage requestMsg) {
        return null;
    }

    @Override
    public ResponseMessage POST(RequestMessage requestMsg) {
        return null;
    }

    @Override
    public void PUT(String url) {

    }

    @Override
    public void HEAD() {

    }

    @Override
    public void TRACE() {

    }

    @Override
    public void OPTIONS() {

    }

    @Override
    public void DELETE() {

    }
}
