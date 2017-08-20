/*
 * Copyright (c) 2017, Chao Liu (chaoliu1024@gmail.com). All rights reserved.
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

import group.chaoliu.lightchaser.core.crawl.Crawler;
import group.chaoliu.lightchaser.core.fission.BaseFission;
import group.chaoliu.lightchaser.core.fission.common.service.SiteService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringBeanUtil {


    public static final ApplicationContext CONTEXT = new ClassPathXmlApplicationContext("applicationContext.xml");

    /**
     * get fission bean
     *
     * @param beanName fission bean name
     * @return fission bean
     */
    public static BaseFission fissionBean(String beanName) {
        return (BaseFission) CONTEXT.getBean(beanName);
    }

    /**
     * get crawler bean
     *
     * @param beanName crawler bean name
     * @return crawler bean
     */
    public static Crawler crawlerBean(String beanName) {
        return (Crawler) CONTEXT.getBean(beanName);
    }

    /**
     * get crawler bean
     *
     * @return crawler bean
     */
    public static Crawler crawlerBean() {
        return CONTEXT.getBean(Crawler.class);
    }

    public static SiteService siteServiceBean() {
        return CONTEXT.getBean(SiteService.class);
    }
}
