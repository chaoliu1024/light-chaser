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

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import group.chaoliu.lightchaser.core.config.Constants;
import group.chaoliu.lightchaser.core.crawl.template.CrawlTemplate;
import group.chaoliu.lightchaser.core.crawl.template.JobTemplate;
import group.chaoliu.lightchaser.core.crawl.template.WrapperTemplate;
import group.chaoliu.lightchaser.core.daemon.FlectionSpaceTime;
import group.chaoliu.lightchaser.core.daemon.Job;
import group.chaoliu.lightchaser.core.daemon.planet.Planet;
import group.chaoliu.lightchaser.core.fission.BaseFission;
import group.chaoliu.lightchaser.core.parser.ParseHandler;
import group.chaoliu.lightchaser.core.parser.template.ParseResult;
import group.chaoliu.lightchaser.core.parser.template.TemplateParseHandler;
import group.chaoliu.lightchaser.core.persistence.ImageStore;
import group.chaoliu.lightchaser.core.persistence.WebPageStore;
import group.chaoliu.lightchaser.core.protocol.http.BasicHttpClient;
import group.chaoliu.lightchaser.core.protocol.http.HttpClient;
import group.chaoliu.lightchaser.core.protocol.http.RequestMessage;
import group.chaoliu.lightchaser.core.protocol.http.ResponseMessage;
import group.chaoliu.lightchaser.core.util.Dom4jUtil;
import group.chaoliu.lightchaser.core.util.SpringBeanUtil;
import group.chaoliu.lightchaser.core.wrapper.WrapHandler;
import group.chaoliu.lightchaser.core.wrapper.template.TemplateWrapHandler;
import group.chaoliu.lightchaser.core.wrapper.template.Wrapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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

    private WrapHandler wrapper = new TemplateWrapHandler();
    private ParseHandler parser = new TemplateParseHandler();

    private HttpClient httpClient;

    private BaseFission fission;

    @Setter
    @Getter
    private FlectionSpaceTime flectionST;

    // light-chaser.yaml
    @Getter
    private Map config;

    // 是否使用代理
    private boolean useProxy = false;

    private boolean isStorePage = false;

    private boolean isStoreMySQL = true;

    @Autowired
    private WebPageStore webPageStore;

    // 内部队列
    public static Queue<CrawlerMessage> innerQueue = new ConcurrentLinkedQueue<>();

    public void setConfig(Map config) {
        this.config = config;
        if (null != config) {
            if ((boolean) config.get(Constants.PROXY_USED)) {
                useProxy = true;
            }
            if ((boolean) config.get(Constants.STORE_WEB_PAGE)) {
                isStorePage = true;
            }
            if (!(boolean) config.get(Constants.STORE_MYSQL)) {
                isStoreMySQL = false;
            }
        }
    }

    public Crawler() {
        HttpClientBuilder builder = HttpClients.custom();
        CloseableHttpClient client = builder.build();
        this.httpClient = new HttpClient(client);
    }

    public void initTemplate(Job job) {

        if (null == Planet.templateCache.getTemplates().get(job)) {

            CrawlTemplate crawlTemplate = new CrawlTemplate(job);
            WrapperTemplate wrapperTemplate = new WrapperTemplate(job);

            JobTemplate template = new JobTemplate(job, crawlTemplate, wrapperTemplate);
            Planet.templateCache.putSiteTemplate(job, template);
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

            wrapperInfo.put(TemplateParseHandler.MERGE_KEY, parseResult.getMergeInfo());

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

    public void run(CrawlerMessage crawlerMsg) {
        try {
            CrawlerMessage _msg = crawl(crawlerMsg);
            if (null != _msg) {
                // whole information
                Map wholeInfo = new HashMap<>();

                Map wrapperInfo = wrapper.wrap(_msg);
                ParseResult parseResult = parser.parse(_msg);

                List<CrawlerMessage> msgs = parseResult.getMessages();
                String mergeInfo = parseResult.getMergeInfo();

                // inner crawl task
                if (StringUtils.isNotBlank(mergeInfo) && TemplateParseHandler.MERGE_XML_VALUE.equals(mergeInfo)) {
                    wholeInfo = innerCrawl(wrapperInfo, mergeInfo, _msg.getURLLevel(), msgs);
                } else {
                    if (null != wrapperInfo && null != wrapperInfo.get(Wrapper.DATA_KEY)) {
                        wholeInfo.putAll(wrapperInfo);
                    }
                    if (CollectionUtils.isNotEmpty(msgs)) {
                        List<CrawlerMessage> _msgs = filterNotCrawledMsgs(msgs);
                        if (CollectionUtils.isNotEmpty(_msgs)) {
                            flectionST.getMessagePool().addMessage(_msgs);
                        }
                    }
                }


                if (!wholeInfo.isEmpty()) {
                    System.out.println(wholeInfo);
                    if (isStoreMySQL) {
                        String beanName = wholeInfo.get(Wrapper.JOB_TYPE_KEY) + BaseFission.FISSION_BEAN_SUFFIX;
                        fission = SpringBeanUtil.fissionBean(beanName);
                        fission.fission(wholeInfo);
                    }
                } else if (wholeInfo.isEmpty() && null == msgs) {
                    // TODO 存储失败内容
                    log.error("{} fail", crawlerMsg.getRequestMsg().getURL());
                }
            }
        } catch (Exception e) {
            log.error("抓取URL: {} 错误, 抓取内容 {}\n, 错误代码: {}",
                    crawlerMsg.getRequestMsg().getURL(), crawlerMsg.getResponseMsg().getBody(), e);
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

        if (null != wrapperInfo && null != wrapperInfo.get(Wrapper.DATA_KEY)) {
            if (TemplateParseHandler.MERGE_XML_VALUE.equals(mergeInfo)) {
                wrapperInfo.put(TemplateParseHandler.MERGE_KEY, TemplateParseHandler.MERGE_XML_VALUE);
                wrapperInfo.put(TemplateParseHandler.FOCUS_ID_KEY, String.valueOf(URLLevel));
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
            if (!flectionST.getBloomFilter().contains(url)) {
                _msgs.add(msg);
            }
        }
        return _msgs;
    }

    /**
     * merge the part information to whole map
     *
     * @param wholeInfo the whole wrapper information
     * @param partInfo  part information
     * @return the whole information after merge
     */
    private Map wrapMerge(Map wholeInfo, Map partInfo) {
        // 都有merge标签
        if (wholeInfo.containsKey(TemplateParseHandler.MERGE_KEY) && partInfo.containsKey(TemplateParseHandler.MERGE_KEY)) {
            if (wholeInfo.get(TemplateParseHandler.FOCUS_ID_KEY).equals(partInfo.get(TemplateParseHandler.MERGE_KEY))) {
                Map part = (Map) partInfo.get(Wrapper.DATA_KEY);
                Map whole = (Map) wholeInfo.get(Wrapper.DATA_KEY);
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

    /**
     * check if the request message need to using POST method
     *
     * @param crawlerMgs crawl message
     */
    private void setPostMethod(CrawlerMessage crawlerMgs) {
        Element levelElement = TemplateParseHandler.crawlLevelElement(crawlerMgs);
        if (null != levelElement) {
            if (Dom4jUtil.isAttributeNotBlank(levelElement, TemplateParseHandler.HTTP_METHOD)) {
                // post method
                if ("post".equals(levelElement.attributeValue(TemplateParseHandler.HTTP_METHOD).trim())) {
                    crawlerMgs.getRequestMsg().setPostRequest(true);
                }
            }
        }
    }

    public CrawlerMessage crawl(CrawlerMessage crawlerMgs) {

        Job job = crawlerMgs.getJob();

        initTemplate(job);
        setPostMethod(crawlerMgs);

        RequestMessage requestMsg = crawlerMgs.getRequestMsg();

        String url = requestMsg.getURL();
        int i = 10;
        while (i > 0) {
            i--;
            if (!flectionST.getBloomFilter().contains(url)) {
                log.info("Crawl url {}", url);
                ResponseMessage rspMsg;
                if (requestMsg.isPostRequest()) {
                    if (useProxy && null != flectionST.getRadiator()) {
                        HttpClient client = BasicHttpClient.buildProxyHttpClient(flectionST.getRadiator().randomProxy());
                        rspMsg = client.POST(requestMsg);
                    } else {
                        rspMsg = this.httpClient.POST(requestMsg);
                    }
                } else {
                    if (useProxy && null != flectionST.getRadiator()) {
                        HttpClient client = BasicHttpClient.buildProxyHttpClient(flectionST.getRadiator().randomProxy());
                        rspMsg = client.GET(requestMsg);
                    } else {
                        rspMsg = this.httpClient.GET(requestMsg);
                    }
                }

                flectionST.getBloomFilter().add(url);

                if (StringUtils.isNotBlank(rspMsg.getBody())) {

                    pageType(crawlerMgs, rspMsg.getBody());

                    crawlerMgs.setCrawlTime(new Date());
                    crawlerMgs.setResponseMsg(rspMsg);

                    if (isStorePage) {
                        webPageStore.save(crawlerMgs);
                    }

                    try {
                        Thread.sleep(flectionST.getSpeedController().getSpeedCache().get(job).randomSpeed());
                    } catch (InterruptedException e) {
                        log.error("Sleep error: {}.", e);
                    }
                    return crawlerMgs;
                } else if (ImageStore.isImageURL(url)) {
                    return null;
                } else {
                    log.error("response body is null!");
                }
            } else {
                log.info("The url {} has been crawled", url);
            }
        }
        return null;
    }
}