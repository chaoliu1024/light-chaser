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

import com.sun.org.apache.xerces.internal.dom.TextImpl;
import group.chaoliu.lightchaser.core.crawl.HtmlDom;
import group.chaoliu.lightchaser.core.parser.util.HtmlXPath;
import group.chaoliu.lightchaser.core.util.Dom4jUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTML wrapper, which is used to extract fields from html page.
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class HtmlWrapper extends Wrapper {

    public HtmlWrapper(Node node) {
        super(node);
    }

    private Map result = new HashMap();

    @Override
    public Map extract(WrapperEntity wrapperEntity) {

        HtmlDom htmlDom = wrapperEntity.getHtmlDom();

        org.w3c.dom.Node htmlNode = htmlDom.getHtmlDoc().getFirstChild();

        result.put(DATA_KEY, null);

        extractFields(super.wrapperNode, htmlNode, result, wrapperEntity);

        return result;
    }

    /**
     * @param wrapNode wrapper xml node
     * @param html     html node
     * @param fields   result fields
     * @param entity   wrapper entity
     */
    public void extractFields(Node wrapNode, Object html,
                              Map<String, String> fields, WrapperEntity entity) {

        Element wrapEle = (Element) wrapNode;

        if (html instanceof String) {
            if (Dom4jUtil.isAttributeNotBlank(wrapEle, Wrapper.NODE_NAME)) {
                fields.put(wrapEle.attributeValue(Wrapper.NODE_NAME).trim(), html.toString().trim());
            }
            return;
        }

        org.w3c.dom.Node htmlNode = (org.w3c.dom.Node) html;

        String text = entity.getText();

        List<Node> fieldNodes = wrapEle.selectNodes("./node");

        // item nodes and plugin nodes are all empty
        if (CollectionUtils.isEmpty(fieldNodes)
                && CollectionUtils.isEmpty(wrapEle.selectNodes("./plugin"))) {
            // leaf node of wrap xml
            if (null != wrapEle.attribute(Wrapper.NODE_XPATH)) {
                try {
                    if (Dom4jUtil.isAttributeNotBlank(wrapEle, Wrapper.NODE_NAME)) {
                        fields.put(wrapEle.attributeValue(Wrapper.NODE_NAME).trim(), htmlNode.getTextContent().trim());
                    }
                } catch (DOMException e) {
                    log.error("using w3c dom parse html error {}", e);
                }
            }
        } else {
            // recurse extract plugin node
            List<WrapperEntity> res = extractFields(entity, wrapNode);

            if (res.size() == 1) {
                WrapperEntity r = res.get(0);
                if (StringUtils.isNotBlank(r.getText())) {
                    fields.put(wrapEle.attributeValue(Wrapper.NODE_NAME).trim(), r.getText().trim());
                }
            } else if (res.size() > 1) {

                // 除非使用xpath解析，才会有这种情况
                // replace, thisurl不会有这种情况。
                log.error("!!!!!!!!!!!!----插件plugin抽取结果大于1----!!!!!!!!!!!!");
                List<String> resValue = new ArrayList<>();
                for (WrapperEntity r : res) {
                    if (StringUtils.isNotBlank(r.getText())) {
                        resValue.add(r.getText().trim());
                    }
                }
                fields.put(wrapEle.attributeValue(Wrapper.NODE_NAME).trim(), resValue.toString());
            }
        }

        // extract sub field node
        for (Node fieldNode : fieldNodes) {

            Element fieldEle = (Element) fieldNode;
            String xpathValue = fieldEle.attributeValue(Wrapper.NODE_XPATH);

            if (StringUtils.isBlank(xpathValue)) {
                xpathValue = ".";
            }
            HtmlXPath xpath = new HtmlXPath();

            Object result;

            // 取当前节点
            if (".".equals(xpathValue) && htmlNode instanceof TextImpl) {
                result = xpath.parse(htmlNode, xpathValue, entity.getHtmlDom().getXpath());
            } else if (StringUtils.isNotBlank(text)) {
                result = xpath.parse(text, xpathValue, entity.getHtmlDom().getXpath());
            } else {
                result = xpath.parse(htmlNode, xpathValue, entity.getHtmlDom().getXpath());
            }

            if (result instanceof NodeList) {

                NodeList nodeList = (NodeList) result;
                for (int i = 0; i < nodeList.getLength(); i++) {
                    org.w3c.dom.Node pageNode = nodeList.item(i);

                    entity.setText(pageNode.getNodeValue());  // xpath="." text=null
                    Map<String, String> field;
                    if ("list".equals(fieldEle.attributeValue(NODE_TYPE))) {
                        field = newField(fieldEle, true);
                    } else {
                        field = newField(fieldEle, false);
                    }
                    if (null != field) {
                        mergeFields(fields, field, wrapEle);
                        extractFields(fieldNode, pageNode, field, entity);
                    }
                }
            } else if (result instanceof String) {
                entity.setText(result.toString());
                Map<String, String> field = newField(fieldEle, false);
                if (null != field) {
                    mergeFields(fields, field, wrapEle);
                    extractFields(fieldNode, result, field, entity);
                }
            }
        }
    }
}