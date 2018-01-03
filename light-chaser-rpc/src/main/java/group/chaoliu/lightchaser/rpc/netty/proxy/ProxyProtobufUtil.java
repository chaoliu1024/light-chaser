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

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class ProxyProtobufUtil {

    public static ProxyProto.Proxy protoProxy(Proxy proxy) {
        return protoProxy(proxy, 0);
    }

    /**
     * @param proxy proxy
     * @param code  proxy feedback code
     * @return
     */
    public static ProxyProto.Proxy protoProxy(Proxy proxy, int code) {
        ProxyProto.ProxyMsg.Builder proxyMsg = ProxyProto.ProxyMsg.newBuilder();
        ProxyProto.Proxy.Builder proxyBuilder = proxyMsg.getProxyBuilder();
        proxyBuilder.setHost(proxy.getHost());
        proxyBuilder.setPort(proxy.getPort());
        proxyBuilder.setType(proxy.getProxyType());
        proxyBuilder.setCode(code);
        return proxyBuilder.build();
    }

    public static ProxyProto.ProxyMsg protoProxyMsg(Proxy proxy, String reqId) {
        ProxyProto.ProxyMsg.Builder proxyMsg = ProxyProto.ProxyMsg.newBuilder();

        ProxyProto.Proxy.Builder proxyBuilder = proxyMsg.getProxyBuilder();
        proxyBuilder.setHost(proxy.getHost());
        proxyBuilder.setPort(proxy.getPort());
        proxyBuilder.setType(proxy.getProxyType());
        proxyBuilder.setCode(0);
        ProxyProto.Proxy proxyProto = proxyBuilder.build();

        proxyMsg.setProxy(proxyProto);
        proxyMsg.setReqId(reqId);

        return proxyMsg.build();
    }

}
