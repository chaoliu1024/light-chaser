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

package me.chaoliu.lightchaser.wps.newspaper;

import me.chaoliu.lightchaser.wps.newspaper.entity.Content;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OutputFormatter {

    private Element node;

    public OutputFormatter(Element node) {
        this.node = node;
    }

    /**
     * Returns the body text of an article, and also the body article
     * html if specified. Returns in (text, html) form
     */
    public String format() {
        if (null == node) {
            return "";
        }
        removeNegativeScoresNodes();
        unwrapLinks();
        addNewlineToBr();
        addNewlineToLi();
        replaceWithText();
        removeEmptyTags();
        removeTrailingMediaDiv();
        return convert2Text();
    }

    /**
     * Remove elements which attribute of score < 1.
     */
    private void removeNegativeScoresNodes() {
        Elements elements = node.getElementsByAttribute(Content.SCORE_ATTR);
        for (Element element : elements) {
            Double score = Double.parseDouble(element.attr(Content.SCORE_ATTR));
            if (score < 1) {
                element.parent().remove();
            }
        }
    }

    /**
     * Remove elements of <b>a</b> tag.
     */
    private void unwrapLinks() {
        Elements links = node.getElementsByTag("a");
        for (Element link : links) {
            link.unwrap();
        }
    }

    /**
     * Replace <b>br</b> tag with separator line.
     */
    private void addNewlineToBr() {
        Elements brNodes = node.getElementsByTag("br");
        for (Element node : brNodes) {
            node.text(System.lineSeparator());
        }
    }

    /**
     * Replace <b>li</b> tag with separator line.
     */
    private void addNewlineToLi() {
        Elements ulNodes = node.getElementsByTag("ul");
        for (Element ul : ulNodes) {
            Elements liNodes = ul.getElementsByTag("li");
            for (Element li : liNodes) {
                li.appendText(System.lineSeparator());
                for (Element c : li.children()) {
                    c.unwrap();
                }
            }
        }
    }

    /**
     * Unwrap the elements of tag with <b>b</b>, <b>strong</b>, <b>i</b>, <b>br</b>,
     * <b>sup</b>.
     */
    private void replaceWithText() {
        Elements nodes = new Elements();
        nodes.addAll(node.getElementsByTag("b"));
        nodes.addAll(node.getElementsByTag("strong"));
        nodes.addAll(node.getElementsByTag("i"));
        nodes.addAll(node.getElementsByTag("br"));
        nodes.addAll(node.getElementsByTag("sup"));
        for (Element node : nodes) {
            node.unwrap();
        }
    }

    /**
     * Remove empty tags.
     */
    private void removeEmptyTags() {
        Elements allNodes = node.getAllElements();
        for (int i = allNodes.size() - 2; i > 0; i--) {
            Element node = allNodes.get(i);
            String tagName = node.tagName();
            String text = node.ownText();
            if ((!tagName.equals("br") || text.equals("\\r"))
                    && text.length() == 0
                    && node.getElementsByTag("object").size() == 0
                    && node.getElementsByTag("embed").size() == 0) {
                node.unwrap();
            }
        }
    }

    /**
     * Return all text of nodes.
     */
    private String convert2Text() {
        Elements allNodes = node.getAllElements();
        StringBuilder text = new StringBuilder();
        for (Element node : allNodes) {
            text.append(node.ownText()).append(System.lineSeparator());
        }
        return text.toString();
    }

    private void removeTrailingMediaDiv() {
    }
}