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

package group.chaoliu.lightchaser.core.wrapper.template;

import group.chaoliu.lightchaser.core.config.LoadConfig;
import group.chaoliu.lightchaser.core.crawl.CrawlerMessage;
import group.chaoliu.lightchaser.core.crawl.HtmlDom;
import group.chaoliu.lightchaser.core.crawl.template.WrapperTemplate;
import group.chaoliu.lightchaser.core.daemon.Job;
import group.chaoliu.lightchaser.core.daemon.star.Star;
import group.chaoliu.lightchaser.core.protocol.http.RequestMessage;
import group.chaoliu.lightchaser.core.protocol.http.ResponseMessage;
import group.chaoliu.lightchaser.core.util.FileUtil;
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

        Job job = new Job("proxy", jobName);
        RequestMessage requestMsg = new RequestMessage();
        requestMsg.setURL(url);

        HtmlDom htmlDom = new HtmlDom(htmlContent);

        ResponseMessage responseMsg = new ResponseMessage();
        responseMsg.setBody(htmlContent);

        CrawlerMessage crawlerMsg = new CrawlerMessage();
        crawlerMsg.setJob(job);
        crawlerMsg.setURLLevel(URLLevel);
        crawlerMsg.setResponseMsg(responseMsg);
        crawlerMsg.setRequestMsg(requestMsg);
        crawlerMsg.setHtmlDom(htmlDom);

        return crawlerMsg;
    }

    @Before
    public void initData() {

        Map config = LoadConfig.readLightChaserConfig();
        Star star = new Star();
//        star.initTemplateRootPath(config);

        crawlerMsg = initCrawlerMessage();
        wrapperEntity = new WrapperEntity(crawlerMsg);

        WrapperTemplate wrapperTemplate = new WrapperTemplate(crawlerMsg.getJob());

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
