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

import group.chaoliu.lightchaser.core.crawl.CrawlerMessage;
import group.chaoliu.lightchaser.core.daemon.Job;
import group.chaoliu.lightchaser.core.protocol.http.CommonHeaders;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;

/**
 * parse seed_url.xml
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class SeedMsgTemplate extends Template {

    private List<CrawlerMessage> messages;

    public SeedMsgTemplate() {
        messages = new ArrayList<>();
    }

    public List<CrawlerMessage> initSeedMsgs(Job job) {

        String seedURLPath = templateRootPath + job.getType() + File.separator +
                job.getName() + File.separator + "seed_url.xml";

        File seedFile = new File(seedURLPath);

        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(seedFile);
            Element root = document.getRootElement();
            listNodes(root, job);
        } catch (DocumentException e) {
            log.error("parse seed url config error. error info: {}", e);
        }
        return this.messages;
    }

    /**
     * Traverse all the children nodes of current node
     *
     * @param node current node
     */
    @SuppressWarnings("unchecked")
    private void listNodes(Element node, Job job) {
        if ("entity".equals(node.getName())) {
            List<Node> entities = node.elements();
            CrawlerMessage crawlerMsg = new CrawlerMessage();
            crawlerMsg.setJob(job);
            for (Node entityNode : entities) {
                if ("url".equals(entityNode.getName())) {
                    crawlerMsg.getRequestMsg().setURL(entityNode.getText().trim());
                }
                if ("level".equals(entityNode.getName())) {
                    crawlerMsg.setURLLevel(Integer.parseInt(entityNode.getText().trim()));
                }
                if ("cookies".equals(entityNode.getName())) {

                    Map<String, String> _cookies = new HashMap<>();

                    String[] cookies = entityNode.getText().trim().split(";");
                    for (String cookie : cookies) {
                        String cookieName = cookie.split("=")[0];
                        String cookieValue = cookie.split("=")[1];
                        _cookies.put(cookieName, cookieValue);
                    }
                    crawlerMsg.getRequestMsg().setCookie(_cookies);
                }
            }
            if (StringUtils.isNotBlank(crawlerMsg.getRequestMsg().getURL())
                    && StringUtils.isNotBlank(String.valueOf(crawlerMsg.getURLLevel()))
                    && StringUtils.isNotBlank(job.getName())) {
                CrawlTemplate crawlConfig = new CrawlTemplate(job);
                // set request headers
                Map<String, String> headers = crawlConfig.getHeaders();
                if (!headers.isEmpty()) {
                    log.info("Using crawl_path.xml headers.");
                    crawlerMsg.getRequestMsg().setHeaders(headers);
                } else {
                    log.info("Tag headers in crawl_path.xml file is null. Using common default headers.");
                    CommonHeaders defaultHeaders = new CommonHeaders();
                    crawlerMsg.getRequestMsg().setHeaders(defaultHeaders.getHeaders());
                }
                messages.add(crawlerMsg);
            }
        }
        // 递归当前节点所有子节点
        Iterator<Element> iterator = node.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            listNodes(e, job);
        }
    }
}