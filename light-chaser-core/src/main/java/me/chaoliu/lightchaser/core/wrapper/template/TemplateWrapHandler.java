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

import lombok.extern.slf4j.Slf4j;
import me.chaoliu.lightchaser.core.daemon.Star;
import me.chaoliu.lightchaser.core.crawl.CrawlerMessage;
import me.chaoliu.lightchaser.core.crawl.template.WrapperTemplate;
import me.chaoliu.lightchaser.core.wrapper.WrapHandler;
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

        String jobName = crawlerMgs.getJobName();

        WrapperEntity wrapperEntity = new WrapperEntity(crawlerMgs);

        // 开始抽取需要内容
        WrapperTemplate wrapperTemplate = Star.templateCache.getTemplates().get(jobName).getWrapperTemplate();

        List<Node> wrapperPatternNodes = wrapperTemplate.getWrapperPatternNodes();

        for (Node patternNode : wrapperPatternNodes) {

            Element patternEle = (Element) patternNode;

            int levelId;
            String pageType;

            if (StringUtils.isNotBlank(patternEle.attributeValue(Wrapper.LEVEL_ID))) {
                levelId = Integer.parseInt(patternEle.attributeValue(Wrapper.LEVEL_ID));
            } else {
                log.error("Wrapper attribute 'levelId' is null");
                return null;
            }
            if (StringUtils.isNotBlank(patternEle.attributeValue(Wrapper.WRAPPRE_TYPE))) {
                pageType = patternEle.attributeValue(Wrapper.WRAPPRE_TYPE);
            } else {
                log.error("Wrapper attribute 'type' is null");
                return null;
            }
            int level = crawlerMgs.getURLLevel();
            if (level == levelId) {
                if ("html".equals(pageType.toLowerCase())) {
                    // TODO design pattern
                    Wrapper wrapper = new HtmlWrapper(patternNode);
                    Map result = wrapper.extract(wrapperEntity);
                    result.put(Wrapper.JOB_TYPE_KEY, crawlerMgs.getJobType().getTypeName());
                    return result;
                } else if ("json".equals(pageType.toLowerCase())) {
                    Wrapper wrapper = new JsonWrapper(patternNode);
                    Map result = wrapper.extract(wrapperEntity);
                    result.put(Wrapper.JOB_TYPE_KEY, crawlerMgs.getJobType().getTypeName());
                    return result;
                }
            }
        }

        long time2 = System.currentTimeMillis();
        log.info("Extract fields cost {} ms.", (time2 - time1));
        return null;
    }
}
