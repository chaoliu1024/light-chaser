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

import org.jsoup.nodes.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class PublishDate {

    public static final String DATE_REGEX = "([\\./\\-_]{0,1}(19|20)\\d{2})[\\./\\-_]{0,1}(([0-3]{0,1}[0-9][\\./\\-_])|(\\w{3,5}[\\./\\-_]))([0-3]{0,1}[0-9][\\./\\-]{0,1})?";

    /**
     * 3 strategies for publishing date extraction. The strategies
     * are descending in accuracy and the next strategy is only
     * attempted if a preferred one fails.
     * 1. Pubdate from URL
     * 2. Pubdate from metadata
     * 3. Raw regex searches in the HTML + added heuristics
     *
     * @param doc
     * @return
     */
    public String publishDate(Document doc, String url) {


        Pattern p = Pattern.compile(DATE_REGEX);
        Matcher matcher = p.matcher(url);
        if(matcher.matches()){
            String date = matcher.group();

        }
        return null;
    }
}
