/*
 * Copyright (c) 2016, Chao Liu (chaoliu1024@gmail.com). All rights reserved.
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

import java.util.HashMap;
import java.util.Map;

/**
 * Default Http header value
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class CommonHeaders extends HttpHeaders {

    private String UserAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36";

    private Map<String, String> headers = new HashMap<>();

    public CommonHeaders() {
        this.headers.put(USER_AGENT, UserAgent);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(String headerName, String headerValue) {
        this.headers.put(headerName, headerValue);
    }
}
