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

package group.chaoliu.lightchaser.core.crawl.template;

import group.chaoliu.lightchaser.core.daemon.Job;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * parse the template of extractor web page
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class WrapperTemplate extends Template {

    @Getter
    private List<Node> wrapperPatternNodes = new ArrayList<>();

    public WrapperTemplate(Job job) {
        String wrapperPath = templateRootPath + job.getType() + File.separator +
                job.getName() + File.separator + "wrapper.xml";
        readWrapperConfig(wrapperPath);
    }

    public void readWrapperConfig(String filePath) {
        File file = new File(filePath);
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(file);
            Element root = document.getRootElement();
            listNodes(root);
        } catch (DocumentException e) {
            log.error("Read wrapper config exception: {}", e);
        }
    }

    /**
     * Traverse all the children nodes of current node
     *
     * @param node current node
     */
    @SuppressWarnings("unchecked")
    public void listNodes(Node node) {
        wrapperPatternNodes.addAll(node.selectNodes("/wrappers/wrapper"));
    }
}
