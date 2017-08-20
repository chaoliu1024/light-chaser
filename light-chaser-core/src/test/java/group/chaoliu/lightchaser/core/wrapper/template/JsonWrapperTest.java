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

import com.alibaba.fastjson.JSONObject;
import group.chaoliu.lightchaser.core.config.LoadConfig;
import group.chaoliu.lightchaser.core.crawl.CrawlerMessage;
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
 * MyTest JsonWrapperTest class.
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class JsonWrapperTest {

    private CrawlerMessage crawlerMsg;
    private WrapperEntity wrapperEntity;
    private Wrapper wrapper;

    private CrawlerMessage initCrawlerMessage() {

        String url = "http://vacations.ota.com/bookingnext/Calendar/CalendarInfo?ProductID=3090323&StartCity=141&SalesCity=141&MinPrice=2340&EffectDate=2016-09-13&ExpireDate=2016-11-05";
        int URLLevel = 110;
        Job job = new Job("ota", "ctrip");

        InputStream in = this.getClass().getResourceAsStream("/test-page/ctrip_date_price.json");
        String jsonContent = FileUtil.streamToString(in);

        RequestMessage requestMsg = new RequestMessage();
        requestMsg.setURL(url);
        Object json = JSONObject.parse(jsonContent);

        ResponseMessage responseMsg = new ResponseMessage();
        responseMsg.setBody(jsonContent);

        CrawlerMessage crawlerMsg = new CrawlerMessage();
        crawlerMsg.setJob(job);
        crawlerMsg.setURLLevel(URLLevel);
        crawlerMsg.setResponseMsg(responseMsg);
        crawlerMsg.setRequestMsg(requestMsg);
        crawlerMsg.setJson(json);

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
            if ((level == levelId) && "json".equals(type)) {
                wrapper = new JsonWrapper(patternNode);
            }
        }
    }

    @Test
    public void testExtract() {
        Map result = wrapper.extract(wrapperEntity);
        System.out.println(result);
    }
}