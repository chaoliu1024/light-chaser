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

package group.chaoliu.lightchaser.core.crawl.template;


import group.chaoliu.lightchaser.common.Category;

import java.util.HashMap;
import java.util.Map;

/**
 * cache all the template which is running crawl job.
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class TemplateCache {

    private Map<Category, CategoryTemplate> templatesCache = new HashMap<>();

    /**
     * get all site templates
     * TODO 需要多线程安全？
     *
     * @return site templates
     */
    public Map<Category, CategoryTemplate> getTemplates() {
        return this.templatesCache;
    }

    /**
     * put one site template into template cache object
     * TODO 需要多线程安全？
     *
     * @param category job
     * @param template template
     */
    public void putCategoryTemplate(Category category, CategoryTemplate template) {
        templatesCache.put(category, template);
    }
}
