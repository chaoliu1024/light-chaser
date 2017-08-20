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

import group.chaoliu.lightchaser.core.fission.proxy.mapper.ProxyMapper;
import group.chaoliu.lightchaser.core.protocol.http.Proxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class Radiator {

    public static final ApplicationContext CONTEXT = new ClassPathXmlApplicationContext("applicationContext.xml");

    private ProxyMapper proxyMapper = CONTEXT.getBean(ProxyMapper.class);

    private List<Proxy> cacheProxy;

    private ScheduledExecutorService proxyScheduledThreadPool = Executors.newScheduledThreadPool(1);

    public Radiator() {
        refreshProxies();
    }

    public void refreshProxies() {
        proxyScheduledThreadPool.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                cacheProxy = proxyMapper.fetchCommonUsableProxies();
            }
        }, 0xa, 10, TimeUnit.MINUTES);
    }

    public void shutdown() {
        if (!proxyScheduledThreadPool.isShutdown()) {
            proxyScheduledThreadPool.shutdown();
        }
    }

    public Proxy randomProxy() {
        int size = cacheProxy.size();
        Random r = new Random();
        int i = r.nextInt(size) % (size - 1);
        return cacheProxy.get(i);
    }

    public static void main(String[] args) {
        Radiator radiator = new Radiator();
        radiator.refreshProxies();
        for (int i = 0; i < 20; i++) {
            System.out.println(radiator.randomProxy());
        }
    }
}
