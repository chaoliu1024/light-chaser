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

package me.chaoliu.lightchaser.core.crawl;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.chaoliu.lightchaser.core.crawl.template.CrawlTemplate;
import me.chaoliu.lightchaser.core.crawl.template.Template;
import me.chaoliu.lightchaser.core.crawl.template.WrapperTemplate;
import me.chaoliu.lightchaser.core.daemon.Star;
import me.chaoliu.lightchaser.core.fission.BaseFission;
import me.chaoliu.lightchaser.core.parser.ParseHandler;
import me.chaoliu.lightchaser.core.parser.template.ParseResult;
import me.chaoliu.lightchaser.core.parser.template.TemplateParseHandler;
import me.chaoliu.lightchaser.core.persistence.hbase.HBaseClient;
import me.chaoliu.lightchaser.core.protocol.http.HttpClient;
import me.chaoliu.lightchaser.core.protocol.http.ResponseMessage;
import me.chaoliu.lightchaser.core.util.MessageDigestUtil;
import me.chaoliu.lightchaser.core.util.SpringBeanUtil;
import me.chaoliu.lightchaser.core.wrapper.WrapHandler;
import me.chaoliu.lightchaser.core.wrapper.template.TemplateWrapHandler;
import me.chaoliu.lightchaser.core.wrapper.template.Wrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * crawler based on template
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
@Component
@Scope("prototype")
public class Crawler {

    private static final String WRAPPER_MERGE_KEY = "merge";
    private static final String WRAPPER_FOCUS_ID_KEY = "focus_id";

    private WrapHandler wrapper = new TemplateWrapHandler();
    private ParseHandler parser = new TemplateParseHandler();

    private HttpClient httpClient;

    private BaseFission fission;

    private Map<String, HBaseClient> hBaseClientPool = new HashMap<>();

    // 内部队列
    public static Queue<CrawlerMessage> innerQueue = new ConcurrentLinkedQueue<>();

    public Crawler() {

        HttpClientBuilder builder = HttpClients.custom();
        // BasicHttpClient.setProxy(builder, "127.0.0.1", 8580);
        CloseableHttpClient client = builder.build();
        this.httpClient = new HttpClient(client);
    }

    public void initTemplate(String siteName) {

        if (null == Star.templateCache.getTemplates().get(siteName)) {

            CrawlTemplate crawlTemplate = new CrawlTemplate(siteName);
            WrapperTemplate wrapperTemplate = new WrapperTemplate(siteName);

            Template template = new Template(siteName, crawlTemplate, wrapperTemplate);
            Star.templateCache.putSiteTemplate(siteName, template);
        }
    }

    /**
     * check the page is json or html
     *
     * @param crawlerMsg crawler message
     * @param page       web page
     */
    private void pageType(CrawlerMessage crawlerMsg, String page) {
        try {
            log.debug("this page is JSON");
            Object json = JSONObject.parse(page);
            crawlerMsg.setJson(json);
        } catch (JSONException e) {
            log.debug("this page is HTML");
            HtmlDom htmlDom = new HtmlDom(page);
            crawlerMsg.setHtmlDom(htmlDom);
        }
    }

    /**
     * 核聚变
     * 多个字段合并成一个完整字段
     */
    private Map nucleusFusion(CrawlerMessage crawlerMgs) {

        CrawlerMessage _msg = crawl(crawlerMgs);

        if (null != _msg) {

            Map wrapperInfo = wrapper.wrap(crawlerMgs);

            ParseResult parseResult = parser.parse(crawlerMgs);

            wrapperInfo.put(WRAPPER_MERGE_KEY, parseResult.getMergeInfo());

            String mergeInfo = parseResult.getMergeInfo();

            if (StringUtils.isNotBlank(mergeInfo)) {
                List<CrawlerMessage> msgs = parseResult.getMessages();
                if (CollectionUtils.isNotEmpty(msgs)) {
                    List<CrawlerMessage> _msgs = filterNotCrawledMsgs(msgs);
                    if (CollectionUtils.isNotEmpty(_msgs)) {
                        innerQueue.addAll(msgs);
                    }
                }
            }
            return wrapperInfo;
        } else {
            return null;
        }
    }

    private HBaseClient getHBaseClient(String jobName) {
        HBaseClient hBaseClient = hBaseClientPool.get(jobName);
        if (null == hBaseClient) {
            try {
                hBaseClient = new HBaseClient(jobName);
                hBaseClientPool.put(jobName, hBaseClient);
            } catch (IOException e) {
                log.error("create HBase client instance error");
            }
        }
        return hBaseClient;
    }

    public void storeWebPage(CrawlerMessage crawlerMgs) {
        String jobName = crawlerMgs.getJobName();
        HBaseClient hBaseClient = getHBaseClient(jobName);
        if (null != hBaseClient) {
            String url = crawlerMgs.getRequestMsg().getURL();
            String page = crawlerMgs.getResponseMsg().getBody();
            String rowKey = MessageDigestUtil.MD5(url);
            hBaseClient.put(rowKey, "p", "c", page);
        }
    }

    public void run(CrawlerMessage crawlerMsg) {

        CrawlerMessage _msg = crawl(crawlerMsg);

        if (null != _msg) {

            // whole information
            Map wholeInfo = new HashMap<>();

            Map wrapperInfo = wrapper.wrap(_msg);
            ParseResult parseResult = parser.parse(_msg);

            List<CrawlerMessage> msgs = parseResult.getMessages();
            String mergeInfo = parseResult.getMergeInfo();

            // inner crawl task
            if (StringUtils.isNotBlank(mergeInfo)) {
                wholeInfo = innerCrawl(wrapperInfo, mergeInfo, _msg.getURLLevel(), msgs);
            } else {
                if (null != wrapperInfo && null != wrapperInfo.get(Wrapper.INFO_KEY)) {
                    wholeInfo.putAll(wrapperInfo);
                }
                if (CollectionUtils.isNotEmpty(msgs)) {
                    List<CrawlerMessage> _msgs = filterNotCrawledMsgs(msgs);
                    if (CollectionUtils.isNotEmpty(_msgs)) {
                        Star.unVisitedMessagePool.addCrawlerMessage(_msgs);
                    }
                }
            }

            if (!wholeInfo.isEmpty()) {
                String beanName = wholeInfo.get(Wrapper.JOB_TYPE_KEY) + BaseFission.FISSION_BEAN_SUFFIX;
                fission = SpringBeanUtil.fissionBean(beanName);
                fission.fission(wholeInfo);
            } else if (wholeInfo.isEmpty() && null == msgs) {
                log.info("{} fail", crawlerMsg.getRequestMsg().getURL());
            }
        }
    }

    /**
     * Inner crawl, that return the whole information of a crawl task which need to crawl multi URLs.
     *
     * @param wrapperInfo wrapperInfo
     * @param mergeInfo   mergeInfo
     * @param URLLevel    level of url
     * @param msgs        crawl message
     * @return whole information of a crawl task
     */
    private Map innerCrawl(Map wrapperInfo, String mergeInfo, int URLLevel, List<CrawlerMessage> msgs) {

        Map wholeInfo = new ConcurrentHashMap<>();

        if (null != wrapperInfo && null != wrapperInfo.get(Wrapper.INFO_KEY)) {
            if ("focus".equals(mergeInfo)) {
                wrapperInfo.put(WRAPPER_MERGE_KEY, "focus");
                wrapperInfo.put(WRAPPER_FOCUS_ID_KEY, String.valueOf(URLLevel));
                wholeInfo.putAll(wrapperInfo);
            }
        }
        // TODO 利用线程池抓取
        if (CollectionUtils.isNotEmpty(msgs)) {
            List<CrawlerMessage> _msgs = filterNotCrawledMsgs(msgs);
            if (CollectionUtils.isNotEmpty(_msgs)) {
                innerQueue.addAll(msgs);
            }
            while (!innerQueue.isEmpty()) {
                CrawlerMessage msg = innerQueue.poll();
                Map partInfo = nucleusFusion(msg);
                wrapMerge(wholeInfo, partInfo);
            }
        }
        return wholeInfo;
    }

    /**
     * The message which not been crawled by utilizing Bloom Filter
     *
     * @param msgs crawl message
     * @return crawl message which not been crawled
     */
    private List<CrawlerMessage> filterNotCrawledMsgs(List<CrawlerMessage> msgs) {
        List<CrawlerMessage> _msgs = new ArrayList<>();
        for (CrawlerMessage msg : msgs) {
            String url = msg.getRequestMsg().getURL();
            if (!Star.bloomFilter.contains(url)) {
                _msgs.add(msg);
            }
        }
        return _msgs;
    }

    public void waitFinish() {
        if (null != fission) {
            fission.waitFissionFinish();
        }
    }

    /**
     * merge the part information to whole map
     *
     * @param wholeInfo the whole wrapper information
     * @param partInfo  part information
     * @return the whole information after merge
     */
    private Map wrapMerge(Map wholeInfo, Map partInfo) {
        if (wholeInfo.containsKey(WRAPPER_MERGE_KEY) && partInfo.containsKey(WRAPPER_MERGE_KEY)) {
            if (wholeInfo.get(WRAPPER_FOCUS_ID_KEY).equals(partInfo.get(WRAPPER_MERGE_KEY))) {
                Map part = (Map) partInfo.get("info");
                Map whole = (Map) wholeInfo.get("info");
                // TODO 这里最好用java泛型
                for (Object key : part.keySet()) {
                    if (whole.containsKey(key)) {
                        // TODO 根据格式追加数据
//                        whole.get(key).append(part.get(key));
                    } else {
                        whole.put(key, part.get(key));
                    }
                }
            }
        }
        return wholeInfo;
    }

    public CrawlerMessage crawl(CrawlerMessage crawlerMgs) {

        String jobName = crawlerMgs.getJobName();
        initTemplate(jobName);

        String url = crawlerMgs.getRequestMsg().getURL();

        if (!Star.bloomFilter.contains(url)) {
            log.info("Crawl url {}", url);
            ResponseMessage rspMsg = this.httpClient.GET(crawlerMgs.getRequestMsg());

            Star.bloomFilter.add(url);

            if (StringUtils.isNotBlank(rspMsg.getBody())) {

                pageType(crawlerMgs, rspMsg.getBody());

                crawlerMgs.setCrawlTime(new Date());
                crawlerMgs.setResponseMsg(rspMsg);

                // 存储网页
                storeWebPage(crawlerMgs);

                try {
                    Thread.sleep(Star.speedController.getSpeedCache().get(jobName).randomSpeed());
                } catch (InterruptedException e) {
                    log.error("Sleep error: {}.", e);
                }
                return crawlerMgs;
            } else {
                log.error("response body is null!");
                return null;
            }
        } else {
            log.info("The url {} has been crawled", url);
            return null;
        }
    }
}