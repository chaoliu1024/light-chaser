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


import lombok.Data;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Data
public class Job {

    /**
     * job type 对应不同的抓取品类, 一个品类可能需要抓取多个网站。
     * 但抓取最终的数据格式是一致的。对应于Fission的一个解析类。
     */
    private JobType jobType;

    /**
     * job name 对应不同的抓取模板
     */
    private String jobName;

    public Job(JobType jobType, String jobName) {
        this.jobType = jobType;
        this.jobName = jobName;
    }
}
