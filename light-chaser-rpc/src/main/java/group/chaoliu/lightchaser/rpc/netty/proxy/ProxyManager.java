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
import group.chaoliu.lightchaser.rpc.netty.protobuf.ProxyProto;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 代理管理
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class ProxyManager {

    private static ProxyManager proxyManager = new ProxyManager();

    @Getter
    @Setter
    private static List<ProxyEntity> proxyEntities = Collections.synchronizedList(new ArrayList<>());

    private ProxyManager() {
    }

    public static ProxyManager proxyManagerInstance() {
        return proxyManager;
    }

    /**
     * 初始化 proxy entities
     *
     * @param domainKeys domain keys
     */
    public void setProxyEntities(List<Proxy> proxies, List<String> domainKeys) {

        if (domainKeys.isEmpty()) {
            return;
        }
        // 需要从代理池中删除的代理...
        List<ProxyEntity> needRemove = new ArrayList<>();
        for (ProxyEntity e : proxyEntities) {
            Map<String, ProxyStatus> proxyStatus = e.getProxyStatus();
            for (String key : domainKeys) {
                ProxyStatus status = proxyStatus.get(key);
                // 大于10秒移除
                if (status.getCurInterval() > 10) {
                    needRemove.add(e);
                    break;
                }
            }
        }
        proxyEntities.removeAll(needRemove);

        List<Proxy> needAdd = new ArrayList<>();
        for (Proxy p : proxies) {
            boolean contain = false;
            for (ProxyEntity e : proxyEntities) {
                if (p.equals(e.getProxy())) {
                    contain = true;
                    break;
                }
            }
            if (!contain) {
                needAdd.add(p);
            }
        }

        for (Proxy p : needAdd) {
            ProxyEntity proxyEntity = new ProxyEntity();

            Map<String, ProxyStatus> proxyStatus = proxyEntity.getProxyStatus();
            for (String key : domainKeys) {
                if (!proxyStatus.containsKey(key)) {
                    proxyStatus.put(key, new ProxyStatus());
                }
            }

            proxyEntity.setProxy(p);
            proxyEntity.setProxyStatus(proxyStatus);

            proxyEntities.add(proxyEntity);
        }
    }

    /**
     * 随机得到一个代理IP
     *
     * @return proxy
     */
    public Proxy randomProxy(String domainKey) {
        log.info("随机代理... {}", domainKey);
        int size = proxyEntities.size();
        Random r = new Random();
        int i = r.nextInt(size) % (size - 1);
        ProxyEntity proxyEntity = proxyEntities.get(i);

        ProxyStatus proxyStatus = new ProxyStatus();
        proxyStatus.setLastTime(new Date());
        proxyEntity.getProxyStatus().put(domainKey, proxyStatus);
        return proxyEntity.getProxy();
    }

    /**
     * 得到一个比较优秀的Proxy,但不是最优Proxy
     * TODO 最优代理需要完整遍历整个 proxyEntities链表找出curInterval最小值
     * 目前只计算 遍历到当 (current - last) > curInterval，即返回.
     *
     * @return proxy
     */
    public Proxy optimalProxy(String domainKey) {

        ProxyEntity e = null;

        for (ProxyEntity entity : proxyEntities) {
            Map<String, ProxyStatus> proxyStatus = entity.getProxyStatus();
            if (proxyStatus.containsKey(domainKey)) {
                ProxyStatus status = proxyStatus.get(domainKey);
                // 代理两次使用间隔 大于 设定的间隔, 可以使用
                if (status.interval() > status.getCurInterval()) {
                    e = entity;
                    status.setLastTime(new Date());
                    break;
                }
            }
        }

        // 将该代理移动至list最后一位, 下次请求不会获得该代理
        // 保持一定的代理均匀随机性
        if (null != e) {
            proxyEntities.remove(e);
            proxyEntities.add(e);
            return e.getProxy();
        }
        return randomProxy(domainKey);
    }

    /**
     * 显示完成的代理状态
     */
    public void showProxyStatus() {

    }

    /**
     * 更新 proxy 状态
     */
    public void updateProxyStatus(Proxy proxy, ProxyCode code, String domainKey) {
        boolean has = false;

        for (ProxyEntity entityProxy : proxyEntities) {
            Proxy e = entityProxy.getProxy();
            if (e.equals(proxy)) {
                Map<String, ProxyStatus> proxyStatus = entityProxy.getProxyStatus();
                ProxyStatus status = proxyStatus.get(domainKey);
                if (null != status) {
                    if (code == ProxyCode.ERROR) {
                        status.setCurInterval(status.getCurInterval() + status.getIncSec());
                    } else if (code == ProxyCode.OK) {
                        status.setCurInterval(1);
                    }
                } else {
                    ProxyStatus newStatus = new ProxyStatus();
                    newStatus.setLastTime(new Date());
                    proxyStatus.put(domainKey, newStatus);
                }
                has = true;
                break;
            }
        }
        if (!has) {
            ProxyEntity entity = new ProxyEntity();
            entity.setProxy(proxy);

            ProxyStatus status = new ProxyStatus();
            if (code == ProxyCode.ERROR) {
                status.setCurInterval(status.getCurInterval() + status.getIncSec());
            } else if (code == ProxyCode.OK) {
                status.setCurInterval(1);
            }

            Map<String, ProxyStatus> map = new HashMap<>();
            map.put(domainKey, status);
            entity.setProxyStatus(map);

            proxyEntities.add(entity);
        }
    }

    public void feedback(ProxyProto.Proxy proxy, ProxyCode code, String domainKey) {
        String host = proxy.getHost();
        int port = proxy.getPort();
        String type = proxy.getType();

        Proxy p = new Proxy(host, port, type);
        updateProxyStatus(p, code, domainKey);
    }

    public class ProxyEntity {

        @Getter
        @Setter
        private Proxy proxy;

        /**
         * 对于每类网站, 代理的状态控制
         * key: domainKey
         * value: ProxyStatus
         */
        @Getter
        @Setter
        private Map<String, ProxyStatus> proxyStatus = new HashMap<>();
    }

    public class ProxyStatus {

        /**
         * 返回失败后，增加的间隔秒数
         */
        @Getter
        private final int incSec = 2;

        /**
         * 当前的间隔数
         */
        @Setter
        @Getter
        private int curInterval = 1;

        /**
         * 上一次使用时间
         */
        @Setter
        private Date lastTime = new Date();

        /**
         * 当前时间与上一次使用的时间差
         *
         * @return second interval
         */
        private int interval() {
            long last = this.lastTime.getTime();
            long current = System.currentTimeMillis();
            return (int) ((current - last) / (1000));
        }
    }

}
