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

package group.chaoliu.lightchaser.wps.newspaper.entity;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

/**
 * Meta information of html.
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class Meta {

    @Getter
    private Map<String, String> metaData = new HashMap<>();

    public Map<String, String> metaInfo(Document doc) {

        Elements metas = doc.select("meta");
        for (Element meta : metas) {
            String key = meta.attr("property").trim();
            if (StringUtils.isBlank(key)) {
                key = meta.attr("name").trim();
            }

            String value = meta.attr("content").trim();
            if (StringUtils.isBlank(value)) {
                value = meta.attr("value").trim();
            }

            if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
                continue;
            }

            metaData.put(key, value);
        }

        return metaData;
    }
}
