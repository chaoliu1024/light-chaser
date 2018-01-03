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

package group.chaoliu.lightchaser.core.fission.proxy;

import group.chaoliu.lightchaser.common.protocol.http.Proxy;
import group.chaoliu.lightchaser.common.protocol.http.ResponseMessage;
import group.chaoliu.lightchaser.core.fission.proxy.domain.ProxyPO;
import group.chaoliu.lightchaser.core.parser.util.HtmlXPath;
import group.chaoliu.lightchaser.core.protocol.http.BasicHttpClient;
import group.chaoliu.lightchaser.core.protocol.http.HttpClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.NodeList;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Proxy validator to check one proxy can be used.
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class ProxyValidator implements Callable<ProxyPO> {

    private Proxy proxy;

    /**
     * 有序Map
     */
    public static final Map<String, ProxyWeb> WEB_PROXY = new LinkedHashMap<>();

    public static final String WEB_PROXY_URL = "url";
    public static final String WEB_PROXY_XPATH = "xpath";
    public static final String WEB_PROXY_VALUE = "value";

    public ProxyValidator(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public ProxyPO call() throws Exception {
        ValidatorResult res = null;

        ProxyPO proxyPO = new ProxyPO();
        try {
            res = isEffectiveProxy(proxy);
        } catch (Exception e) {
            log.error("validate proxy fail {}.", proxy);
            return proxyPO;
        }

        try {
            proxyPO.setHost(proxy.getHost());
            proxyPO.setPort(proxy.getPort());
            if (StringUtils.isBlank(proxy.getProxyType())) {
                proxyPO.setProxyType("http");
            } else {
                proxyPO.setProxyType(proxy.getProxyType().trim().toLowerCase());
            }
            proxyPO.setUserName(proxy.getUserName());
            proxyPO.setPassword(proxy.getPassword());
            proxyPO.setInternet(true);
            proxyPO.setDomainKey(proxy.getDomainKey());
            proxyPO.setCrawlTime(proxy.getCrawlTime());
            proxyPO.setUpdateTime(proxy.getCrawlTime());

            if (null != res) {
                proxyPO.setCostTime(res.getCostTime());
                proxyPO.setLevel(res.proxyLevel);
                proxyPO.setFailedCount(0);
            } else {
                if (null != proxy.getFailedCount()) {
                    // 更新验证代理
                    proxyPO.setCostTime(0);
                    proxyPO.setLevel(0);
                    proxyPO.setFailedCount(proxy.getFailedCount() + 1);
                } else {
                    // 抓取的新代理
                    return new ProxyPO();
                }
            }
        } catch (Exception e) {
            return new ProxyPO();
        }
        return proxyPO;
    }

    /**
     * if the proxy is ok, return cost time > 0, else cost time = 0.
     *
     * @param proxy proxy
     * @return cost time
     */
    private ValidatorResult isEffectiveProxy(Proxy proxy) {

        log.info(Thread.currentThread().getName() + "\tvalidate {}", proxy);

        int proxyLevel = 0;

        ValidatorResult res = new ValidatorResult();

        for (Map.Entry<String, ProxyWeb> web : WEB_PROXY.entrySet()) {
            ProxyWeb proxyWeb = web.getValue();
            log.info("use " + web.getKey() + " to validate");
            String url = proxyWeb.getWebURL();
            String xpath = proxyWeb.getValidatorXpath();
            String value = proxyWeb.getXpathValue();

            HttpClient httpClient = BasicHttpClient.buildProxyHttpClient(proxy);
            log.debug("validate proxy: host={}, port={}, by url {}", proxy.getHost(), proxy.getPort(), url);
            float startTime = System.nanoTime();
            ResponseMessage responseMsg = httpClient.GET(url);
            float endTime = System.nanoTime();
            if (null == responseMsg || !isLegalResponse(responseMsg, xpath, value)) {
                if (res.getProxyLevel() == 0) {
                    return null;
                } else {
                    log.info("proxy--> host={}, port={} is successful, using {}s, level is {}",
                            proxy.getHost(), proxy.getPort(), res.getCostTime(), res.getProxyLevel());
                    return res;
                }
            }
            float costTime = (endTime - startTime) / 1000000000.0f;
            // 保留时间最久的costTime
            if (res.getCostTime() < costTime) {
                res.setCostTime(costTime);
            }
            res.setProxyLevel(++proxyLevel);
            httpClient.close();
        }
        log.info("proxy--> host={}, port={} is successful, using {}s, level is {}",
                proxy.getHost(), proxy.getPort(), res.getCostTime(), res.getProxyLevel());
        return res;
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
        if (StringUtils.isBlank(body)) {
            return false;
        }

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

    /**
     * Result of one proxy validator.
     */
    @Setter
    @Getter
    private class ValidatorResult {
        float costTime = 0f;
        int proxyLevel = 0;
    }
    
}