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

package group.chaoliu.lightchaser.core.parser.template;

import group.chaoliu.lightchaser.core.crawl.CrawlerMessage;
import group.chaoliu.lightchaser.core.crawl.template.CrawlTemplate;
import group.chaoliu.lightchaser.core.crawl.template.CrawlTemplateException;
import group.chaoliu.lightchaser.core.daemon.Job;
import group.chaoliu.lightchaser.core.daemon.planet.Planet;
import group.chaoliu.lightchaser.core.parser.ParseHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.List;

/**
 * JobTemplate parse handler, which parse the result according to crawl_path.xml.
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class TemplateParseHandler implements ParseHandler {

    public static final String MERGE_KEY = "merge";

    // 对应crawl_path.xml中 <level merge="focus">
    public static final String MERGE_XML_VALUE = "focus";

    public static final String FOCUS_ID_KEY = "focus_id";

    public static final String HTTP_METHOD = "method";

    public static final String LEVEL = "level";

    public static final String RESPONSE_HEADERS = "response-headers";


    public static Element crawlLevelElement(CrawlerMessage crawlerMgs) {
        Job job = crawlerMgs.getJob();
        CrawlTemplate crawlConfig = Planet.templateCache.getTemplates().get(job).getCrawlTemplate();
        Element urlLevelsElement = crawlConfig.getUrlLevelsSet();
        Node levelNode = urlLevelsElement.selectSingleNode(String.format("//level[@id=\"%s\"]", crawlerMgs.getURLLevel()));

        if (null == levelNode) {
            throw new CrawlTemplateException("crawl template error! the unknown url level is " + crawlerMgs.getURLLevel());
        }
        if (levelNode.getNodeType() == Element.ELEMENT_NODE) {
            return (Element) levelNode;
        }
        return null;
    }

    @Override
    public ParseResult parse(CrawlerMessage crawlerMgs) {

        ParseResult result = new ParseResult();

        long time1 = System.currentTimeMillis();

        // parse next level urls
        ParseAction parser = new ParseAction();

        Element levelElement = crawlLevelElement(crawlerMgs);

        if (null != levelElement) {

            List<CrawlerMessage> messages = parser.nextLevelMsgs(crawlerMgs, levelElement);

            if (CollectionUtils.isNotEmpty(messages)) {
                result.setMessages(messages);
            }

            for (CrawlerMessage message : messages) {
                log.info("new urls is: {}", message.getRequestMsg().getURL());
            }

            String mergeInfo = levelElement.attributeValue(MERGE_KEY);
            if (StringUtils.isNotBlank(mergeInfo)) {
                result.setMergeInfo(mergeInfo);
                log.debug("merge information is {}", mergeInfo);
            }
        }

        long time2 = System.currentTimeMillis();
        log.info("Parse sub urls cost {} ms.", (time2 - time1));

        return result;
    }
}
