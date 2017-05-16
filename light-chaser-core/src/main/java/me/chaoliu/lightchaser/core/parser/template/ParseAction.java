/*
 * Copyright (c) 2016, Chao Liu (chaoliu1024@gmail.com). All rights reserved.
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

package me.chaoliu.lightchaser.core.parser.template;

import lombok.extern.slf4j.Slf4j;
import me.chaoliu.lightchaser.core.crawl.CrawlerMessage;
import me.chaoliu.lightchaser.core.protocol.http.RequestMessage;
import me.chaoliu.lightchaser.core.util.Dom4jUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Parse the current URL and html body, by utilizing xpath or regular expression tools, and
 * obtain next level unvisited messages.</br>
 * Parse Action using <b>Abstract Factory Patter</b> of 23 Design Patterns and
 * <b>Dependency Injection</b> to auto load parser plugin
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class ParseAction {

    public static final String PACKAGE = "me.chaoliu.lightchaser.core.parser.template.plugins.";

    /**
     * By using parser plugin to parse parent crawler message, and will get next level unvisited messages.
     *
     * @param crawlerMsg   parent crawler message
     * @param stateElement parent URL state element of crawler configuration XML
     * @return list of next level unvisited messages
     */
    public List<CrawlerMessage> nextLevelMsgs(CrawlerMessage crawlerMsg, Element stateElement) {

        List<CrawlerMessage> messages = new ArrayList<>();

        @SuppressWarnings("unchecked")
        List<Node> pluginNodes = stateElement.selectNodes("./plugin");

        for (Node pluginNode : pluginNodes) {

            List<CrawlerMessage> crawlerMsgs = parseLevelNode(crawlerMsg, pluginNode);
            if (CollectionUtils.isNotEmpty(crawlerMsgs)) {
                for (CrawlerMessage msg : crawlerMsgs) {
                    if (null != msg.getHtmlDom()) {
                        msg.setHtmlDom(null);
                    }
                    if (null != msg.getJson()) {
                        msg.setJson(null);
                    }
                    if (null != msg.getResponseMsg()) {
                        msg.setResponseMsg(null);
                    }
                    messages.add(msg);
                }
            }
        }
        return messages;
    }

    /**
     * Parse one level of crawler message, and get list of sub unvisited crawler messages.
     *
     * @param crawlerMsg parent crawler message
     * @param pNode      plugin node
     * @return list of next level unvisited messages
     */
    public List<CrawlerMessage> parseLevelNode(CrawlerMessage crawlerMsg, Node pNode) {
        Element pluginEle = (Element) pNode;

        // 先进行一次计算, crawl 每一个level肯定有至少一个plugin
        List<CrawlerMessage> crawlerMsgs = pluginAction(crawlerMsg, pluginEle);

        @SuppressWarnings("unchecked")
        List<Node> subPluginNodes = pluginEle.selectNodes("./plugin");

        // if has sub plugins, recursion parse next level msg
        if (CollectionUtils.isNotEmpty(subPluginNodes)) {
            List<CrawlerMessage> results = new ArrayList<>();
            for (Node pluginNode : subPluginNodes) {
                List<CrawlerMessage> temp = new ArrayList<>();
                for (CrawlerMessage msg : crawlerMsgs) {
                    temp.addAll(parseLevelNode(msg, pluginNode));
                }
                results.addAll(temp);
            }
            return results;
        } else {
            return crawlerMsgs;
        }
    }

    /**
     * Using plugin to parse crawler message.
     *
     * @param crawlerMsg current crawler message
     * @param pluginEle  plugin element
     * @return list of next level unvisited messages
     */
    public List<CrawlerMessage> pluginAction(CrawlerMessage crawlerMsg, Element pluginEle) {

        List<CrawlerMessage> crawlerMsgs = new ArrayList<>();

        try {
            String pluginName = pluginEle.attributeValue("class");

            if (StringUtils.isNotBlank(pluginName)) {
                String pluginClass = PACKAGE + pluginName;
                // Dependency Injection
                Parse plugin = (Parse) Class.forName(pluginClass).newInstance();
                List<CrawlerMessage> reqMsgs = plugin.parse(crawlerMsg, pluginEle);

                if (CollectionUtils.isNotEmpty(reqMsgs)) {
                    for (CrawlerMessage msg : reqMsgs) {
                        if (Dom4jUtil.isAttributeNotBlank(pluginEle, "level")) {
                            int urlLevel = Integer.parseInt(pluginEle.attributeValue("level"));
                            msg.setURLLevel(urlLevel);
                        } else {
                            msg.setURLLevel(crawlerMsg.getURLLevel());
                        }
                        // 将返回头设置会下一层的请求头中
                        if (Dom4jUtil.isAttributeNotBlank(pluginEle, "response-headers")) {
                            String headerKey = pluginEle.attributeValue("response-headers").trim();
                            String headerValue = crawlerMsg.getResponseMsg().getResponseHeaders().get(headerKey);
                            msg.getRequestMsg().getHeaders().put(headerKey, headerValue);
                        }
                        crawlerMsgs.add(msg);
                    }
                }
            } else {
                log.info("{} --> attribute 'class' is null", pluginEle.getName());
            }
        } catch (InstantiationException e) {
            log.error("error info {}", e);
        } catch (IllegalAccessException e) {
            log.error("error info {}", e);
        } catch (ClassNotFoundException e) {
            log.error("{} --> class name: {} can not find this class. error info {}", pluginEle.getName(), pluginEle.attributeValue("class"), e.toString());
        }
        return crawlerMsgs;
    }

    /**
     * construct new request message
     *
     * @param msg    old crawler message
     * @param newURL new URL
     * @return new http request message
     */
    public static CrawlerMessage generateRequestMessage(CrawlerMessage msg, String newURL) {

        CrawlerMessage crawlerMessage = new CrawlerMessage();

        RequestMessage reqMsg = new RequestMessage();
        reqMsg.setURL(newURL);
        reqMsg.setHeaders(msg.getRequestMsg().getHeaders());

        Map<String, String> cookies = msg.getRequestMsg().getCookie();
        reqMsg.setCookie(cookies);

        crawlerMessage.setRequestMsg(reqMsg);
        crawlerMessage.setJobName(msg.getJobName());
        crawlerMessage.setJobType(msg.getJobType());

        if (null != msg.getHtmlDom()) {
            crawlerMessage.setHtmlDom(msg.getHtmlDom());
        }
        if (null != msg.getJson()) {
            crawlerMessage.setJson(msg.getJson());
        }
        if (null != msg.getResponseMsg()) {
            crawlerMessage.setResponseMsg(msg.getResponseMsg());
        }
        return crawlerMessage;
    }
}