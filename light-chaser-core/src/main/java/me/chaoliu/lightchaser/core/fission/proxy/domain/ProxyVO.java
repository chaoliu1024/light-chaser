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

package me.chaoliu.lightchaser.core.fission.proxy.domain;

import lombok.Data;

import java.util.Date;

/**
 * Proxy VO
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Data
public class ProxyVO {

    private String host;
    private int port;
    private String proxyType;
    private String userName;
    private String password;
    private int usedType;
    private boolean isInternet;
    private float costTime;
    private int failedNum;
    private Date updateTime;

}