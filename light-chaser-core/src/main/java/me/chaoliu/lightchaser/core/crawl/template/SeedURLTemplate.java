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

package me.chaoliu.lightchaser.core.crawl.template;

import lombok.extern.slf4j.Slf4j;
import me.chaoliu.lightchaser.core.crawl.CrawlerMessage;
import me.chaoliu.lightchaser.core.daemon.Job;
import me.chaoliu.lightchaser.core.daemon.Star;
import me.chaoliu.lightchaser.core.protocol.http.CommonHeaders;
import me.chaoliu.lightchaser.core.queue.memory.UnVisitedMessagePool;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * parse seed_url.xml
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class SeedURLTemplate {

    private UnVisitedMessagePool unVisitedMessagePool;
    private File seedFile;

    public SeedURLTemplate(File file) {
        this.unVisitedMessagePool = Star.unVisitedMessagePool;
        this.seedFile = file;
    }

    public UnVisitedMessagePool initSeedURLs(Job job) {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(this.seedFile);
            Element root = document.getRootElement();
            listNodes(root, job);
        } catch (DocumentException e) {
            log.error("parse seed url config error. error info: {}", e);
        }
        return this.unVisitedMessagePool;
    }

    /**
     * Traverse all the children nodes of current node
     *
     * @param node current node
     */
    @SuppressWarnings("unchecked")
    public void listNodes(Element node, Job job) {
        if ("entity".equals(node.getName())) {
            List<Node> entities = node.elements();
            CrawlerMessage crawlerMsg = new CrawlerMessage();
            crawlerMsg.setJobName(job.getJobName());
            crawlerMsg.setJobType(job.getJobType());
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
                    && StringUtils.isNotBlank(job.getJobName())) {
                CrawlTemplate crawlConfig = Star.templateCache.getTemplates().get(job.getJobName()).getCrawlTemplate();
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
                unVisitedMessagePool.addCrawlerMessage(crawlerMsg);
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
