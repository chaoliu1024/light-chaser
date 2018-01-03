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

import group.chaoliu.lightchaser.rpc.netty.protobuf.ProxyProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Proxy Socket 客户端
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class ProxySocketHandler extends SimpleChannelInboundHandler<ProxyProto.ProxyMsg> {

    /**
     * 客户端接收到消息
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, ProxyProto.ProxyMsg msg) throws Exception {

        String reqId = msg.getReqId();
        SyncWriteFuture future = SyncWriter.syncWriteKey.get(reqId);
        if (future != null) {
            future.setProxyMsg(msg);
        }
    }
}
