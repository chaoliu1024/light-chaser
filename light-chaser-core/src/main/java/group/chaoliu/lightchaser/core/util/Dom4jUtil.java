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

package group.chaoliu.lightchaser.core.util;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

/**
 * dom4j utils
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class Dom4jUtil {

    /**
     * <p>Checks if an element attribute is empty (""), or null.</p>
     *
     * @param element       element
     * @param attributeName attribute name
     * @return <code>true</code> if the element attribute is null, empty or whitespace
     */
    public static boolean isAttributeBlank(Element element, String attributeName) {
        return (null == element.attribute(attributeName) || StringUtils.isBlank(element.attributeValue(attributeName)));
    }

    /**
     * <p>Checks if an element attribute is not empty (""), not null and not whitespace only.</p>
     *
     * @param element       element
     * @param attributeName attribute name
     * @return <code>true</code> if the element attribute is not empty and not null and not whitespace
     */
    public static boolean isAttributeNotBlank(Element element, String attributeName) {
        return !isAttributeBlank(element, attributeName);
    }
}
