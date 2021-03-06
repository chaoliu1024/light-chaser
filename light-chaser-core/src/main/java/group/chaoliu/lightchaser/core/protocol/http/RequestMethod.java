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

package group.chaoliu.lightchaser.core.protocol.http;

import lombok.Getter;

public enum RequestMethod {

    GET("get"),

    PUT("put"),

    POST("post"),

    HEAD("head"),

    TRACE("trace"),

    OPTIONS("options"),

    DELETE("detele");

    @Getter
    private String requestMethod;

    RequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public static RequestMethod method(String method) {
        for (RequestMethod requestMethod : RequestMethod.values()) {
            if (requestMethod.getRequestMethod().equals(method)) {
                return requestMethod;
            }
        }
        return null;
    }
}
