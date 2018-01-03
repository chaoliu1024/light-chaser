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

import group.chaoliu.lightchaser.common.config.YamlConfig;
import group.chaoliu.lightchaser.common.protocol.http.Proxy;
import group.chaoliu.lightchaser.rpc.netty.proxy.ProxySocket;
import io.netty.channel.ChannelFuture;

import java.util.Map;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class Client {

    private static ChannelFuture future;

    public static void main(String[] args) {

        Map proxyConfig = YamlConfig.readProxyConfig();

        ProxySocket client = new ProxySocket(proxyConfig);
        Thread thread = new Thread(client);
        thread.start();

        while (true) {
            if (null != client.getFuture()) {
                if (client.getFuture().isSuccess()) {
                    break;
                }
            }
        }

        try {
            for (int i = 0; i < 10; i++) {
                Proxy proxy = client.requestProxy("ctrip");
                System.out.println(proxy);
                Thread.sleep(1000);
            }
        } catch (Exception e) {
        }

        if (!thread.isInterrupted()) {
            System.out.println("关闭线程...");
            thread.interrupt();
        }
        System.exit(1);
    }
}
