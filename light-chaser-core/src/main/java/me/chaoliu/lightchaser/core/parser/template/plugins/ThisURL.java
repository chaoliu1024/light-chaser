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
import me.chaoliu.lightchaser.core.protocol.http.RequestMessage;
import me.chaoliu.lightchaser.core.wrapper.template.Extract;
import me.chaoliu.lightchaser.core.wrapper.template.WrapperEntity;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * return this request URL
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class ThisURL implements Parse, Extract {


    public ThisURL() {
        log.debug("plugin: {}", ThisURL.class.getSimpleName());
    }

    @Override
    public List<CrawlerMessage> parse(CrawlerMessage crawlerMsg, Element pluginEle) {
        List<CrawlerMessage> reqMsgs = new ArrayList<>();

        log.debug("level: {}", pluginEle.attributeValue("level"));

        CrawlerMessage reqMsg = ParseAction.generateRequestMessage(crawlerMsg, crawlerMsg.getRequestMsg().getURL());
        reqMsgs.add(reqMsg);
        return reqMsgs;
    }

    @Override
    public List<String> extract(WrapperEntity entity, Node wrapNode) {
        List<String> results = new ArrayList<>();
        results.add(entity.getUrl());
        return results;
    }
}
