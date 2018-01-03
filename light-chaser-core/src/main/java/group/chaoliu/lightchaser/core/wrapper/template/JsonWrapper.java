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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import group.chaoliu.lightchaser.core.util.Dom4jUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;
import org.w3c.dom.DOMException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Json wrapper, which used to extract json data.
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class JsonWrapper extends Wrapper {

    public JsonWrapper(Element rootEle) {
        super(rootEle);
    }

    private Map result = new HashMap();

    @Override
    public Map extract(WrapperEntity wrapperEntity) {

        JSON json = wrapperEntity.getJson();

        result.put(DATA_KEY, null);

        try {
            extractFields(super.wrapperEle, json, result, wrapperEntity);
        } catch (Exception e) {
            log.error("解析URL {}, 内容 {} 错误 {}", wrapperEntity.getUrl(),
                    wrapperEntity.getText(), e);
        }

        return result;
    }

    /**
     * @param wrapEle wrapper xml node
     * @param jsonObj json object
     * @param fields  result fields
     * @param entity  wrapper entity
     */
    public void extractFields(Element wrapEle, Object jsonObj,
                              Map<String, String> fields, WrapperEntity entity) {

        if (jsonObj instanceof String || jsonObj instanceof BigDecimal || jsonObj instanceof Integer) {
            if (Dom4jUtil.isAttributeNotBlank(wrapEle, NODE_NAME)) {
                fields.put(wrapEle.attributeValue(NODE_NAME).trim(), jsonObj.toString().trim());
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
            if (null != wrapEle.attribute(NODE_KEY)) {
                try {
                    if (Dom4jUtil.isAttributeNotBlank(wrapEle, NODE_NAME)) {
                        if (null != json) {
                            fields.put(wrapEle.attributeValue(NODE_NAME).trim(), json.toJSONString());
                        }
                    }
                } catch (DOMException e) {
                    log.error("extract json key {} fail. error info: {}", wrapEle.attributeValue(NODE_NAME).trim(), e);
                }
            }
        } else { // recurse extract plugin node
            List<WrapperEntity> res = extractFields(entity, wrapEle);
            for (WrapperEntity r : res) {
                if (StringUtils.isNotBlank(r.getText())) {
                    fields.put(wrapEle.attributeValue(NODE_NAME).trim(), r.getText().trim());
                }
            }
        }

        // extract sub field node
        for (Node fieldNode : fieldNodes) {

            Element fieldEle = (Element) fieldNode;
            String keys = fieldEle.attributeValue(NODE_KEY);

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
                    if (null != jsonO) {
                        jsonE = jsonO.get(keys);
                    }
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
                        extractFields(fieldEle, array, field, entity);
                    }
                }
            } else {
                Map<String, String> field = newField(fieldEle, false);
                if (null != field) {
                    mergeFields(fields, field, wrapEle);
                }
                extractFields(fieldEle, jsonE, field, entity);
            }
        }
        entity.setJson(entityJSON);
    }
}
