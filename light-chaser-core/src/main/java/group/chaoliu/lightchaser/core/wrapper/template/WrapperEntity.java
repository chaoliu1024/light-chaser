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

package group.chaoliu.lightchaser.core.wrapper.template;

import com.alibaba.fastjson.JSON;
import group.chaoliu.lightchaser.core.crawl.CrawlerMessage;
import group.chaoliu.lightchaser.core.crawl.HtmlDom;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * wrapper entity
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Setter
@Getter
public class WrapperEntity {

    private String url;

    // text content
    private String text;

    // htmlDom
    private HtmlDom htmlDom;

    // json content
    private JSON json;

    // extract information
    private Map result = new HashMap();

    public WrapperEntity(CrawlerMessage crawlerMsg) {
        this.url = crawlerMsg.getRequestMsg().getURL();
        this.text = crawlerMsg.getResponseMsg().getBody();

        if (null != crawlerMsg.getHtmlDom()) {
            this.htmlDom = crawlerMsg.getHtmlDom();
        } else if (null != crawlerMsg.getJson()) {
            this.json = (JSON) crawlerMsg.getJson();
        }
    }

    public WrapperEntity(String url, String text, HtmlDom htmlDom) {
        this.url = url;
        this.text = text;
        this.htmlDom = htmlDom;
    }

    public WrapperEntity(String url, String text, JSON json) {
        this.url = url;
        this.text = text;
        this.json = json;
    }
}