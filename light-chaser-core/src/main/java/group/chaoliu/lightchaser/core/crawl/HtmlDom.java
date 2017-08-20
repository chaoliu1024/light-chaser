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

import lombok.extern.slf4j.Slf4j;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

/**
 * html dom, contains html document and xpath object
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class HtmlDom {

    private Document htmlDoc;
    private XPath xpath;

    public HtmlDom(String html) {
        try {
            setHtmlDoc(html);
        } catch (ParserConfigurationException e) {
            log.error("new html document error {}", e);
        }
        setXpath();
    }

    public void setHtmlDoc(String html) throws ParserConfigurationException {
        HtmlCleaner htmlCleaner = new HtmlCleaner();
        TagNode tagNode = htmlCleaner.clean(html);
        this.htmlDoc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);
    }

    public void setHtmlDoc(Document doc) {
        this.htmlDoc = doc;
    }

    public Document getHtmlDoc() {
        return this.htmlDoc;
    }

    private void setXpath() {
        this.xpath = XPathFactory.newInstance().newXPath();
    }

    public XPath getXpath() {
        return this.xpath;
    }

}
