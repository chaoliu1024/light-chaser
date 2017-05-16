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

package me.chaoliu.lightchaser.core.wrapper.template;

import me.chaoliu.lightchaser.core.daemon.JobType;
import me.chaoliu.lightchaser.core.daemon.Star;
import me.chaoliu.lightchaser.core.config.LoadConfig;
import me.chaoliu.lightchaser.core.crawl.CrawlerMessage;
import me.chaoliu.lightchaser.core.crawl.HtmlDom;
import me.chaoliu.lightchaser.core.crawl.template.WrapperTemplate;
import me.chaoliu.lightchaser.core.protocol.http.RequestMessage;
import me.chaoliu.lightchaser.core.protocol.http.ResponseMessage;
import me.chaoliu.lightchaser.core.util.FileUtil;
import org.dom4j.Element;
import org.dom4j.Node;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * MyTest HtmlWrapperTest class.
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class HtmlWrapperTest {

    private CrawlerMessage crawlerMsg;
    private WrapperEntity wrapperEntity;
    private Wrapper wrapper;

    private CrawlerMessage initCrawlerMessage() {

        String url = "http://www.66ip.cn/1.html";
        int URLLevel = 50;
        String jobName = "66ip";
        InputStream in = this.getClass().getResourceAsStream("/test-page/66ip.html");
        String htmlContent = FileUtil.streamToString(in);

        RequestMessage requestMsg = new RequestMessage();
        requestMsg.setURL(url);

        HtmlDom htmlDom = new HtmlDom(htmlContent);

        ResponseMessage responseMsg = new ResponseMessage();
        responseMsg.setBody(htmlContent);

        CrawlerMessage crawlerMsg = new CrawlerMessage();
        crawlerMsg.setJobName(jobName);
        crawlerMsg.setURLLevel(URLLevel);
        crawlerMsg.setResponseMsg(responseMsg);
        crawlerMsg.setRequestMsg(requestMsg);
        crawlerMsg.setHtmlDom(htmlDom);

        return crawlerMsg;
    }

    @Before
    public void initData() {

        Map config = LoadConfig.readLightChaserConfig();
        Star photon = new Star();
        photon.initTemplateRootPath(config, JobType.OTA);

        crawlerMsg = initCrawlerMessage();
        wrapperEntity = new WrapperEntity(crawlerMsg);

        WrapperTemplate wrapperTemplate = new WrapperTemplate(crawlerMsg.getJobName());

        List<Node> wrapperPatternNodes = wrapperTemplate.getWrapperPatternNodes();

        for (Node patternNode : wrapperPatternNodes) {

            Element patternEle = (Element) patternNode;
            int levelId = Integer.parseInt(patternEle.attributeValue("levelId"));
            String type = patternEle.attributeValue("type");

            int level = crawlerMsg.getURLLevel();
            if ((level == levelId) && "html".equals(type)) {
                wrapper = new HtmlWrapper(patternNode);
            }
        }
    }

    @Test
    public void testExtract() {
        Map result = wrapper.extract(wrapperEntity);
        System.out.println(result);
    }
}
