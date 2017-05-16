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

package me.chaoliu.lightchaser.core.fission.proxy;

import lombok.extern.slf4j.Slf4j;
import me.chaoliu.lightchaser.core.fission.proxy.domain.ProxyVO;
import me.chaoliu.lightchaser.core.parser.util.HtmlXPath;
import me.chaoliu.lightchaser.core.protocol.http.BasicHttpClient;
import me.chaoliu.lightchaser.core.protocol.http.HttpClient;
import me.chaoliu.lightchaser.core.protocol.http.Proxy;
import me.chaoliu.lightchaser.core.protocol.http.ResponseMessage;
import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.w3c.dom.NodeList;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Proxy validator to check one proxy can be used.
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class ProxyValidator implements Callable<ProxyVO> {

    private Proxy proxy;

    public static final Map<String, ProxyWeb> webProxy = new HashMap<>();

    public static final String WEB_PROXY_URL = "url";
    public static final String WEB_PROXY_XPATH = "xpath";
    public static final String WEB_PROXY_VALUE = "value";

    public ProxyValidator(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public ProxyVO call() throws Exception {
        float costTime = isEffectiveProxy(proxy);
        if (costTime > 0) {
            ProxyVO proxyVO = new ProxyVO();
            proxyVO.setHost(proxy.getHost());
            proxyVO.setPort(proxy.getPort());
            if (StringUtils.isBlank(proxy.getProxyType())) {
                proxyVO.setProxyType("http");
            } else {
                proxyVO.setProxyType(proxy.getProxyType().trim().toLowerCase());
            }
            proxyVO.setUserName(proxy.getUserName());
            proxyVO.setPassword(proxy.getPassword());
            proxyVO.setUsedType(1);
            proxyVO.setInternet(true);
            proxyVO.setCostTime(costTime);
            proxyVO.setFailedNum(0);
            proxyVO.setUpdateTime(new Date());
            return proxyVO;
        } else {
            return null;
        }
    }

    /**
     * if the proxy is ok, return cost time > 0, else cost time = 0.
     *
     * @param proxy proxy
     * @return cost time
     */
    private float isEffectiveProxy(Proxy proxy) {

        log.info(Thread.currentThread().getName() + "\tvalidate {}", proxy);

        float costTime = 0f;

        for (Map.Entry<String, ProxyWeb> web : webProxy.entrySet()) {
            ProxyWeb proxyWeb = web.getValue();
            String url = proxyWeb.getWebURL();
            String xpath = proxyWeb.getValidatorXpath();
            String value = proxyWeb.getXpathValue();

            HttpClientBuilder builder = HttpClients.custom();
            BasicHttpClient.setProxy(builder, proxy.getHost(), proxy.getPort());
            CloseableHttpClient client = builder.build();
            HttpClient httpClient = new HttpClient(client);

            float startTime = System.nanoTime();
            log.debug("validate proxy: host={}, port={}, by url {}.", proxy.getHost(), proxy.getPort(), url);
            ResponseMessage responseMsg = httpClient.GET(url);
            float endTime = System.nanoTime();

            if (null == responseMsg || !isLegalResponse(responseMsg, xpath, value)) {
                return costTime;
            }
            costTime = (endTime - startTime) / 1000000000.0f;
        }
        log.debug("proxy--> host={}, port={} is successful, using {}", proxy.getHost(), proxy.getPort(), costTime);
        return costTime;
    }

    /**
     * Check the response is legal or not.
     *
     * @param responseMsg response message
     * @param xpath       xpath
     * @return if the legal response, return true
     */
    private boolean isLegalResponse(ResponseMessage responseMsg, String xpath, String value) {
        String body = responseMsg.getBody();
        HtmlXPath htmlXPath = new HtmlXPath();

        Object object = htmlXPath.parse(body, xpath);

        if (object instanceof NodeList) {
            NodeList nodeList = (NodeList) object;
            for (int i = 0; i < nodeList.getLength(); i++) {
                org.w3c.dom.Node node = nodeList.item(i);
                String nodeText = node.getTextContent();
                if (StringUtils.isNotBlank(nodeText) && nodeText.contains(value)) {
                    log.info("validator proxy node text is {}", nodeText);
                    return true;
                }
            }
        } else if (object instanceof String) {
            String nodeText = object.toString().trim();
            if (StringUtils.isNotBlank(nodeText) && nodeText.contains(value)) {
                log.info("validator proxy node text is {}", nodeText);
                return true;
            }
        }
        return false;
    }
}
