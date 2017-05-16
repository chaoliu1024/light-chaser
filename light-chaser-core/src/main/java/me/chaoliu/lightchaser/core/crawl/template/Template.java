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

package me.chaoliu.lightchaser.core.crawl.template;

import lombok.Data;

/**
 * this template object contains crawl template and wrapper template
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Data
public class Template {

    private String siteName;
    private CrawlTemplate crawlTemplate;
    private WrapperTemplate wrapperTemplate;

    public Template(String siteName, CrawlTemplate crawlTemplate, WrapperTemplate wrapperTemplate) {
        this.siteName = siteName;
        this.crawlTemplate = crawlTemplate;
        this.wrapperTemplate = wrapperTemplate;
    }

    @Override
    public String toString() {
        return "Template{siteName='" + siteName + "\', crawlTemplate=" + crawlTemplate +
                ", wrapperTemplate=" + wrapperTemplate + "}";
    }
}
