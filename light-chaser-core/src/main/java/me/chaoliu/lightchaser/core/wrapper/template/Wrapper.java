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
import me.chaoliu.lightchaser.core.parser.template.ParseAction;
import me.chaoliu.lightchaser.core.util.Dom4jUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.*;

/**
 * This abstract class is the superclass of all classes representing a wrapper.
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public abstract class Wrapper {

    public static final String INFO_KEY = "info";

    public static final String LEVEL_ID = "levelId";

    public static final String JOB_TYPE_KEY = "job";

    public static final String WRAPPRE_TYPE = "type";

    public static final String NODE_TYPE = "type";

    public static final String NODE_NAME = "name";

    public static final String NODE_KEY = "key";

    public static final String NODE_XPATH = "xpath";

    public static final String PLUGIN_CLASS = "class";


    protected Node wrapperNode;

    public Wrapper(Node node) {
        this.wrapperNode = node;
    }

    public abstract Map extract(WrapperEntity wrapperEntity);

    /**
     * extract fields
     *
     * @param entity   wrapper entity
     * @param wrapNode wrapper node
     * @return
     */
    public List<WrapperEntity> extractFields(WrapperEntity entity, Node wrapNode) {

        List<WrapperEntity> results = new ArrayList<>();

        Element wrapEle = (Element) wrapNode;

        @SuppressWarnings("unchecked")
        List<Node> pluginNodes = wrapEle.selectNodes("./plugin");

        for (Node pluginNode : pluginNodes) {
            List<WrapperEntity> entities = extractPlugin(entity, pluginNode);
            if (CollectionUtils.isNotEmpty(entities)) {
                results.addAll(entities);
            }
        }
        return results;
    }

    /**
     * Parse one level of crawler message, and get list of sub unvisited crawler messages.
     *
     * @param entity   parent crawler message
     * @param wrapNode plugin node
     * @return list of next level unvisited messages
     */
    public List<WrapperEntity> extractPlugin(WrapperEntity entity, Node wrapNode) {
        Element wrapEle = (Element) wrapNode;

        @SuppressWarnings("unchecked")
        List<Node> subPluginNodes = wrapEle.selectNodes("./plugin");

        List<WrapperEntity> entities = pluginAction(entity, wrapNode);

        if (CollectionUtils.isNotEmpty(subPluginNodes)) {
            List<WrapperEntity> results = new ArrayList<>();
            for (Node pluginNode : subPluginNodes) {
                List<WrapperEntity> temp = new ArrayList<>();
                for (WrapperEntity subEntity : entities) {
                    temp.addAll(extractPlugin(subEntity, pluginNode));
                }
                results.addAll(temp);
            }
            return results;
        } else {
            return entities;
        }
    }

    public List<WrapperEntity> pluginAction(WrapperEntity entity, Node wrapNode) {

        Element wrapEle = (Element) wrapNode;

        List<WrapperEntity> wrapEntities = new ArrayList<>();
        try {
            String pluginName = wrapEle.attributeValue(PLUGIN_CLASS);

            if (StringUtils.isNotBlank(pluginName)) {
                String pluginClass = ParseAction.PACKAGE + pluginName;
                // Dependency Injection
                Extract plugin = (Extract) Class.forName(pluginClass).newInstance();
                List<String> results = plugin.extract(entity, wrapNode);

                if (CollectionUtils.isNotEmpty(results)) {
                    for (String res : results) {
                        WrapperEntity wrapEntity;
                        if (null != entity.getHtmlDom()) {
                            wrapEntity = new WrapperEntity(entity.getUrl(), res, entity.getHtmlDom());
                            wrapEntities.add(wrapEntity);
                        } else if (null != entity.getJson()) {
                            wrapEntity = new WrapperEntity(entity.getUrl(), res, entity.getJson());
                            wrapEntities.add(wrapEntity);
                        }
                    }
                }
            } else {
                log.info("{} tag --> attribute 'class' is null", wrapEle.getName());
            }
        } catch (InstantiationException e) {
            log.error("error info {}", e);
        } catch (IllegalAccessException e) {
            log.error("error info {}", e);
        } catch (ClassNotFoundException e) {
            log.error("{} --> class name: {} can not find this class. error info {}",
                    wrapEle.getName(), wrapEle.attributeValue(PLUGIN_CLASS), e);
        }
        return wrapEntities;
    }

    protected Map mergeFields(Map fields, Map field, Element wrapEle) {

        String key;
        if (Dom4jUtil.isAttributeBlank(wrapEle, NODE_NAME)) {
            key = INFO_KEY;
        } else {
            key = wrapEle.attributeValue(NODE_NAME).trim();
        }

        if (null == fields.get(key)) {
            if (fields.get(key) instanceof List) {
                ((List) fields.get(key)).add(field);
            } else {
                fields.put(key, field);
            }
        } else if (fields.get(key) instanceof List) {
            List tmpList = (List) fields.get(key);

            if (tmpList.size() == 0) { // empty list
                tmpList.add(field);    // direct add
            } else {
                int lastIndex = tmpList.size() - 1;  // index of last element
                if (null == tmpList.get(lastIndex)) { // last element is null
                    tmpList.add(field); // direct add
                } else {
                    if (((Map) tmpList.get(lastIndex)).size() == 0) {
                        tmpList.set(lastIndex, field);
                    } else {
                        Iterator iterator = ((Map) tmpList.get(tmpList.size() - 1)).entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry entry = (Map.Entry) iterator.next();
                            field.put(entry.getKey(), entry.getValue());
                            tmpList.set(tmpList.size() - 1, field);
                        }
                    }
                }
            }
        } else if (fields.get(key) instanceof Map) {

            Iterator<String> keyIte = field.keySet().iterator();

            while (keyIte.hasNext()) {

                String fieldKey = keyIte.next();

                if (((Map) fields.get(key)).containsKey(fieldKey)) { // 首次一个key有多个值，需要转成list
                    Iterator ite = ((Map) fields.get(key)).entrySet().iterator(); // 重新赋值field
                    while (ite.hasNext()) {
                        Map.Entry entry = (Map.Entry) ite.next();
                        field.put(entry.getKey(), entry.getValue());
                    }
                    if (((Map) fields.get(key)).get(fieldKey) instanceof List) {
                        ((List) ((Map) fields.get(key)).get(fieldKey)).add(new HashMap());
                    } else if (((Map) fields.get(key)).get(fieldKey) instanceof String) {

                    }
                } else {
                    Iterator ite = ((Map) fields.get(key)).entrySet().iterator();  // 重新赋值field
                    while (ite.hasNext()) {
                        Map.Entry entry = (Map.Entry) ite.next();
                        field.put(entry.getKey(), entry.getValue());
                        fields.put(key, field);
                    }
                }
            }
        }
        return fields;
    }

    /**
     * create a map, and the key of map is the value of element attribute 'name'
     *
     * @param ele element
     * @return HashMap object if the node has attribute 'name', else null
     */
    protected Map newField(Element ele, boolean isArray) {

        if (Dom4jUtil.isAttributeNotBlank(ele, NODE_NAME)) {
            String key = ele.attributeValue(NODE_NAME).trim();

            if (isArray) {
                Map<String, List> field = new HashMap<>();
                field.put(key, new ArrayList<>());
                return field;
            } else {
                Map<String, String> field = new HashMap<>();
                field.put(key, "");
                return field;
            }
        } else {
            return null;
        }
    }
}