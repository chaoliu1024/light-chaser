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

package group.chaoliu.lightchaser.core.fission.proxy.mapper;

import group.chaoliu.lightchaser.common.protocol.http.Proxy;
import group.chaoliu.lightchaser.core.fission.proxy.domain.ProxyPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Proxy interface
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Repository
public interface ProxyMapper {

    void insertProxy(ProxyPO proxy);

    void insertBatchProxies(@Param("proxies") List<ProxyPO> proxies);

    /**
     * Fetch proxies
     *
     * @param proxy proxyPO
     * @return proxyPO list
     */
    List<Proxy> fetchProxies(ProxyPO proxy);

    /**
     * Delete ineffective proxies.
     *
     * @param failedCount failed count
     */
    void deleteIneffectiveProxies(int failedCount);
}
