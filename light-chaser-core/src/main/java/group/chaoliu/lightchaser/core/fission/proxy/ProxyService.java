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

package group.chaoliu.lightchaser.core.fission.proxy;

import group.chaoliu.lightchaser.core.fission.proxy.domain.ProxyPO;
import group.chaoliu.lightchaser.core.fission.proxy.mapper.ProxyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * proxy server
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
@Service
public class ProxyService {

    @Autowired
    private ProxyMapper proxyMapper;

    public void insertProxy(ProxyPO proxy) {
        proxyMapper.insertProxy(proxy);
    }

    public void batchInsertProxy(List<ProxyPO> proxies) {
        log.info("batch insert proxies...");
        proxyMapper.insertBatchProxies(proxies);
    }
}
