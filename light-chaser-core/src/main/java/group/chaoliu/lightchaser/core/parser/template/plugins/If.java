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

package group.chaoliu.lightchaser.core.parser.template.plugins;

import group.chaoliu.lightchaser.common.queue.message.QueueMessage;
import group.chaoliu.lightchaser.core.crawl.CrawlerMessage;
import group.chaoliu.lightchaser.core.parser.template.Parse;
import group.chaoliu.lightchaser.core.parser.template.ParseAction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * If plugin
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class If implements Parse {

    /**
     * find 每个元素的"并"关系, 都满足才算 找到
     */
    private static final String RELATION_AND = "and";

    /**
     * find 每个元素的"或"关系, 其中一个或多个元素可以没有找到
     */
    private static final String RELATION_OR = "or";

    public If() {
        log.debug("plugin: {}", If.class.getSimpleName());
    }

    @Override
    public List<CrawlerMessage> parse(CrawlerMessage crawlerMsg, Element pluginEle) {

        QueueMessage queueMsg = crawlerMsg.getQueueMessage();

        List<CrawlerMessage> reqMsgs = new ArrayList<>();

        String url = queueMsg.getRequestMsg().getURL();

        log.debug("\tthis URL: {}", url);
        log.debug("\tlevel   : {}", pluginEle.attributeValue("level"));

        @SuppressWarnings("unchecked")
        Iterator<Element> findIt = pluginEle.elementIterator("find");

        while (findIt.hasNext()) {
            Element e = findIt.next();

            String text = e.getTextTrim().trim();

            String relation = RELATION_OR;
            relation = e.attributeValue("relation").trim();

            if ("URL".equals(e.attributeValue("type"))) {
                Pattern p = Pattern.compile(text);
                Matcher matcher = p.matcher(queueMsg.getRequestMsg().getURL());

                if (matcher.matches()) {
                    for (int i = 1; i <= matcher.groupCount(); i++) {
                        String s = matcher.group(i);
                        if (RELATION_AND.equals(relation) && StringUtils.isBlank(s)) {
                            return new ArrayList<>();
                        }
                    }
                } else {
                    return new ArrayList<>();
                }

            } else if ("Page".equals(e.attributeValue("type"))) {

            }
        }

        CrawlerMessage reqMsg = ParseAction.generateRequestMessage(crawlerMsg, url);

        reqMsgs.add(reqMsg);
        return reqMsgs;
    }
}
