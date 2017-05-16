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

package me.chaoliu.lightchaser.wps.util;

import org.jsoup.select.NodeVisitor;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class W3CBuilder implements NodeVisitor {

    private final Document doc;
    private Element dest;

    public W3CBuilder(Document doc) {
        this.doc = doc;
    }

    public void head(final org.jsoup.nodes.Node source, int depth) {
        if (source instanceof org.jsoup.nodes.Element) {
            org.jsoup.nodes.Element sourceEl = (org.jsoup.nodes.Element) source;
            Element el = doc.createElement(sourceEl.tagName());
            JsoupParserUtil.copyAttributes(sourceEl, el);
            if (dest == null) {
                doc.appendChild(el);
            } else {
                dest.appendChild(el);
            }
            dest = el;
        } else if (source instanceof org.jsoup.nodes.TextNode) {
            org.jsoup.nodes.TextNode sourceText = (org.jsoup.nodes.TextNode) source;
            Text text = doc.createTextNode(sourceText.getWholeText());
            dest.appendChild(text);
        } else if (source instanceof org.jsoup.nodes.Comment) {
            org.jsoup.nodes.Comment sourceComment = (org.jsoup.nodes.Comment) source;
            Comment comment = doc.createComment(sourceComment.getData());
            dest.appendChild(comment);
        } else if (source instanceof org.jsoup.nodes.DataNode) {
            org.jsoup.nodes.DataNode sourceData = (org.jsoup.nodes.DataNode) source;
            Text node = doc.createTextNode(sourceData.getWholeData());
            dest.appendChild(node);
        } else {

        }
    }

    public void tail(final org.jsoup.nodes.Node source, int depth) {
        if (source instanceof org.jsoup.nodes.Element && dest.getParentNode() instanceof Element) {
            dest = (Element) dest.getParentNode();
        }
    }
}
