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

package me.chaoliu.lightchaser.wps.newspaper.entity;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class Author {

    public String author(Document doc) {

        String[] attrs = new String[]{"name", "rel", "itemprop", "class", "id"};
        String[] values = new String[]{"author", "byline", "dc.creator"};

        Elements authorNodes = new Elements();
        List<String> authors = new ArrayList<>();

        for (String attr : attrs) {
            for (String value : values) {
                Elements elements = doc.getElementsByAttributeValue(attr, value);
                if (!elements.isEmpty()) {
                    authorNodes.addAll(elements);
                }
            }
        }
        for (Element node : authorNodes) {
            if ("meta".equals(node.tagName().toLowerCase())) {
                String content = node.attr("content");
                if (StringUtils.isNotBlank(content)) {
                    authors.add(content);
                }
            } else {
                if (StringUtils.isNotBlank(node.text())) {
                    authors.add(node.text());
                }
            }
        }
        return authors.toString();
    }
}
