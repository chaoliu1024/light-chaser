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

import group.chaoliu.lightchaser.common.queue.message.QueueMessage;
import group.chaoliu.lightchaser.core.crawl.CrawlerMessage;
import group.chaoliu.lightchaser.core.parser.template.Parse;
import group.chaoliu.lightchaser.core.parser.template.ParseAction;
import group.chaoliu.lightchaser.core.wrapper.template.Extract;
import group.chaoliu.lightchaser.core.wrapper.template.WrapperEntity;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * replace plugin, which using regular expression to replace text content
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class Replace implements Parse, Extract {

    public Replace() {
        log.debug("plugin: {}", Replace.class.getSimpleName());
    }

    @Override
    public List<CrawlerMessage> parse(CrawlerMessage crawlerMsg, Element pluginEle) {

        QueueMessage queueMsg = crawlerMsg.getQueueMessage();

        List<CrawlerMessage> reqMsgs = new ArrayList<>();

        Node regexNode = pluginEle.selectSingleNode("./regex");
        Node resultNode = pluginEle.selectSingleNode("./result");
        log.debug("\tthis URL : {}", queueMsg.getRequestMsg().getURL());
        log.debug("\tlevel    : {}", pluginEle.attributeValue("level"));

        String result = matchReplace(regexNode, resultNode, queueMsg.getRequestMsg().getURL());

        log.debug("\t\tnew URL: {}", result);
        CrawlerMessage reqMsg = ParseAction.generateRequestMessage(crawlerMsg, result);
        reqMsgs.add(reqMsg);
        return reqMsgs;
    }

    @Override
    public List<String> extract(WrapperEntity entity, Node wrapNode) {
        List<String> results = new ArrayList<>();

        Node regexNode = wrapNode.selectSingleNode("./regex");
        Node resultNode = wrapNode.selectSingleNode("./result");
        log.debug("\ttext    : {}", entity.getText());

        String result = matchReplace(regexNode, resultNode, entity.getText());

        log.debug("\t\tresult: {}", result);
        results.add(result);
        return results;
    }

    private String matchReplace(Node regexNode, Node resultNode, String text) {
        String regex = regexNode.getStringValue().trim();
        String resultExp = resultNode.getStringValue().trim();

        log.debug("\tregex    : {}", regex);
        log.debug("\tresultExp: {}", resultExp);

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(text);

        if (matcher.matches()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                log.debug("\tmatcher.group(" + i + "): {}", matcher.group(i));
                resultExp = resultExp.replace("\\" + i, matcher.group(i));
            }
            return resultExp;
        } else {
            return "";
        }
    }
}
