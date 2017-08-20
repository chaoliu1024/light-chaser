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

package group.chaoliu.lightchaser.core.fission.proxy.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.util.Date;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Setter
@Getter
public class ProxyPO {

    /**
     * 代理主机
     */
    private String host;

    /**
     * 代理端口号
     */
    private int port;

    /**
     * 代理类型:http,https,socket
     */
    private String proxyType;

    /**
     * 代理用户名
     */
    private String userName;

    /**
     * 代理密码
     */
    private String password;

    /**
     * 最高级别网站,验证时长
     */
    private float costTime;

    /**
     * 1:外部代理， 2:内部代理
     */
    private boolean isInternet;

    /**
     * 0:不可用,数字越高,验证的网站越多,代理质量越好
     */
    private int level;

    /**
     * 代理验证连续失败次数
     */
    private int failedNum;

    /**
     * 更新时间
     */
    private Date updateTime;

    public boolean isNotNULL() {
        return (StringUtils.isNotBlank(host) && port > 10);
    }
}