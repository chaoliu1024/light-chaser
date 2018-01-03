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
import group.chaoliu.lightchaser.core.protocol.http.BasicHttpClient;
import group.chaoliu.lightchaser.core.wrapper.WrapHandler;
import group.chaoliu.lightchaser.core.wrapper.WrapResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.*;

/**
 * 基于模板的抽取
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class TemplateWrapHandler implements WrapHandler {

    /**
     * key: wrapper levelId
     */
    private Map nodesNullableInfo = new HashMap();

    @Override
    public WrapResult wrap(CrawlerMessage crawlerMgs) {

        // TODO 只用AOP处理该功能
        long time1 = System.currentTimeMillis();

        WrapResult wrapResult = new WrapResult();

        WrapperEntity wrapperEntity = new WrapperEntity(crawlerMgs);

        // 开始抽取需要内容
        WrapperTemplate wrapperTemplate = Planet.templateCache.getTemplates().
                get(crawlerMgs.getQueueMessage().getCategory()).getWrapperTemplate();

        List<Node> wrapperPatternNodes = wrapperTemplate.getWrapperPatternNodes();

        for (Node rootNode : wrapperPatternNodes) {

            Element rootEle = (Element) rootNode;

            int levelId;
            String pageType;

            if (StringUtils.isNotBlank(rootEle.attributeValue(Wrapper.LEVEL_ID))) {
                levelId = Integer.parseInt(rootEle.attributeValue(Wrapper.LEVEL_ID));
                if (StringUtils.isNotBlank(rootEle.attributeValue(Wrapper.WRAPPRE_TYPE))) {
                    pageType = rootEle.attributeValue(Wrapper.WRAPPRE_TYPE);
                } else {
                    log.error("Wrapper attribute 'type' is null");
                    return null;
                }
            } else {
                log.error("Wrapper attribute 'levelId' is null");
                return null;
            }

            int level = crawlerMgs.getQueueMessage().getUrlLevel();

            if (level == levelId) {
                Object nodeInfo = nodesNullableInfo.get(String.valueOf(level));
                if (null == nodeInfo) {
                    nodesNullableInfo.put(String.valueOf(level), "");
                    wrapperNodeKeys(rootEle, String.valueOf(level), nodesNullableInfo);
                }

                if ("html".equals(pageType.toLowerCase())) {
                    // TODO design pattern
                    Wrapper wrapper = new HtmlWrapper(rootEle);
                    Map result = resultMap(wrapper, wrapperEntity, crawlerMgs);
                    wrapResult.setResult(result);
                    verifyWrapperResult(result, levelId, wrapResult);
                    return wrapResult;
                } else if ("json".equals(pageType.toLowerCase())) {
                    Wrapper wrapper = new JsonWrapper(rootEle);
                    Map result = resultMap(wrapper, wrapperEntity, crawlerMgs);
                    wrapResult.setResult(result);
                    verifyWrapperResult(result, levelId, wrapResult);
                    return wrapResult;
                }
            }
        }

        long time2 = System.currentTimeMillis();
        log.info("Extract fields cost {} ms.", (time2 - time1));
        return null;
    }

    /**
     * 根据 nullable 校验抽取结果
     *
     * @param result     extract result
     * @param levelId    wrapper level id
     * @param wrapResult wrap result
     */
    public void verifyWrapperResult(Map result, int levelId, WrapResult wrapResult) {
        List nodes = (List) nodesNullableInfo.get(String.valueOf(levelId));
        Map allData = (Map) result.get(Wrapper.DATA_KEY);
        for (int i = 0, l = nodes.size(); i < l; i++) {
            Object node = nodes.get(i);
            if (node instanceof Map) {
                Map nodeMap = (Map) node;
                Iterator ite = nodeMap.entrySet().iterator();
                while (ite.hasNext()) {
                    Map.Entry entry = (Map.Entry) ite.next();
                    String nodeName = (String) entry.getKey();
                    // 抽取值
                    Object data = allData.get(nodeName);
                    recurseVerify(entry, data, wrapResult);
                }
            }
        }
    }

    /**
     * 递归检查
     *
     * @param key  node nullable entry, key:node name, value: nullable 0/1
     * @param data 抽取到的数值
     */
    public void recurseVerify(Map.Entry key, Object data, WrapResult wrapResult) {
        Object nullableValue = key.getValue();
        String nodeName = (String) key.getKey();

        // 抽取值
        if (nullableValue instanceof String) {
//            if (null != data) {
//                log.info("nodeName: " + nodeName + "\t nullable: " + nullableValue + "\t data: " + data.toString());
//            } else {
//                log.info("nodeName: " + nodeName + "\t nullable: " + nullableValue + "\t data: null");
//            }
            if (Wrapper.NOT_NULL_TAG.equals(nullableValue)) {
                if (null == data) {
                    errorField(wrapResult, nullableValue, nodeName);
                } else {
                    if (data instanceof String && StringUtils.isBlank(data.toString())) {
                        errorField(wrapResult, nullableValue, nodeName);
                    }
                }
            }
        } else if (nullableValue instanceof List) {
            // node下有 node 的情况
            List subNodes = (List) nullableValue;
            for (int j = 0, s = subNodes.size(); j < s; j++) {
                Object subNode = subNodes.get(j);
                // 抽取数据为list
                if (data instanceof List) {
                    List dataList = (List) data;
                    for (int n = 0, ll = dataList.size(); n < ll; n++) {
                        Object dataL = dataList.get(n);
                        if (dataL instanceof Map) {
                            Map dataM = (Map) dataL;
                            if (subNode instanceof Map) {
                                Iterator ite = ((Map) subNode).entrySet().iterator();
                                while (ite.hasNext()) {
                                    Map.Entry entry = (Map.Entry) ite.next();
                                    if (dataM.containsKey(entry.getKey())) {
                                        String eKey = entry.getKey().toString();
                                        metaVerify(subNode, dataM.get(eKey), wrapResult);
                                    }
                                }
                            }
                        } else {
                            metaVerify(subNode, dataL, wrapResult);
                        }
                    }
                }
            }
        }
    }

    /**
     * 最基本的元数据校验
     */
    private void metaVerify(Object subNode, Object data, WrapResult wrapResult) {
        if (subNode instanceof Map) {
            Map m = (Map) subNode;
            Iterator ite = m.entrySet().iterator();
            while (ite.hasNext()) {
                Map.Entry entry = (Map.Entry) ite.next();
                recurseVerify(entry, data, wrapResult);
            }
        }
    }

    /**
     * 抽取失败, 记录失败字段
     */
    private void errorField(WrapResult wrapResult, Object nullableValue, String nodeName) {
        wrapResult.setSuccess(false);
        List<String> errorField = wrapResult.getErrorField();
        errorField.add(nodeName);
        wrapResult.setErrorField(errorField);
        log.error("抽取失败--> nodeName: " + nodeName + "\t nullable: " + nullableValue + "\t data: null");
    }

    /**
     * 保留每个抽取wrapper节点的node key信息，用于校验抽取是否完整
     *
     * @param element wrapper element
     * @param key     key
     * @param keySet  key set
     */
    @SuppressWarnings("unchecked")
    public void wrapperNodeKeys(Element element, String key, Map keySet) {
        List<Node> nodes = element.selectNodes("./node");
        for (Node node : nodes) {
            Element subEle = (Element) node;
            String subKey = subEle.attributeValue(Wrapper.NODE_NAME).trim();

            if (null == subEle.attributeValue(Wrapper.NODE_NULLABLE)) {
                throw new NullPointerException("son of " + key + ": " + Wrapper.NODE_NULLABLE + " attribute is null");
            }
            if (StringUtils.isBlank(subKey)) {
                throw new NullPointerException("son of " + key + ": " + Wrapper.NODE_NAME + " attribute is null");
            }

            Map temp = new HashMap();
            temp.put(subKey, subEle.attributeValue(Wrapper.NODE_NULLABLE).trim());

            if (keySet.containsKey(key)) {
                Object o = keySet.get(key);
                if (o instanceof String) {
                    List l = new ArrayList();
                    l.add(temp);
                    keySet.put(key, l);
                } else if (o instanceof List) {
                    List l = (List) keySet.get(key);
                    l.add(temp);
                    keySet.put(key, l);
                }
            } else {
                Iterator ite = temp.entrySet().iterator();
                while (ite.hasNext()) {
                    Map.Entry entry = (Map.Entry) ite.next();
                    keySet.put(entry.getKey(), entry.getValue());
                }
            }
            wrapperNodeKeys(subEle, subKey, temp);
        }
    }

    @SuppressWarnings("unchecked")
    private Map resultMap(Wrapper wrapper, WrapperEntity wrapperEntity, CrawlerMessage crawlerMgs) {
        Map result = wrapper.extract(wrapperEntity);
        result.put(Wrapper.CATEGORY_TYPE_KEY, crawlerMgs.getQueueMessage().getCategory().getType());
        if (null != result.get(Wrapper.DATA_KEY)) {
            if (result.get(Wrapper.DATA_KEY) instanceof Map) {
                ((Map) result.get(Wrapper.DATA_KEY)).put(Wrapper.CRAWL_TIME, crawlerMgs.getCrawlTime());

                String domainKey = BasicHttpClient.getDomainKey(crawlerMgs.getQueueMessage().getRequestMsg().getURL());
                if (StringUtils.isNotBlank(domainKey)) {
                    ((Map) result.get(Wrapper.DATA_KEY)).put(Wrapper.DOMAIN_KEY, domainKey);
                }
            }
        }
        return result;
    }
}
