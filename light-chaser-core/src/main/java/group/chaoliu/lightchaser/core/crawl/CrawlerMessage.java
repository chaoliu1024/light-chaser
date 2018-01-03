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

package group.chaoliu.lightchaser.core.crawl;

import group.chaoliu.lightchaser.common.protocol.http.ResponseMessage;
import group.chaoliu.lightchaser.common.queue.message.QueueMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * Http message, which contains request message and response message.
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
@Setter
@Getter
public class CrawlerMessage implements Cloneable {

    private Date crawlTime;

    /**
     * html page
     */
    private HtmlDom htmlDom;

    /**
     * json page
     */
    private Object json;

    /**
     * response message
     */
    private ResponseMessage responseMsg;

    private QueueMessage queueMessage;

    public CrawlerMessage() {
        this.queueMessage = new QueueMessage();
        this.responseMsg = new ResponseMessage();
    }

    /**
     * Deep copy
     */
    @Override
    public Object clone() {

        CrawlerMessage crawlerMsg = null;
        try {
            crawlerMsg = (CrawlerMessage) super.clone();
            crawlerMsg.setResponseMsg((ResponseMessage) crawlerMsg.getResponseMsg().clone());
            crawlerMsg.setCrawlTime((Date) (crawlerMsg.getCrawlTime().clone()));
        } catch (CloneNotSupportedException e) {
            log.error("crawl message clone error {}", e);
        }
        return crawlerMsg;
    }

    @Override
    public String toString() {
        return "CrawlerMessage{crawlTime=" + crawlTime +
                ", responseMsg=" + responseMsg + ", queueMessage=" + queueMessage + '}';
    }
}