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

package group.chaoliu.lightchaser.rpc.netty.proxy;

import group.chaoliu.lightchaser.common.protocol.http.Proxy;
import org.junit.Test;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class ProxyManagerTest {

    @Test
    public void testUpdateProxyStatus() {

        ProxyManager proxyManager = ProxyManager.proxyManagerInstance();

        ProxyManager.ProxyEntity proxyEntity1 = proxyManager.new ProxyEntity();
        ProxyManager.ProxyEntity proxyEntity2 = proxyManager.new ProxyEntity();
        ProxyManager.ProxyEntity proxyEntity3 = proxyManager.new ProxyEntity();
        ProxyManager.ProxyEntity proxyEntity4 = proxyManager.new ProxyEntity();

        Proxy proxy1 = new Proxy("127.0.1.2", 100, "http");
        Proxy proxy2 = new Proxy("127.0.1.3", 100, "http");
        Proxy proxy3 = new Proxy("127.0.1.2", 101, "http");

        proxyEntity1.setProxy(proxy1);
        proxyEntity2.setProxy(proxy2);
        proxyEntity3.setProxy(proxy3);

        ProxyManager.getProxyEntities().add(proxyEntity1);
        ProxyManager.getProxyEntities().add(proxyEntity2);
        ProxyManager.getProxyEntities().add(proxyEntity3);

        Proxy proxy = new Proxy("127.0.1.2", 100, "http");

        proxyManager.updateProxyStatus(proxy, ProxyCode.OK, "ctrip");

    }
}
