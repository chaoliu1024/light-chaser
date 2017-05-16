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

package me.chaoliu.lightchaser.core.fission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("base" + BaseFission.FISSION_BEAN_SUFFIX)
public class BaseFission implements IFission {

    public static final String FISSION_BEAN_SUFFIX = "Fission";

    private Map<String, BaseFission> fissionMap = new ConcurrentHashMap<>();

    @Resource
    private List<BaseFission> fissionServices;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void fission(Map data) {
    }

    private BaseFission getFission(String fission) {

        if (null != fissionMap.get(fission)) {
            return fissionMap.get(fission);
        }

        if (applicationContext.containsBean(fission) && applicationContext.getBean(fission) instanceof BaseFission) {
            return (BaseFission) applicationContext.getBean(fission);
        }
//        for (BaseFission fissionService : fissionServices) {
//
//            fissionService
//
//        }

        return null;
    }

    @Override
    public void waitFissionFinish() {
    }
}
