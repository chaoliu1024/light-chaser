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

package me.chaoliu.lightchaser.core.daemon;

import lombok.extern.slf4j.Slf4j;
import me.chaoliu.lightchaser.core.config.Constants;
import me.chaoliu.lightchaser.core.config.LoadConfig;
import me.chaoliu.lightchaser.core.crawl.CrawlSpeedController;
import me.chaoliu.lightchaser.core.crawl.Crawler;
import me.chaoliu.lightchaser.core.crawl.CrawlerMessage;
import me.chaoliu.lightchaser.core.crawl.SiteSpeed;
import me.chaoliu.lightchaser.core.crawl.template.*;
import me.chaoliu.lightchaser.core.filter.BloomFilter;
import me.chaoliu.lightchaser.core.fission.proxy.ProxyValidator;
import me.chaoliu.lightchaser.core.fission.proxy.ProxyWeb;
import me.chaoliu.lightchaser.core.persistence.hbase.HBaseClient;
import me.chaoliu.lightchaser.core.persistence.hbase.StarJobFamily;
import me.chaoliu.lightchaser.core.queue.memory.UnVisitedMessagePool;
import me.chaoliu.lightchaser.core.util.SpringBeanUtil;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 恒星，光的产生，相当于一个处理worker
 * Star interface to start crawler
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class Star {

    public static TemplateCache templateCache = new TemplateCache();

    public static CrawlSpeedController speedController = new CrawlSpeedController();

    /**
     * root path of template configuration files
     */
    public static String templateRootPath;

    // global singleton url
    // 必须线程安全
    public static UnVisitedMessagePool unVisitedMessagePool;

    public static BloomFilter<String> bloomFilter = new BloomFilter<>(0.0000001, Integer.MAX_VALUE);

    private Crawler crawler = SpringBeanUtil.crawlerBean();

    public Star() {
        initMessagePool();
    }

    // TODO 只支持单机爬取
    public void initMessagePool() {
        this.unVisitedMessagePool = UnVisitedMessagePool.getInstance();
    }

    public void initSeedURLs(Job job) {

        String seedURLPath = templateRootPath + job.getJobName() + File.separator + "seed_url.xml";
        File seedFile = new File(seedURLPath);
        SeedURLTemplate seedURLTemplate = new SeedURLTemplate(seedFile);
        seedURLTemplate.initSeedURLs(job);
        log.info("Init {} seed urls finish.", job.getJobName());
    }

    /**
     * load craw_path.xml and wrapper.xml template file
     *
     * @param jobName name of site
     */
    public void loadTemplate(String jobName) {

        log.info("Load the {} template file.", jobName);
        // instance crawl_path.xml template jobName
        CrawlTemplate crawlTemplate = new CrawlTemplate(jobName);
        // instance wrapper.xml template file
        WrapperTemplate wrapperTemplate = new WrapperTemplate(jobName);

        Template template = new Template(jobName, crawlTemplate, wrapperTemplate);
        templateCache.putSiteTemplate(jobName, template);
    }

    /**
     * initialize the initial speed of one site
     *
     * @param jobName name of site
     */
    public void siteSpeed(String jobName) {
        CrawlTemplate crawlConfig = Star.templateCache.getTemplates().get(jobName).getCrawlTemplate();
        int minInterval = crawlConfig.getMinInterval();
        int maxInterval = crawlConfig.getMaxInterval();
        SiteSpeed speed = new SiteSpeed(jobName, minInterval, maxInterval);
        speedController.setSiteSpeed(jobName, speed);
        log.info("Set the {} speed finished. The min interval is {}, " +
                "and the max interval is {}", jobName, minInterval, maxInterval);
    }

    public static void initTemplateRootPath(final Map config, JobType jobType) {
        String templatePath;
        if (jobType == JobType.PROXY) {
            templatePath = config.get(Constants.PROXY_PATH).toString().trim();
        } else {
            templatePath = config.get(Constants.TEMPLATE_PATH).toString().trim();
        }
        if (StringUtils.isNotBlank(templatePath)) {
            if (!templatePath.endsWith("\\")) {
                Star.templateRootPath = templatePath + File.separator;
            } else {
                Star.templateRootPath = templatePath;
            }
        } else {
            throw new TemplatePathException("template is null");
        }
    }

    public void radiate(Job job) {

        log.info("Start {} job...", job.getJobName());

        loadTemplate(job.getJobName());
        initSeedURLs(job);
        siteSpeed(job.getJobName());

        while (!unVisitedMessagePool.isEmpty()) {
            CrawlerMessage crawlerMgs = unVisitedMessagePool.getCrawlerMessage();
            crawler.run(crawlerMgs);
        }

        crawler.waitFinish();
    }

    /**
     * 初始化job的相关信息
     */
    public void initJob(Job job) {
        HBaseClient hbase;
        System.setProperty("hadoop.home.dir", "d:\\hadoop_home");
        try {
            hbase = new HBaseClient(job.getJobName());
            hbase.createTable(job.getJobName(), StarJobFamily.HBASE_FAMILIES, false);
        } catch (IOException e) {
            log.error("create HBase table error!", job.getJobName());
        }
        radiate(job);
    }

    public static void initProxyWeb(final Map config) {

        String proxyFile = config.get(Constants.PROXY_FILE).toString().trim();
        Map proxyConfig = LoadConfig.readConfig(proxyFile);

        Map<String, List> proxyWebs = (Map) proxyConfig.get(Constants.PROXY_VERIFIERS);
        for (Map.Entry<String, List> web : proxyWebs.entrySet()) {
            ProxyWeb proxyWeb = new ProxyWeb();
            ProxyValidator.webProxy.put(web.getKey(), proxyWeb);
            for (Object webInfos : web.getValue()) {
                Map<String, String> info = (Map) webInfos;
                if (null != info.get(ProxyValidator.WEB_PROXY_URL)) {
                    proxyWeb.setWebURL(info.get(ProxyValidator.WEB_PROXY_URL));
                }
                if (null != info.get(ProxyValidator.WEB_PROXY_XPATH)) {
                    proxyWeb.setValidatorXpath(info.get(ProxyValidator.WEB_PROXY_XPATH));
                }
                if (null != info.get(ProxyValidator.WEB_PROXY_VALUE)) {
                    proxyWeb.setXpathValue(info.get(ProxyValidator.WEB_PROXY_VALUE));
                }
            }
        }
    }


    public static List<String> initProxyJob() {
        String proxySites = Star.templateRootPath + "proxy_sites.xml";
        File file = new File(proxySites);
        SAXReader reader = new SAXReader();

        List<String> proxies = new ArrayList<>();
        try {
            Document document = reader.read(file);
            Element root = document.getRootElement();

            @SuppressWarnings("unchecked")
            List<Node> sites = root.selectNodes("/proxy_sites/site");
            for (Node site : sites) {
                Element ele = (Element) site;
                if ("true".endsWith(ele.attributeValue("enable"))) {
                    proxies.add(ele.getTextTrim());
                }
            }
        } catch (DocumentException e) {
            log.error("Read proxy_sites.xml error: {}", e);
        }
        return proxies;
    }

    public static void main(String[] args) {

        if (null == args || args.length < 1) {
            throw new InvalidParameterException("need to input job name");
        }

        String jobName = args[0];
        JobType jobType;

        if (args.length == 1) {
            jobType = JobType.OTA;
        } else {
            jobType = JobType.parseType(args[1]);
        }

        Map config = LoadConfig.readLightChaserConfig();

        Star star = new Star();

        initTemplateRootPath(config, jobType);

        if (jobType == JobType.PROXY) {
            initProxyWeb(config);
            List<String> proxies = initProxyJob();
            for (String proxy : proxies) {
                Job job = new Job(jobType, proxy);
                star.initJob(job);
            }
        } else {
            Job job = new Job(jobType, jobName);
            star.initJob(job);
        }
    }
}