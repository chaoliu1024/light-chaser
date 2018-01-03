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

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * http request message
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Setter
@Getter
public class RequestMessage implements Serializable {

    private String URL;

    private boolean isPostRequest = false;

    private Map<String, String> headers = new HashMap<>();

    private Map<String, String> cookie = new HashMap<>();

    public RequestMessage() {
    }

    public RequestMessage(String URL, Map<String, String> headers, Map<String, String> cookie) {
        this.URL = URL;

        if (!headers.isEmpty()) {
            this.headers.putAll(headers);
        } else {
            CommonHeaders commonHeaders = new CommonHeaders();
            this.headers.putAll(commonHeaders.getHeaders());
        }
        this.cookie = cookie;
    }

    @Override
    public String toString() {
        return "RequestMessage{" +
                "URL='" + URL + '\'' +
                ", isPostRequest=" + isPostRequest +
                ", headers=" + headers +
                ", cookie=" + cookie +
                '}';
    }

    public static RequestMessage requestMsgDemo(int i) {
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setURL("www.tuniu.com");
        requestMessage.setPostRequest(true);

        Map<String, String> head = new HashMap<>();
        head.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36");
        head.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        requestMessage.setHeaders(head);

        Map<String, String> cookie = new HashMap<>();
        cookie.put("host", "127.0.0." + i);
        cookie.put("MKT_Pagesource", "PC");
        requestMessage.setCookie(cookie);

        return requestMessage;
    }
}
