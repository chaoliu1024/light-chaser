/*
 * Copyright (c) 2017, Chao Liu (chaoliu1024@gmail.com). All rights reserved.
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

package group.chaoliu.lightchaser.core.daemon.photon;

import group.chaoliu.lightchaser.core.config.Constants;
import group.chaoliu.lightchaser.core.config.LoadConfig;
import group.chaoliu.lightchaser.core.crawl.template.Template;
import group.chaoliu.lightchaser.core.daemon.Deamon;
import group.chaoliu.lightchaser.core.daemon.Job;
import group.chaoliu.lightchaser.core.daemon.LocalDaemon;
import group.chaoliu.lightchaser.core.daemon.star.Star;
import group.chaoliu.lightchaser.core.fission.BaseFission;
import group.chaoliu.lightchaser.core.fission.proxy.ProxyFission;
import group.chaoliu.lightchaser.core.fission.proxy.ProxyValidator;
import group.chaoliu.lightchaser.core.fission.proxy.ProxyWeb;
import group.chaoliu.lightchaser.core.fission.proxy.domain.ProxyPO;
import group.chaoliu.lightchaser.core.fission.proxy.mapper.ProxyMapper;
import group.chaoliu.lightchaser.core.protocol.http.Proxy;
import group.chaoliu.lightchaser.core.util.SpringBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class Photon extends Deamon {

    private BaseFission fission = SpringBeanUtil.fissionBean("proxy" + BaseFission.FISSION_BEAN_SUFFIX);


    public static final ApplicationContext CONTEXT = new ClassPathXmlApplicationContext("applicationContext.xml");

    private ProxyMapper proxyMapper = CONTEXT.getBean(ProxyMapper.class);


    public void initProxyWeb(final Map config) {

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

    public List<String> initProxyJob(Map lightChaserConfig) {

        initTemplateRootPath(lightChaserConfig);

        String proxySites = Template.templateRootPath + "proxy" + File.separator + "proxy_sites.xml";
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

    /**
     * 抓取代理任务
     *
     * @param lightChaserConfig light chaser config
     */
    public void crawlProxy(Map lightChaserConfig) {
        initProxyWeb(lightChaserConfig);
        List<String> proxies = initProxyJob(lightChaserConfig);
        for (String proxy : proxies) {
            Job job = new Job("proxy", proxy);
            LocalDaemon localDaemon = new LocalDaemon();
            localDaemon.setStar(new Star());
            localDaemon.getStar().init(job, lightChaserConfig);
            localDaemon.initSpaceTime(job);
            localDaemon.submitFlection(lightChaserConfig, localDaemon.flectionST);
        }
    }

    public void validateProxy() {
        List<Proxy> cacheProxy = proxyMapper.fetchAllProxies();
        ProxyFission fission = new ProxyFission();
        List<ProxyPO> proxyPOS = fission.validateProxy(cacheProxy);


    }


    public static void main(String[] args) {
        Map lightChaserConfig = LoadConfig.readLightChaserConfig();
        Photon photon = new Photon();
        photon.crawlProxy(lightChaserConfig);
        photon.fission.finish();
        System.exit(0);
    }
}
