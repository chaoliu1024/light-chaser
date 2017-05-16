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
import me.chaoliu.lightchaser.core.daemon.Star;
import me.chaoliu.lightchaser.core.crawl.CrawlerMessage;
import me.chaoliu.lightchaser.core.crawl.template.CrawlTemplate;
import me.chaoliu.lightchaser.core.crawl.template.CrawlTemplateException;
import me.chaoliu.lightchaser.core.parser.ParseHandler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.List;

/**
 * Template parse handler, which parse the result according to crawl_path.xml.
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class TemplateParseHandler implements ParseHandler {

    private static final String MERGE_KEY = "merge";

    @Override
    public ParseResult parse(CrawlerMessage crawlerMgs) {

        ParseResult result = new ParseResult();

        long time1 = System.currentTimeMillis();

        String jobName = crawlerMgs.getJobName();
        // parse next level urls
        ParseAction parser = new ParseAction();
        // get from cache template
        CrawlTemplate crawlConfig = Star.templateCache.getTemplates().get(jobName).getCrawlTemplate();
        Element urlLevelsElement = crawlConfig.getUrlLevelsSet();
        Node levelNode = urlLevelsElement.selectSingleNode(String.format("//level[@id=\"%s\"]", crawlerMgs.getURLLevel()));

        if (null == levelNode) {
            throw new CrawlTemplateException("crawl template error...");
        }
        if (levelNode.getNodeType() == Element.ELEMENT_NODE) {
            Element stateElement = (Element) levelNode;
            List<CrawlerMessage> messages = parser.nextLevelMsgs(crawlerMgs, stateElement);

            if (CollectionUtils.isNotEmpty(messages)) {
                result.setMessages(messages);
            }

            String mergeInfo = stateElement.attributeValue(MERGE_KEY);
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
