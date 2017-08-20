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
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * get next page
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class NextPage implements Parse {

    public NextPage() {
        log.debug("plugin: {}", NextPage.class.getSimpleName());
    }

    @Override
    public List<CrawlerMessage> parse(CrawlerMessage crawlerMsg, Element pluginEle) {
        List<CrawlerMessage> reqMsgs = new ArrayList<>();

        Node xpathNode = pluginEle.selectSingleNode("./xpath");
        Node regexNode = pluginEle.selectSingleNode("./regex");
        Node replaceNode = pluginEle.selectSingleNode("./replace");

        String xpathValue = xpathNode.getStringValue().trim();
        String regex = regexNode.getStringValue().trim();
        String replace = replaceNode.getStringValue().trim();

        log.debug("\tthis URL: {}", crawlerMsg.getRequestMsg().getURL());
        log.debug("\tlevel   : {}", pluginEle.attributeValue("level"));
        log.debug("\txpath   : {}", xpathValue);
        log.debug("\tregex   : {}", regex);
        log.debug("\treplace : {}", replace);

        int totalPage = 0;
        if (null != pluginEle.selectNodes("./totalPage")) {
            Node totalPageNode = pluginEle.selectSingleNode("./totalPage");
            totalPage = Integer.parseInt(totalPageNode.getStringValue());
            log.debug("\ttotalPage:  {}" + totalPage);
        }

        String endPageTag = null;
        if (null != pluginEle.selectNodes("./endPageTag")) {
            Node endPageTagNode = pluginEle.selectSingleNode("./endPageTag");
            endPageTag = endPageTagNode.getStringValue();
            log.debug("\tendPageTag: {}", endPageTag);
        }

        HtmlXPath xpath = new HtmlXPath();

        Object result = xpath.parse(crawlerMsg.getResponseMsg().getBody(), xpathValue, crawlerMsg.getHtmlDom().getXpath());

        if (result instanceof NodeList) {
            NodeList nodeList = (NodeList) result;
            for (int i = 0; i < nodeList.getLength(); i++) {
                org.w3c.dom.Node node = nodeList.item(i);
                String URL = node.getTextContent();
                CrawlerMessage reqMsg = replaceURL(regex, URL, replace, totalPage, endPageTag, crawlerMsg);
                if (null != reqMsg) {
                    reqMsgs.add(reqMsg);
                }
            }
        } else if (result instanceof String) {
            String URL = result.toString().trim();
            CrawlerMessage reqMsg = replaceURL(regex, URL, replace, totalPage, endPageTag, crawlerMsg);
            if (null != reqMsg) {
                reqMsgs.add(reqMsg);
            }

        }
        return reqMsgs;
    }

    private CrawlerMessage replaceURL(String regex, String URL, String replace, int totalPage,
                                      String endPageTag, CrawlerMessage crawlerMsg) {

        if (StringUtils.isBlank(URL)) {
            return null;
        }
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(URL);

        String newURL = "";

        if (matcher.matches()) {
            for (int j = 1; j <= matcher.groupCount(); j++) {
                newURL = replace.replace("\\" + j, matcher.group(j));
            }
        }

        if (totalPage > 0 && null != endPageTag) {
            if (isEndPage(newURL, endPageTag, totalPage)) {
                log.debug("\t\tend page: {}", newURL);
                return null;
            }
        }

        log.debug("\t\tnew URL: {}", newURL);
        return ParseAction.generateRequestMessage(crawlerMsg, newURL);
    }

    /**
     * Check whether the URL is last page URL we need.
     *
     * @param newURL     the new generation URL
     * @param endPageTag last page regular expression
     * @param totalPage  total page we need
     * @return is end page or not
     */
    private boolean isEndPage(String newURL, String endPageTag, int totalPage) {

        boolean isEndPage = false;

        Pattern endPagePattern = Pattern.compile(endPageTag);
        Matcher endPageMatcher = endPagePattern.matcher(newURL);
        if (endPageMatcher.matches()) {
            int pageNum = Integer.parseInt(endPageMatcher.group(1));
            if (totalPage == pageNum) {
                isEndPage = true;
            }
        }
        return isEndPage;
    }
}
