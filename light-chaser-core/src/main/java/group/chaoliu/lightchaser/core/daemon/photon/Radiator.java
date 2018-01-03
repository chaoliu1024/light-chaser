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

package group.chaoliu.lightchaser.core.daemon.photon;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import group.chaoliu.lightchaser.common.config.YamlConfig;
import group.chaoliu.lightchaser.common.protocol.http.Proxy;
import group.chaoliu.lightchaser.core.daemon.photon.thread.ValidateProxyThread;
import group.chaoliu.lightchaser.core.fission.proxy.ProxyService;
import group.chaoliu.lightchaser.core.util.SpringBeanUtil;
import group.chaoliu.lightchaser.rpc.netty.proxy.ProxyCache;
import group.chaoliu.lightchaser.rpc.netty.proxy.ProxyServerSocket;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 代理服务
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class Radiator {

    private ProxyService proxyService = SpringBeanUtil.proxyServiceBean();

    private ScheduledExecutorService proxyScheduledThreadPool = Executors.newScheduledThreadPool(1);

    private ProxyCache proxiesCache = ProxyCache.proxyCacheInstance();

    public Radiator() {
        List<Proxy> cache = proxyService.fetchCtripProxies();
        proxiesCache.setProxies(cache);
        refreshProxies();
    }

    public void refreshProxies() {
        proxyScheduledThreadPool.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                log.info("刷新代理缓存...");
                proxiesCache.setProxies(proxyService.fetchCtripProxies());
            }
        }, 0xa, 10, TimeUnit.MINUTES);
    }

    public void shutdown() {
        if (!proxyScheduledThreadPool.isShutdown()) {
            proxyScheduledThreadPool.shutdown();
        }
    }

    public void run(Map proxyConfig) {
        refreshProxies();

        ThreadFactory thread = new ThreadFactoryBuilder().setNameFormat("proxy-server-socket-thread").build();
        ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), thread, new ThreadPoolExecutor.AbortPolicy());
        singleThreadPool.execute(new ProxyServerSocket(proxyConfig));
    }

    public static void main(String[] args) {
        Map proxyConfig = YamlConfig.readProxyConfig();
        Radiator radiator = new Radiator();
        radiator.run(proxyConfig);
    }

}