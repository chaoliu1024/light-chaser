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

package group.chaoliu.lightchaser.core.parser.template.plugins;

import group.chaoliu.lightchaser.core.crawl.CrawlerMessage;
import group.chaoliu.lightchaser.core.parser.template.Parse;
import group.chaoliu.lightchaser.core.parser.template.ParseAction;
import group.chaoliu.lightchaser.core.parser.util.HtmlXPath;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * multiple concat plugin
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class MultipleConcat implements Parse {

    public MultipleConcat() {
        log.debug("plugin: {}", MultipleConcat.class.getSimpleName());
    }

    @Override
    public List<CrawlerMessage> parse(CrawlerMessage crawlerMsg, Element pluginEle) {

        List<CrawlerMessage> reqMsgs = new ArrayList<>();

        log.debug("\tthis URL: {}", crawlerMsg.getRequestMsg().getURL());
        log.debug("\tlevel   : {}", pluginEle.attributeValue("level"));

        @SuppressWarnings("unchecked")
        Iterator<Element> partIt = pluginEle.elementIterator("part");
        List<String> newURLs = new ArrayList<>();

        HtmlXPath xpath = new HtmlXPath();

        while (partIt.hasNext()) {
            Element partEle = partIt.next();

            String partContent = partEle.getTextTrim();

            if ("XPath".equals(partEle.attributeValue("type"))) {

                List<String> temps = new ArrayList<>();

                Object result = xpath.parse(crawlerMsg.getResponseMsg().getBody(), partContent, crawlerMsg.getHtmlDom().getXpath());

                if (result instanceof NodeList) {
                    NodeList nodeList = (NodeList) result;
                    if (nodeList.getLength() == 0) {
                        return reqMsgs;
                    }
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node node = nodeList.item(i);
                        temps.add(node.getNodeValue());
                    }
                    newURLs = concat(newURLs, temps);
                } else if (result instanceof String) {
                    temps.add(result.toString().trim());
                    newURLs = concat(newURLs, temps);
                }
            } else if ("URL".equals(partEle.attributeValue("type"))) {

                Pattern p = Pattern.compile(partContent);
                Matcher matcher = p.matcher(crawlerMsg.getRequestMsg().getURL());

                if (matcher.matches()) {
                    List<String> temps = new ArrayList<>();
                    temps.add(matcher.group(1));
                    newURLs = concat(newURLs, temps);
                } else {
                    return reqMsgs;
                }
            } else {
                List<String> temps = new ArrayList<>();
                temps.add(partContent);
                newURLs = concat(newURLs, temps);
            }
        }

        for (String newURL : newURLs) {
            log.debug("\t\tnew URL: {}", newURL);
            CrawlerMessage reqMsg = ParseAction.generateRequestMessage(crawlerMsg, newURL);
            reqMsgs.add(reqMsg);
        }
        return reqMsgs;
    }

    private List<String> concat(List<String> newURLs, List<String> temps) {

        List<String> result = new ArrayList<>();
        if (!newURLs.isEmpty() && !temps.isEmpty()) {
            for (String url : newURLs) {
                for (String temp : temps) {
                    result.add(url + temp);
                }
            }
        } else if (newURLs.isEmpty() && !temps.isEmpty()) {
            result.addAll(temps);
        }
        return result;
    }
}