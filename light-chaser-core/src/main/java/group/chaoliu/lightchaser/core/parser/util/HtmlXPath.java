/*
 * Copyright (c) 2015, Chao Liu (chaoliu1024@gmail.com). All rights reserved.
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

package group.chaoliu.lightchaser.core.parser.util;

import lombok.extern.slf4j.Slf4j;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * xpath tool to parse html page
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class HtmlXPath {

    private static final String[] XPATH_STRING_RESULT = {"substring-before", "substring-after"};

    /**
     * get node list of one html page
     *
     * @param HtmlPage html page
     * @param xpathExp expression of xpath
     * @return if has nodes, return NodeList, else return null.
     */
    public Object parse(String HtmlPage, String xpathExp, XPath xpath) {
        HtmlCleaner htmlCleaner = new HtmlCleaner();
        TagNode tagNode = htmlCleaner.clean(HtmlPage);
        Document dom;
        try {
            dom = new DomSerializer(new CleanerProperties()).createDOM(tagNode);
            Object result = null;
            for (String xpathKey : XPATH_STRING_RESULT) {
                if (xpathExp.contains(xpathKey)) {
                    result = xpath.evaluate(xpathExp, dom, XPathConstants.STRING);
                    break;
                }
            }
            if (null == result) {
                result = xpath.evaluate(xpathExp, dom, XPathConstants.NODESET);
            }
            return result;
        } catch (ParserConfigurationException e) {
            log.error("parse config exception {}", e);
        } catch (XPathExpressionException e) {
            log.error("xpath expression exception {}", e);
        }
        return null;
    }

    public Object parse(String HtmlPage, String xpathExp) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        return parse(HtmlPage, xpathExp, xpath);
    }

    /**
     * get value of html node, by using xpath expression
     *
     * @param node     html node
     * @param xpathExp expression of xpath
     * @return value of the xpath node
     */
    public Object parse(Node node, String xpathExp) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        return parse(node, xpathExp, xpath);
    }

    public Object parse(Node node, String xpathExp, XPath xpath) {
        Object result;
        try {
            result = xpath.evaluate(xpathExp, node, XPathConstants.NODESET);
            return result;
        } catch (XPathExpressionException e) {
            log.error("xpath expression exception {}", e);
        }
        return "";
    }
}
