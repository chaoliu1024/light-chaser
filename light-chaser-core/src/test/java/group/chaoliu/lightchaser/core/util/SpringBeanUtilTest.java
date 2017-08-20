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
import group.chaoliu.lightchaser.core.fission.proxy.ProxyFission;
import org.junit.Test;

public class SpringBeanUtilTest {

    @Test
    public void testBean() {
        Crawler c1 = SpringBeanUtil.crawlerBean();
        Crawler c2 = SpringBeanUtil.crawlerBean();
        System.out.println(c1.hashCode());
        System.out.println(c2.hashCode());

        ProxyFission p1 = (ProxyFission) SpringBeanUtil.fissionBean("proxyFission");
        ProxyFission p2 = (ProxyFission) SpringBeanUtil.fissionBean("proxyFission");
        System.out.println(p1.hashCode());
        System.out.println(p2.hashCode());
    }
}
