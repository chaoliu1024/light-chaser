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

package me.chaoliu.lightchaser.core.persistence.mysql;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.chaoliu.lightchaser.core.fission.IFission;
import me.chaoliu.lightchaser.core.fission.ota.OTAFission;
import me.chaoliu.lightchaser.core.fission.webpage.WebPage;
import me.chaoliu.lightchaser.core.persistence.PersistenceHandler;
import me.chaoliu.lightchaser.core.wrapper.template.Wrapper;

import java.util.Map;


@Slf4j
public class MysqlPersistence implements PersistenceHandler {

    @Override
    public void saveProduct(Map info) {

        String map2String = JSON.toJSONString(info);
        JSONObject String2Json = JSONObject.parseObject(map2String);

        log.info("Extract result: {}", String2Json);

        String type = String2Json.get(Wrapper.JOB_TYPE_KEY).toString();
        JSONObject data = (JSONObject) String2Json.get(Wrapper.INFO_KEY);

        if (type.equals("ota")) {
            IFission service = new OTAFission();
            service.fission(data);
        }
    }

    @Override
    public void saveWebPage(WebPage page) {

    }
}
