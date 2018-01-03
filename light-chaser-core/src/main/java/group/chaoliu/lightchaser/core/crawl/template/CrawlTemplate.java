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

import group.chaoliu.lightchaser.common.Category;
import group.chaoliu.lightchaser.common.Category;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * parse crawl rules from crawl site template
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class CrawlTemplate extends Template {

    public static final String CRAWL_TEMPLATE_HEADERS = "headers";

    public static final String CRAWL_TEMPLATE_URL_LEVELS_SET = "url_levels";

    public static final String CRAWL_TEMPLATE_PARAMETERS = "parameters";

    @Getter
    private Map<String, String> headers = new HashMap<>();

    @Getter
    private Element urlLevelsSet = null;

    @Getter
    private Element crawlParameters = null;

    @Getter
    private int httpSocketTimeout = 5000;

    @Getter
    private int httpConnectTimeout = 10000;

    /**
     * min interval time between two crawl action
     */
    @Getter
    @Setter
    private int minInterval = 0;

    /**
     * max interval time between two crawl action
     */
    @Getter
    @Setter
    private int maxInterval = 0;

    public CrawlTemplate(Category category) {
        String configPath = templateRootPath + category.getType() + File.separator +
                category.getName() + File.separator + "crawl_path.xml";
        readCrawlerConfig(configPath);
    }

    public void readCrawlerConfig(String path) {
        File file = new File(path);
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(file);
            Element root = document.getRootElement();
            listNodes(root);
        } catch (DocumentException e) {
            log.error("Read crawler config error. error info:{}", e);
        }
    }

    /**
     * Traverse all the children nodes of current node.
     *
     * @param node current node
     */
    @SuppressWarnings("unchecked")
    public void listNodes(Element node) {

        if (CRAWL_TEMPLATE_HEADERS.equals(node.getName())) {
            getHeaders(node);
        } else if (CRAWL_TEMPLATE_URL_LEVELS_SET.equals(node.getName())) {
            urlLevelsSet = node;
        } else if (CRAWL_TEMPLATE_PARAMETERS.equals(node.getName())) {
            crawlParameters = node;
            intervalTime();
            httpClientTimeout();
        }

        // 递归当前节点所有子节点
        Iterator<Element> iterator = node.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            listNodes(e);
        }
    }

    /**
     * set the http client timeout
     */
    private void httpClientTimeout() {
        String socketTimeout = crawlParameters.elementTextTrim("http_socket_timeout");
        String connectTimeout = crawlParameters.elementTextTrim("http_connect_timeout");
        if (StringUtils.isNotBlank(socketTimeout)) {
            try {
                httpSocketTimeout = Integer.parseInt(socketTimeout);
            } catch (NumberFormatException e) {
            }
        }
        if (StringUtils.isNotBlank(connectTimeout)) {
            try {
                httpConnectTimeout = Integer.parseInt(connectTimeout);
            } catch (NumberFormatException e) {
            }
        }
    }

    /**
     * set the min and max interval time between two crawl action
     */
    private void intervalTime() {
        String intervalMin = crawlParameters.elementTextTrim("interval_min");
        String intervalMax = crawlParameters.elementTextTrim("interval_max");
        if (StringUtils.isNotBlank(intervalMin)) {
            try {
                minInterval = Integer.parseInt(intervalMin);
            } catch (NumberFormatException e) {
                minInterval = 0;
            }
        }
        if (StringUtils.isNotBlank(intervalMax)) {
            try {
                maxInterval = Integer.parseInt(intervalMax);
            } catch (NumberFormatException e) {
                maxInterval = 0;
            }
        }
    }

    /**
     * get request header from crawler configuration file
     *
     * @param node header node
     */
    @SuppressWarnings("unchecked")
    public void getHeaders(Element node) {
        Iterator<Element> iterator = node.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            headers.put(e.getName(), e.getTextTrim());
        }
    }
}
