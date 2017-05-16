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

package me.chaoliu.lightchaser.core.daemon;

import lombok.Getter;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public enum JobType {

    // 代理
    PROXY("proxy"),

    // 在线旅游 Online Travel Agency
    OTA("ota"),

    // 在线电影票 Online Cinema Tickets
    OCT("oct");

    @Getter
    private String typeName;

    private JobType(String typeName) {
        this.typeName = typeName;
    }

    public static JobType parseType(String type) {
        for (JobType jobType : JobType.values()) {
            if (jobType.getTypeName().equals(type)) {
                return jobType;
            }
        }
        return null;
    }
}
