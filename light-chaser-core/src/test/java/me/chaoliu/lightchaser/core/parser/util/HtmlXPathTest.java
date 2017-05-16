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

package me.chaoliu.lightchaser.core.parser.util;

import me.chaoliu.lightchaser.core.util.FileUtil;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;

/**
 * test HtmlXPath
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class HtmlXPathTest {

    private String html;

    @Before
    public void getHTML() {
        InputStream in = this.getClass().getResourceAsStream("/test-page/test.html");
        html = FileUtil.streamToString(in);
    }

    @Test
    public void testParse() throws XPathExpressionException {
        String xpathExp = "//h1[@itemprop='name']/text()";
        HtmlXPath xpather = new HtmlXPath();

        XPath xpath = XPathFactory.newInstance().newXPath();

        Object result = xpather.parse(html, xpathExp, xpath);

        if (result instanceof NodeList) {
            NodeList nodeList = (NodeList) result;
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                System.out.println("Node Value: " + node.getNodeValue());
                System.out.println("Text Content: " + node.getTextContent());

                System.out.println("====================sub path value==============");
                Object subPath = xpath.evaluate(".", node, XPathConstants.STRING);
                System.out.println(subPath.toString());
            }
        } else {
            System.out.println(result.toString());
        }
    }
}
