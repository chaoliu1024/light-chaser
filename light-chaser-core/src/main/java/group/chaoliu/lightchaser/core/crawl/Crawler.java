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
import group.chaoliu.lightchaser.common.Category;
import group.chaoliu.lightchaser.common.config.Constants;
import group.chaoliu.lightchaser.common.protocol.http.Proxy;
import group.chaoliu.lightchaser.common.protocol.http.RequestMessage;
import group.chaoliu.lightchaser.common.protocol.http.ResponseMessage;
import group.chaoliu.lightchaser.common.queue.message.QueueMessage;
import group.chaoliu.lightchaser.core.crawl.template.CategoryTemplate;
import group.chaoliu.lightchaser.core.crawl.template.CrawlTemplate;
import group.chaoliu.lightchaser.core.crawl.template.WrapperTemplate;
import group.chaoliu.lightchaser.core.daemon.FlectionSpaceTime;
import group.chaoliu.lightchaser.core.daemon.planet.Planet;
import group.chaoliu.lightchaser.core.fission.BaseFission;
import group.chaoliu.lightchaser.core.parser.ParseHandler;
import group.chaoliu.lightchaser.core.parser.template.ParseResult;
import group.chaoliu.lightchaser.core.parser.template.TemplateParseHandler;
import group.chaoliu.lightchaser.core.persistence.ImageStore;
import group.chaoliu.lightchaser.core.persistence.WebPageStore;
import group.chaoliu.lightchaser.core.protocol.http.BasicHttpClient;
import group.chaoliu.lightchaser.core.protocol.http.HttpClient;
import group.chaoliu.lightchaser.core.util.Dom4jUtil;
import group.chaoliu.lightchaser.core.util.SpringBeanUtil;
import group.chaoliu.lightchaser.core.wrapper.WrapHandler;
import group.chaoliu.lightchaser.core.wrapper.WrapResult;
import group.chaoliu.lightchaser.core.wrapper.template.TemplateWrapHandler;
import group.chaoliu.lightchaser.core.wrapper.template.Wrapper;
import group.chaoliu.lightchaser.rpc.netty.proxy.ProxyCode;
import group.chaoliu.lightchaser.rpc.netty.proxy.ProxySocket;
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

    /**
     * light-chaser.yaml
     */
    @Getter
    private Map config;

    @Setter
    private ProxySocket proxyClient;

    /**
     * 是否使用代理
     */
    private boolean useProxy = false;

    private boolean isStorePage = false;

    private boolean isStoreMySQL = true;

    @Autowired
    private WebPageStore webPageStore;

    /**
     * 内部队列
     */
    public static Queue<QueueMessage> innerQueue = new ConcurrentLinkedQueue<>();

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

    public void initTemplate(Category category) {

        if (null == Planet.templateCache.getTemplates().get(category)) {
            CrawlTemplate crawlTemplate = new CrawlTemplate(category);
            WrapperTemplate wrapperTemplate = new WrapperTemplate(category);
            CategoryTemplate template = new CategoryTemplate(category, crawlTemplate, wrapperTemplate);
            Planet.templateCache.putCategoryTemplate(category, template);
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

        CrawlerMessage cMsg = crawl(crawlerMgs);

        if (null != cMsg) {

            WrapResult wrapResult = wrapper.wrap(crawlerMgs);
            ParseResult parseResult = parser.parse(crawlerMgs);

            Map wrapperInfo = wrapResult.getResult();

            wrapperInfo.put(TemplateParseHandler.MERGE_KEY, parseResult.getMergeInfo());
            String mergeInfo = parseResult.getMergeInfo();

            if (StringUtils.isNotBlank(mergeInfo)) {
                List<CrawlerMessage> msgs = parseResult.getMessages();
                if (CollectionUtils.isNotEmpty(msgs)) {
                    List<QueueMessage> cMsgs = filterNotCrawledMsgs(msgs);
                    if (CollectionUtils.isNotEmpty(cMsgs)) {
                        innerQueue.addAll(cMsgs);
                    }
                }
            }
            return wrapperInfo;
        } else {
            return null;
        }
    }

    /**
     * 保留抽取失败的信息
     */
    public void storeErrorWrap(String url, List<String> errorFields) {

    }

    public void run(CrawlerMessage crawlerMsg) {
        try {
            CrawlerMessage cMsg = crawl(crawlerMsg);
            if (null != cMsg) {
                QueueMessage queueMsg = cMsg.getQueueMessage();

                // whole information
                Map wholeInfo = new HashMap<>();

                WrapResult wrapResult = wrapper.wrap(cMsg);
                ParseResult parseResult = parser.parse(cMsg);

                Map wrapperInfo = null;

                if (null != wrapResult) {
                    if (!wrapResult.isSuccess()) {
                        wrapperInfo = null;

                        storeErrorWrap(queueMsg.getRequestMsg().getURL(), wrapResult.getErrorField());

                    } else {
                        wrapperInfo = wrapResult.getResult();
                    }
                }

                List<CrawlerMessage> msgs = parseResult.getMessages();
                String mergeInfo = parseResult.getMergeInfo();

                // inner crawl task
                if (StringUtils.isNotBlank(mergeInfo) && TemplateParseHandler.MERGE_XML_VALUE.equals(mergeInfo)) {
                    wholeInfo = innerCrawl(wrapperInfo, mergeInfo, queueMsg.getUrlLevel(), msgs);
                } else {
                    if (null != wrapperInfo && null != wrapperInfo.get(Wrapper.DATA_KEY)) {
                        wholeInfo.putAll(wrapperInfo);
                    }
                    if (CollectionUtils.isNotEmpty(msgs)) {
                        List<QueueMessage> cMsgs = filterNotCrawledMsgs(msgs);
                        if (CollectionUtils.isNotEmpty(cMsgs)) {
                            flectionST.getMessagePool().addMessage(cMsgs);
                        }
                    }
                }

                if (!wholeInfo.isEmpty()) {
                    wholeInfo.put(Wrapper.CATEGORY_SUFFIX, flectionST.getCategory().getSuffix());
                    System.out.println(wholeInfo);
                    if (isStoreMySQL) {
                        String beanName = wholeInfo.get(Wrapper.CATEGORY_TYPE_KEY) + BaseFission.FISSION_BEAN_SUFFIX;
                        fission = SpringBeanUtil.fissionBean(beanName);
                        if (null != fission) {
                            fission.fission(wholeInfo);
                        }
                    }
                } else if (wholeInfo.isEmpty() && null == msgs) {
                    // TODO 存储失败内容
                    log.error("{} fail", queueMsg.getRequestMsg().getURL());
                }
            }
        } catch (Exception e) {
            log.error("抓取URL: {} 错误, 错误代码: {}",
                    crawlerMsg.getQueueMessage().getRequestMsg().getURL(), e);
        }
    }

    /**
     * Inner crawl, that return the whole information of a crawl task which need to crawl multi URLs.
     *
     * @param wrapperInfo wrapperInfo
     * @param mergeInfo   mergeInfo
     * @param urlLevel    level of url
     * @param msgs        crawl message
     * @return whole information of a crawl task
     */
    private Map innerCrawl(Map wrapperInfo, String mergeInfo, int urlLevel, List<CrawlerMessage> msgs) {

        Map wholeInfo = new ConcurrentHashMap<>();

        if (null != wrapperInfo && null != wrapperInfo.get(Wrapper.DATA_KEY)) {
            if (TemplateParseHandler.MERGE_XML_VALUE.equals(mergeInfo)) {
                wrapperInfo.put(TemplateParseHandler.MERGE_KEY, TemplateParseHandler.MERGE_XML_VALUE);
                wrapperInfo.put(TemplateParseHandler.FOCUS_ID_KEY, String.valueOf(urlLevel));
                wholeInfo.putAll(wrapperInfo);
            }
        }
        // TODO 利用线程池抓取
        if (CollectionUtils.isNotEmpty(msgs)) {
            List<QueueMessage> cMsgs = filterNotCrawledMsgs(msgs);
            if (CollectionUtils.isNotEmpty(cMsgs)) {
                innerQueue.addAll(cMsgs);
            }
            while (!innerQueue.isEmpty()) {
                QueueMessage msg = innerQueue.poll();
                CrawlerMessage crawlerMsg = new CrawlerMessage();
                crawlerMsg.setQueueMessage(msg);
                Map partInfo = nucleusFusion(crawlerMsg);
                wrapMerge(wholeInfo, partInfo);
            }
        }
        return wholeInfo;
    }

    /**
     * The message which not been crawled by utilizing Bloom Filter
     *
     * @param msgs crawl message
     * @return message which not been crawled
     */
    private List<QueueMessage> filterNotCrawledMsgs(List<CrawlerMessage> msgs) {
        List<QueueMessage> cMsgs = new ArrayList<>();
        for (CrawlerMessage msg : msgs) {
            QueueMessage qMsg = msg.getQueueMessage();
            String url = qMsg.getRequestMsg().getURL();
            if (!flectionST.getBloomFilter().contains(url)) {
                cMsgs.add(qMsg);
            }
        }
        return cMsgs;
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
        if (wholeInfo.containsKey(TemplateParseHandler.MERGE_KEY)
                && partInfo.containsKey(TemplateParseHandler.MERGE_KEY)) {
            if (wholeInfo.get(TemplateParseHandler.FOCUS_ID_KEY).equals(partInfo.get(TemplateParseHandler.MERGE_KEY))) {
                Map part = (Map) partInfo.get(Wrapper.DATA_KEY);
                Map whole = (Map) wholeInfo.get(Wrapper.DATA_KEY);
                for (Object key : part.keySet()) {
                    if (whole.containsKey(key)) {
                        // 合并相同key数据
                        Object focusData = whole.get(key);
                        Object partData = part.get(key);
                        if (focusData instanceof List) {
                            if (partData instanceof String) {
                                if (StringUtils.isNotBlank((String) partData)) {
                                    ((List) focusData).add(partData);
                                }
                            }
                            if (partData instanceof List) {
                                if (((List) partData).size() > 0) {
                                    ((List) focusData).addAll((List) partData);
                                }
                            }
                        } else if (focusData instanceof String) {
                            if (partData instanceof String) {
                                // 若focus data中, 与part data相同key的有值/不为空, 保留focus data值, part data忽略
                                if (StringUtils.isBlank((String) focusData)) {
                                    whole.put(key, partData);
                                }
                            }
                            if (partData instanceof List) {
                                if (((List) partData).size() > 0) {
                                    ((List) focusData).addAll((List) partData);
                                }
                            }
                        }
                    } else {
                        // 新增key数据
                        Object partData = part.get(key);
                        if (partData instanceof String) {
                            if (StringUtils.isNotBlank((String) partData)) {
                                whole.put(key, partData);
                            }
                        } else if (partData instanceof List) {
                            if (((List) partData).size() > 0) {
                                whole.put(key, partData);
                            }
                        }
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
                    crawlerMgs.getQueueMessage().getRequestMsg().setPostRequest(true);
                }
            }
        }
    }

    public CrawlerMessage crawl(CrawlerMessage crawlerMgs) {

        QueueMessage queueMsg = crawlerMgs.getQueueMessage();
        Category category = queueMsg.getCategory();

        initTemplate(category);
        setPostMethod(crawlerMgs);

        RequestMessage requestMsg = queueMsg.getRequestMsg();

        String url = requestMsg.getURL();
        if (!flectionST.getBloomFilter().contains(url)) {
            int i = 10;
            while (i > 0) {
                i--;
                log.info("Crawl url {}", url);
                ResponseMessage rspMsg;

                Proxy proxy = null;
                if (useProxy && !"proxy".equals(category.getType())) {
                    proxy = proxyClient.requestProxy(category.getName());
                    if (proxy == null || proxy.isNull()) {
                        continue;
                    }
                    HttpClient client = BasicHttpClient.buildProxyHttpClient(proxy);
                    if (requestMsg.isPostRequest()) {
                        rspMsg = client.POST(requestMsg);
                    } else {
                        rspMsg = client.GET(requestMsg);
                    }

                } else {
                    if (requestMsg.isPostRequest()) {
                        rspMsg = this.httpClient.POST(requestMsg);
                    } else {
                        rspMsg = this.httpClient.GET(requestMsg);
                    }
                }

                if (StringUtils.isNotBlank(rspMsg.getBody())) {

                    flectionST.getBloomFilter().add(url);

                    pageType(crawlerMgs, rspMsg.getBody());

                    crawlerMgs.setCrawlTime(new Date());
                    crawlerMgs.setResponseMsg(rspMsg);

                    if (isStorePage) {
                        webPageStore.save(crawlerMgs);
                    }

                    try {
                        SiteSpeed siteSpeed = flectionST.getSpeedController().getSpeedCache().get(category);
                        if (null != siteSpeed) {
                            Thread.sleep(siteSpeed.randomSpeed());
                        }
                    } catch (InterruptedException e) {
                        log.error("Sleep error: {}.", e);
                    }
                    if (useProxy && null != proxy) {
                        proxyClient.feedback(proxy, ProxyCode.OK, category.getName());
                    }
                    return crawlerMgs;
                } else if (ImageStore.isImageURL(url)) {
                    return null;
                } else {
                    if (useProxy && null != proxy) {
                        proxyClient.feedback(proxy, ProxyCode.ERROR, category.getName());
                    } else {
                        log.error("response body is null!");
                    }
                }
            }
        } else {
            log.info("The url {} has been crawled", url);
        }
        return null;
    }
}