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

package me.chaoliu.lightchaser.core.config;

import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * test LoadConfig class function
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class LoadConfigTest {

    private static final String DEFAULT_YAML_NAME = "light-chaser.yaml";

    Map config = LoadConfig.findAndReadConfigFile(DEFAULT_YAML_NAME);

    @Test
    public void testFindAndReadConfigFile() {
//        System.out.println(config);
//        System.out.println(config.get(Constants.TEMPLATE_PATH));
        Map proxyList = (Map) config.get("http.proxy");
        System.out.println(proxyList);
        System.out.println(proxyList.get("host"));
        System.out.println(proxyList.get("port.194"));
        System.out.println(proxyList.get("port.248"));
    }

    @Test
    public void testProxyWeb() {

        String proxyYaml = "proxy.yaml";
        Map config = LoadConfig.readConfig(proxyYaml);
        Map<String, List> proxyWebs = (Map) config.get("proxy.web");

        for (Map.Entry<String, List> entry : proxyWebs.entrySet()) {
            System.out.println(entry.getKey());
            for (Object detail : entry.getValue()) {
                Map<String, String> detailMap = (Map) detail;
                for (Map.Entry<String, String> d : detailMap.entrySet()) {
                    System.out.println(d.getKey() + ":\t" + d.getValue());
                }
            }
        }
    }
}
