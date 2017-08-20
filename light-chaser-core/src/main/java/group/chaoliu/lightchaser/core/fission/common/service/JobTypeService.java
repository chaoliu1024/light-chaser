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

import group.chaoliu.lightchaser.core.fission.common.domain.JobTypePO;
import group.chaoliu.lightchaser.core.fission.common.mapper.JobTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Service
public class JobTypeService {

    @Autowired
    private JobTypeMapper jobTypeMapper;

    public int insertJobType(JobTypePO jobTypePO) {

        JobTypePO jobType = jobTypeMapper.fetchJobType(jobTypePO);
        if (null != jobType) {
            return jobType.getId();
        } else {
            jobTypeMapper.insertJobType(jobTypePO);
            return jobTypeMapper.fetchJobType(jobTypePO).getId();
        }
    }

    public boolean isExistJobType(JobTypePO jobTypePO) {
        return (null != jobTypeMapper.fetchJobType(jobTypePO));
    }
}
