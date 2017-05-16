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


package me.chaoliu.lightchaser.core.daemon;

import me.chaoliu.lightchaser.core.config.LoadConfig;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class StarTest {

    @Test
    public void test_Ctrip_CommonTypeStar() {
        String jobName = "ctrip";
        testCommonTypeStar(jobName);
    }

    @Test
    public void test_Gewara_CommonTypeStar() {
        String jobName = "gewara";
        testCommonTypeStar(jobName);
    }

    @Test
    public void test_Archive_CommonTypeStar() {
        String jobName = "archive";
        testCommonTypeStar(jobName);
    }

    @Test
    public void test_66IP_ProxyTypeStar() {
        String jobName = "66ip";
        testProxyStar(jobName);
    }

    @Test
    public void test_CoolProxy_ProxyTypeStar() {
        String jobName = "cool-proxy";
        testProxyStar(jobName);
    }

    @Test
    public void test_IP3366_ProxyTypeStar() {
        String jobName = "ip3366";
        testProxyStar(jobName);
    }

    @Test
    public void test_KuaiDaiLi_ProxyTypeStar() {
        String jobName = "kuaidaili";
        testProxyStar(jobName);
    }

    @Test
    public void test_MimiIp_ProxyTypeStar() {
        String jobName = "mimiip";
        testProxyStar(jobName);
    }

    @Test
    public void test_NianShao_ProxyTypeStar() {
        String jobName = "nianshao";
        testProxyStar(jobName);
    }

    @Test
    public void test_XiciDaiLi_ProxyTypeStar() {
        String jobName = "xicidaili";
        testProxyStar(jobName);
    }

    @Test
    public void test_Xroxy_ProxyTypeStar() {
        String jobName = "xroxy";
        testProxyStar(jobName);
    }

    @Test
    public void test_XsDaiLi_ProxyTypeStar() {
        String jobName = "xsdaili";
        testProxyStar(jobName);
    }

    @Test
    public void test_YouDaiLi_ProxyTypeStar() {
        String jobName = "youdaili";
        testProxyStar(jobName);
    }

    @Test
    public void startAllProxy() {
        JobType jobType = JobType.PROXY;
        Map config = LoadConfig.readLightChaserConfig();
        Star star = new Star();
        Star.initTemplateRootPath(config, jobType);
        Star.initProxyWeb(config);
        List<String> proxies = Star.initProxyJob();
        for (String proxy : proxies) {
            Job job = new Job(jobType, proxy);
            star.radiate(job);
        }
    }

    private void testCommonTypeStar(String jobName) {
        JobType starType = JobType.OTA;
        start(starType, jobName);
    }

    private void testProxyStar(String jobName) {
        JobType starType = JobType.PROXY;
        start(starType, jobName);
    }

    private void start(JobType starType, String jobName) {
        Map config = LoadConfig.readLightChaserConfig();
        Star star = new Star();
        Star.initTemplateRootPath(config, starType);
        Star.initProxyWeb(config);

        Job job = new Job(starType, jobName);
        star.initJob(job);
    }


    @Test
    public void testInitProxyWeb() {
        Map config = LoadConfig.readLightChaserConfig();
        Star.initProxyWeb(config);
    }
}
