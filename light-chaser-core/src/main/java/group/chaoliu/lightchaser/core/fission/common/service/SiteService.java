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
package group.chaoliu.lightchaser.core.fission.common.service;

import group.chaoliu.lightchaser.core.daemon.Job;
import group.chaoliu.lightchaser.core.fission.common.domain.JobTypePO;
import group.chaoliu.lightchaser.core.fission.common.domain.SitePO;
import group.chaoliu.lightchaser.core.fission.common.mapper.SiteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * web site service
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Service
public class SiteService {

    @Autowired
    private SiteMapper siteMapper;

    @Autowired
    private JobTypeService jobTypeService;

    /**
     * 记录抓取站点
     */
    public void siteLog(Job job) {

        String type = job.getType();
        JobTypePO jobTypePO = new JobTypePO();
        jobTypePO.setJobName(type);

        int jobTypeId = jobTypeService.insertJobType(jobTypePO);

        String domainKey = job.getName();
        SitePO site = new SitePO();
        site.setDomainKey(domainKey);
        site.setJobType(jobTypeId);

        if (!isSiteExist(site)) {
            siteMapper.insertSite(site);
        }
    }

    public boolean isSiteExist(SitePO site) {
        return (null != siteMapper.fetchSite(site));
    }

}