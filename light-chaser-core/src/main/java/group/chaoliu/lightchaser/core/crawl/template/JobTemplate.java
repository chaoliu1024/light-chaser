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

import group.chaoliu.lightchaser.core.daemon.Job;
import lombok.Getter;
import lombok.Setter;

/**
 * this template object contains crawl template and wrapper template
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Setter
@Getter
public class JobTemplate {

    private Job job;
    private CrawlTemplate crawlTemplate;
    private WrapperTemplate wrapperTemplate;

    public JobTemplate(Job job, CrawlTemplate crawlTemplate, WrapperTemplate wrapperTemplate) {
        this.job = job;
        this.crawlTemplate = crawlTemplate;
        this.wrapperTemplate = wrapperTemplate;
    }

    @Override
    public String toString() {
        return "JobTemplate{job='" + job + "\', crawlTemplate=" + crawlTemplate +
                ", wrapperTemplate=" + wrapperTemplate + "}";
    }
}
