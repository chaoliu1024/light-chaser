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

package me.chaoliu.lightchaser.core;

import me.chaoliu.lightchaser.core.util.FileUtil;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * test org.w3c.dom
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class W3CDomTest {

    private String html = "<html><head><title>First parse</title></head>"
            + "<body><p>Parsed HTML into a doc.</p></body></html>";

    // @Before
    public void getHTML() {
        String htmlFile = System.getProperty("user.dir") + File.separator + "test-page/test.html";
        html = FileUtil.readFile(htmlFile);
    }

    @Test
    public void testParse() throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new ByteArrayInputStream(html.getBytes("utf-8"))));
        Node node = doc.getFirstChild();
        System.out.println(node.getNodeName());
        System.out.println(node.getFirstChild().getNodeName());
    }
}
