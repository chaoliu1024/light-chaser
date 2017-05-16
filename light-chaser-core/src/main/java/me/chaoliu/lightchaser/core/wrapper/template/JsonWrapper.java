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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.chaoliu.lightchaser.core.util.Dom4jUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;
import org.w3c.dom.DOMException;

import java.math.BigDecimal;
import java.util.*;

/**
 * Json wrapper, which used to extract json data.
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class JsonWrapper extends Wrapper {

    public JsonWrapper(Node node) {
        super(node);
    }

    private Map result = new HashMap();

    @Override
    public Map extract(WrapperEntity wrapperEntity) {

        JSON json = wrapperEntity.getJson();

        result.put(INFO_KEY, null);

        extractFields(super.wrapperNode, json, result, wrapperEntity);

        return result;
    }

    /**
     * @param wrapNode wrapper xml node
     * @param jsonObj  json object
     * @param fields   result fields
     * @param entity   wrapper entity
     */
    public void extractFields(Node wrapNode, Object jsonObj,
                              Map<String, String> fields, WrapperEntity entity) {

        Element wrapEle = (Element) wrapNode;

        if (jsonObj instanceof String || jsonObj instanceof BigDecimal || jsonObj instanceof Integer) {
            if (Dom4jUtil.isAttributeNotBlank(wrapEle, Wrapper.NODE_NAME)) {
                fields.put(wrapEle.attributeValue(Wrapper.NODE_NAME).trim(), jsonObj.toString().trim());
            }
            return;
        }

        JSON json = (JSON) jsonObj;
        JSON entityJSON = entity.getJson();

        entity.setJson(json);

        List<Node> fieldNodes = wrapEle.selectNodes("./node");

        // item nodes and plugin nodes are all empty
        if (CollectionUtils.isEmpty(fieldNodes)
                && CollectionUtils.isEmpty(wrapEle.selectNodes("./plugin"))) {
            // leaf node of wrap xml
            if (null != wrapEle.attribute(Wrapper.NODE_KEY)) {
                try {
                    if (Dom4jUtil.isAttributeNotBlank(wrapEle, Wrapper.NODE_NAME)) {
                        fields.put(wrapEle.attributeValue(Wrapper.NODE_NAME).trim(), json.toJSONString());
                    }
                } catch (DOMException e) {
                    log.error("extract json key {} fail. error info: {}", wrapEle.attributeValue(Wrapper.NODE_NAME).trim(), e);
                }
            }
        } else { // recurse extract plugin node
            List<WrapperEntity> res = extractFields(entity, wrapNode);
            for (WrapperEntity r : res) {
                if (StringUtils.isNotBlank(r.getText())) {
                    fields.put(wrapEle.attributeValue(Wrapper.NODE_NAME).trim(), r.getText().trim());
                }
            }
        }

        // extract sub field node
        for (Node fieldNode : fieldNodes) {

            Element fieldEle = (Element) fieldNode;
            String keys = fieldEle.attributeValue(Wrapper.NODE_KEY);

            Object jsonE = entity.getJson();

            if (StringUtils.isNotBlank(keys)) {
                if (keys.contains(".")) {
                    String[] keyArray = keys.split("\\.");
                    for (String key : keyArray) {
                        if (jsonE instanceof JSONObject) {
                            JSONObject jsonO = (JSONObject) jsonE;
                            jsonE = jsonO.get(key);
                        }
                    }
                } else {
                    JSONObject jsonO = (JSONObject) jsonE;
                    jsonE = jsonO.get(keys);
                }
            }

            if (jsonE instanceof JSONArray) {
                for (Object array : (JSONArray) jsonE) {
                    JSONArray jsonA = (JSONArray) jsonE;
                    Map<String, String> field;
                    if (jsonA.size() == 1) {
                        field = newField(fieldEle, false);
                    } else {
                        field = newField(fieldEle, true);
                    }
                    if (null != field) {
                        mergeFields(fields, field, wrapEle);
                        extractFields(fieldNode, array, field, entity);
                    }
                }
            } else {
                Map<String, String> field = newField(fieldEle, false);
                if (null != field) {
                    mergeFields(fields, field, wrapEle);
                }
                extractFields(fieldNode, jsonE, field, entity);
            }
        }
        entity.setJson(entityJSON);
    }
}
