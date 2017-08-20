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

import group.chaoliu.lightchaser.core.crawl.CrawlerMessage;
import group.chaoliu.lightchaser.core.crawl.template.WrapperTemplate;
import group.chaoliu.lightchaser.core.daemon.planet.Planet;
import group.chaoliu.lightchaser.core.wrapper.WrapHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class TemplateWrapHandler implements WrapHandler {

    @Override
    public Map wrap(CrawlerMessage crawlerMgs) {

        // TODO 只用AOP处理该功能
        long time1 = System.currentTimeMillis();

        WrapperEntity wrapperEntity = new WrapperEntity(crawlerMgs);

        // 开始抽取需要内容
        WrapperTemplate wrapperTemplate = Planet.templateCache.getTemplates().
                get(crawlerMgs.getJob()).getWrapperTemplate();

        List<Node> wrapperPatternNodes = wrapperTemplate.getWrapperPatternNodes();

        for (Node patternNode : wrapperPatternNodes) {

            Element patternEle = (Element) patternNode;

            int levelId;
            String pageType;

            if (StringUtils.isNotBlank(patternEle.attributeValue(Wrapper.LEVEL_ID))) {
                levelId = Integer.parseInt(patternEle.attributeValue(Wrapper.LEVEL_ID));
                if (StringUtils.isNotBlank(patternEle.attributeValue(Wrapper.WRAPPRE_TYPE))) {
                    pageType = patternEle.attributeValue(Wrapper.WRAPPRE_TYPE);
                } else {
                    log.error("Wrapper attribute 'type' is null");
                    return null;
                }
            } else {
                log.error("Wrapper attribute 'levelId' is null");
                return null;
            }

            int level = crawlerMgs.getURLLevel();
            if (level == levelId) {
                if ("html".equals(pageType.toLowerCase())) {
                    // TODO design pattern
                    Wrapper wrapper = new HtmlWrapper(patternNode);
                    return resultMap(wrapper, wrapperEntity, crawlerMgs);
                } else if ("json".equals(pageType.toLowerCase())) {
                    Wrapper wrapper = new JsonWrapper(patternNode);
                    return resultMap(wrapper, wrapperEntity, crawlerMgs);
                }
            }
        }

        long time2 = System.currentTimeMillis();
        log.info("Extract fields cost {} ms.", (time2 - time1));
        return null;
    }

    private Map resultMap(Wrapper wrapper, WrapperEntity wrapperEntity, CrawlerMessage crawlerMgs) {
        Map result = wrapper.extract(wrapperEntity);
        result.put(Wrapper.JOB_TYPE_KEY, crawlerMgs.getJob().getType());
        if (null != result.get(Wrapper.DATA_KEY)) {
            if (result.get(Wrapper.DATA_KEY) instanceof Map) {
                ((Map) result.get(Wrapper.DATA_KEY)).put(Wrapper.CRAWL_TIME, crawlerMgs.getCrawlTime());
            }
        }
        return result;
    }
}
