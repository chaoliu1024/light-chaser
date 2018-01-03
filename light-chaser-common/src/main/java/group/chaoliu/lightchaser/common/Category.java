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

package group.chaoliu.lightchaser.common;

import group.chaoliu.lightchaser.common.util.DateTimeUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class Category implements Serializable {

    /**
     * 对应于Fission的一个解析类。
     */
    @Setter
    @Getter
    private String type;

    @Setter
    @Getter
    private String name;

    @Getter
    private Date date;

    @Getter
    private String suffix;

    public String key() {
        return type + "_" + name;
    }

    public Category(String type, String name) {
        this.type = type;
        this.name = name;
        this.date = new Date();
        if (StringUtils.isNotBlank(this.type)) {
//            this.suffix = this.type + "_" + DateTimeUtil.date2String(this.date, "yyyyMMdd");
            this.suffix = DateTimeUtil.date2String(this.date, "yyyyMMdd");
        }
    }

    @Override
    public String toString() {
        return "Category (name: " + name + ", suffix: " + suffix + ")";
    }

}