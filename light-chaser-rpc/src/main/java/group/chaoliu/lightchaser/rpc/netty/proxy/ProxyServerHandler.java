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
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * TODO
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class ProxyServerHandler extends SimpleChannelInboundHandler<ProxyProto.ProxyMsg> {

    private ProxyManager proxyManager = ProxyManager.proxyManagerInstance();

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, ProxyProto.ProxyMsg msg)
            throws Exception {
        ProxyProto.ProxyMsg.MsgType msgType = msg.getMsgType();

        InetSocketAddress clientAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String hostAddress = clientAddress.getAddress().getHostAddress();
        String hostName = clientAddress.getAddress().getHostName();
        int port = clientAddress.getPort();
        log.info("client info: " + hostAddress + "\t" + hostName + "\t" + port);

        if (msgType == ProxyProto.ProxyMsg.MsgType.REQ_MSG) {
            String domainKey = msg.getDomainKey();
            Proxy proxy = proxyManager.optimalProxy(domainKey);
            ProxyProto.ProxyMsg proxyMsg = ProxyProtobufUtil.protoProxyMsg(proxy, msg.getReqId());

            ctx.writeAndFlush(proxyMsg);
            ReferenceCountUtil.release(msg);
        } else if (msgType == ProxyProto.ProxyMsg.MsgType.FEEDBACK_MSG) {
            // 代理反馈
            ProxyProto.Proxy proxy = msg.getProxy();
            int code = proxy.getCode();
            ProxyCode proxyCode = ProxyCode.getProxyCode(code);
            if (null != proxyCode) {
                String domainKey = msg.getDomainKey();
                proxyManager.feedback(proxy, proxyCode, domainKey);
            } else {
                log.info("返回代理状态码有误...");
            }
        }
    }
}
