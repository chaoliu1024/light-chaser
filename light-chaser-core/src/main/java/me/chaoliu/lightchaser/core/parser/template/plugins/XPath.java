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

/**
 * XPath plugin, which using xpath to generate URLs
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class XPath implements Parse {

    public XPath() {
        log.debug("plugin: {}", XPath.class.getSimpleName());
    }

    @Override
    public List<CrawlerMessage> parse(CrawlerMessage crawlerMsg, Element pluginEle) {

        List<CrawlerMessage> reqMsgs = new ArrayList<>();

        Node xpathNode = pluginEle.selectSingleNode("./xpath");
        String xpathValue = xpathNode.getStringValue().trim();

        log.debug("\tthis URL: {}", crawlerMsg.getRequestMsg().getURL());
        log.debug("\tlevel   : {}", pluginEle.attributeValue("level"));
        log.debug("\txpath   : {}", xpathValue);

        HtmlXPath xpath = new HtmlXPath();

        Object result = xpath.parse(crawlerMsg.getResponseMsg().getBody(), xpathValue, crawlerMsg.getHtmlDom().getXpath());

        if (result instanceof NodeList) {

            NodeList nodeList = (NodeList) result;
            for (int i = 0; i < nodeList.getLength(); i++) {
                org.w3c.dom.Node node = nodeList.item(i);
                String newURL = node.getTextContent();
                log.debug("\t\tnew URL: {}", newURL);
                CrawlerMessage reqMsg = ParseAction.generateRequestMessage(crawlerMsg, newURL);
                reqMsgs.add(reqMsg);
            }
        } else if (result instanceof String) {
            String url = result.toString().trim();
            log.debug("\t\tnew URL: {}", url);
            CrawlerMessage reqMsg = ParseAction.generateRequestMessage(crawlerMsg, url);
            reqMsgs.add(reqMsg);
        }

        return reqMsgs;
    }
}
