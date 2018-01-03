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

package group.chaoliu.lightchaser.common.protocol.http;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.util.Date;

/**
 * HTTP proxy
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Setter
@Getter
public class Proxy {

    private String host;
    private Integer port;

    /**
     * http, https, socket
     */
    private String proxyType;

    private String userName;
    private String password;

    /**
     * 代理验证连续失败次数
     */
    private Integer failedCount = null;

    /**
     * 代理抓取时间
     */
    private Date crawlTime;

    /**
     * 获取该代理的网站域
     */
    private String domainKey;

    public Proxy(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public Proxy(String host, Integer port, String proxyType) {
        this.host = host;
        this.port = port;
        this.proxyType = proxyType;
    }

    public Proxy(String host, Integer port, String proxyType,
                 String userName, String password, Integer failedCount) {
        this.host = host;
        this.port = port;
        this.proxyType = proxyType;
        this.userName = userName;
        this.password = password;
        this.failedCount = failedCount;
    }

    public Proxy(String host, Integer port, String proxyType,
                 String userName, String password, Integer failedCount, String domainKey) {
        this.host = host;
        this.port = port;
        this.proxyType = proxyType;
        this.userName = userName;
        this.password = password;
        this.failedCount = failedCount;
        this.domainKey = domainKey;
    }

    /**
     * Check the proxy object can be used.
     */
    public boolean isNull() {
        return !isNotNull();
    }

    public boolean isNotNull() {
        return (StringUtils.isNotBlank(this.host) && port > 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Proxy proxy = (Proxy) o;

        if (host != null ? !host.equals(proxy.host) : proxy.host != null) {
            return false;
        }

        return port != null ? port.equals(proxy.port) : proxy.port == null;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + (port != null ? port.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {

        StringBuilder str = new StringBuilder();

        str.append("Proxy: host=").append(host).append(", port=").append(port);
        if (StringUtils.isNotBlank(proxyType)) {
            str.append(", type=").append(proxyType);
        }
        if (StringUtils.isNotBlank(userName)) {
            str.append(", user name=").append(userName);
        }
        if (StringUtils.isNotBlank(password)) {
            str.append(", password=").append(password);
        }
        return str.toString();
    }

}