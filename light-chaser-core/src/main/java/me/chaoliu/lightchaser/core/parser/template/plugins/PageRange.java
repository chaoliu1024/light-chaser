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

package me.chaoliu.lightchaser.core.parser.template.plugins;

import lombok.extern.slf4j.Slf4j;
import me.chaoliu.lightchaser.core.crawl.CrawlerMessage;
import me.chaoliu.lightchaser.core.parser.template.Parse;
import me.chaoliu.lightchaser.core.parser.template.ParseAction;
import me.chaoliu.lightchaser.core.parser.util.HtmlXPath;
import org.dom4j.Element;
import org.dom4j.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class PageRange implements Parse {

    public PageRange() {
        log.debug("plugin: {}", PageRange.class.getSimpleName());
    }

    @Override
    public List<CrawlerMessage> parse(CrawlerMessage crawlerMsg, Element pluginEle) {
        List<CrawlerMessage> reqMsgs = new ArrayList<>();

        String URL = crawlerMsg.getRequestMsg().getURL();
        log.debug("\tthis URL : {}", URL);
        log.debug("\tlevel    : {}", pluginEle.attributeValue("level"));

        Node xpathNode = pluginEle.selectSingleNode("./xpath");
        Node regexNode = pluginEle.selectSingleNode("./regex");
        Node resultNode = pluginEle.selectSingleNode("./result");

        Node totalPageNode = pluginEle.selectSingleNode("./totalPage");

        if (null != xpathNode && null != regexNode && null != resultNode) {
            List<CrawlerMessage> reqMsg = rangeByXpath(crawlerMsg, xpathNode, regexNode, resultNode);
            reqMsgs.addAll(reqMsg);
        } else if (null != totalPageNode) {
            List<CrawlerMessage> reqMsg = rangeByTotalPage(crawlerMsg, totalPageNode);
            reqMsgs.addAll(reqMsg);
        }
        return reqMsgs;
    }

    private List<CrawlerMessage> rangeByTotalPage(CrawlerMessage crawlerMsg, Node totalPageNode) {
        List<CrawlerMessage> reqMsg;

        String totalPage = totalPageNode.getStringValue().trim();
        log.debug("\ttotalPage: {}", totalPage);
        reqMsg = rangeURLs(crawlerMsg, crawlerMsg.getRequestMsg().getURL(), Integer.parseInt(totalPage));

        return reqMsg;
    }

    private List<CrawlerMessage> rangeByXpath(CrawlerMessage crawlerMsg, Node xpathNode, Node regexNode, Node resultNode) {

        List<CrawlerMessage> reqMsg = new ArrayList<>();

        String xpathValue = xpathNode.getStringValue().trim();
        String regex = regexNode.getStringValue().trim();
        String resultExp = resultNode.getStringValue().trim();

        log.debug("\txpath    : {}", xpathValue);
        log.debug("\tregex    : {}", regex);
        log.debug("\tresultExp: {}", resultExp);

        HtmlXPath xpath = new HtmlXPath();
        Object result = xpath.parse(crawlerMsg.getResponseMsg().getBody(), xpathValue, crawlerMsg.getHtmlDom().getXpath());
        if (result instanceof NodeList) {
            NodeList nodeList = (NodeList) result;
            for (int i = 0; i < nodeList.getLength(); i++) {
                org.w3c.dom.Node node = nodeList.item(i);
                int totalPage = totalPage(node.getTextContent(), regex, resultExp);
                reqMsg = rangeURLs(crawlerMsg, crawlerMsg.getRequestMsg().getURL(), totalPage);

            }
        } else if (result instanceof String) {
            int totalPage = totalPage(result.toString().trim(), regex, resultExp);
            reqMsg = rangeURLs(crawlerMsg, crawlerMsg.getRequestMsg().getURL(), totalPage);
        }
        return reqMsg;
    }

    private int totalPage(String text, String regex, String resultExp) {

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(text);

        if (matcher.matches()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                log.debug("\tmatcher.group(" + i + "): {}", matcher.group(i));
                resultExp = resultExp.replace("\\" + i, matcher.group(i));
            }
        }
        return Integer.parseInt(resultExp);
    }

    private List<CrawlerMessage> rangeURLs(CrawlerMessage crawlerMsg, String URL, int totalPage) {
        List<CrawlerMessage> crawlerMsgs = new ArrayList<>();
        for (int i = 1; i <= totalPage; i++) {
            String newURL = URL + i;
            CrawlerMessage crawlerMessage = ParseAction.generateRequestMessage(crawlerMsg, newURL);
            crawlerMsgs.add(crawlerMessage);
        }
        return crawlerMsgs;
    }
}
