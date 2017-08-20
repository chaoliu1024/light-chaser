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

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Calculate title of HTML.
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class Title {

    private static final String PIPE_SPLITTER = "|";
    private static final String DASH_SPLITTER = " - ";
    private static final String UNDERSCORE_SPLITTER = "_";
    private static final String SLASH_SPLITTER = "/";
    private static final String ARROWS_SPLITTER = " » ";
    private static final String COLON_SPLITTER = ":";
    private static final String SPACE_SPLITTER = " ";

    /**
     * Fetch the article title and analyze it
     * Assumptions:
     * <ul>
     * <li>title tag is the most reliable (inherited from Goose)</li>
     * <li>h1, if properly detected, is the best (visible to users)</li>
     * <li>og:title and h1 can help improve the title extraction</li>
     * </ul>
     * Explicit rules:
     * <ol>
     * <li>title == h1, no need to split</li>
     * <li>h1 similar to og:title, use h1</li>
     * <li>title contains h1, title contains og:title, len(h1) > len(og:title), use h1</li>
     * <li>title starts with og:title, use og:title</li>
     * <li>use title, after splitting</li>
     * </ol>
     *
     * @return title of html page
     */
    public String title(Document doc) {

        String title = "";

        String h1Text = "";
        String titleText = "";

        boolean usedDelimeter = false;

        Elements titles = doc.getElementsByTag("title");
        if (null == titles || titles.isEmpty()) {
            return title;
        }
        titleText = titles.get(0).ownText();

        Elements h1Eles = doc.getElementsByTag("h1");
        List<String> h1Texts = new ArrayList<>();

        for (Element h1 : h1Eles) {
            h1Texts.add(h1.ownText());
        }

        if (!CollectionUtils.isEmpty(h1Texts)) {
            Collections.sort(h1Texts);

            h1Text = h1Texts.get(h1Texts.size() - 1);
            // discard too short texts
            if (h1Text.split(" ").length <= 2) {
                h1Text = "";
            }
        }

        // check for better alternatives for title_text and possibly skip splitting
        if (h1Text.equals(titleText)) {
            usedDelimeter = true;
        }

        if (!usedDelimeter && titleText.contains(PIPE_SPLITTER)) {
            titleText = splitTitle(titleText, PIPE_SPLITTER, h1Text);
            usedDelimeter = true;
        }
        if (!usedDelimeter && titleText.contains(DASH_SPLITTER)) {
            titleText = splitTitle(titleText, DASH_SPLITTER, h1Text);
            usedDelimeter = true;
        }
        if (!usedDelimeter && titleText.contains(UNDERSCORE_SPLITTER)) {
            titleText = splitTitle(titleText, UNDERSCORE_SPLITTER, h1Text);
            usedDelimeter = true;
        }
        if (!usedDelimeter && titleText.contains(SLASH_SPLITTER)) {
            titleText = splitTitle(titleText, SLASH_SPLITTER, h1Text);
            usedDelimeter = true;
        }
        if (!usedDelimeter && titleText.contains(ARROWS_SPLITTER)) {
            titleText = splitTitle(titleText, ARROWS_SPLITTER, h1Text);
            usedDelimeter = true;
        }

        if (StringUtils.isNotBlank(titleText)) {
            title = titleText;
        }

        return title;
    }

    /**
     * Split the title to best part possible.
     *
     * @param titleText title node text
     * @param separator separator
     * @param h1Text    long h1 node text
     * @return most best title text
     */
    private String splitTitle(String titleText, String separator, String h1Text) {

        String removePattern = "[^a-zA-Z0-9\\ ]";
        String[] splitTitle = titleText.split(separator);

        if (StringUtils.isNotBlank(h1Text)) {
            h1Text = h1Text.replaceAll(removePattern, "").toLowerCase();
        }

        int largeTitleLength = 0;
        // find the largest title piece
        for (String partTitle : splitTitle) {
            if (StringUtils.isNotBlank(h1Text) &&
                    partTitle.replaceAll(removePattern, "").toLowerCase().contains(h1Text)) {
                titleText = partTitle;
                break;
            }
            if (partTitle.length() > largeTitleLength) {
                largeTitleLength = partTitle.length();
                titleText = partTitle;
            }
        }
        return titleText.trim();
    }
}