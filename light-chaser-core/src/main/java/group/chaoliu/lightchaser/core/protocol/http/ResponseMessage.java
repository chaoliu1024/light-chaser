/*
 * Copyright (c) 2015, Chao Liu (chaoliu1024@gmail.com). All rights reserved.
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
import lombok.Setter;

import java.util.Map;

/**
 * HTTP response message
 * <p>
 * Http CrawlerMessage contains three part: (1) start line, (2) header and (3) body
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Setter
@Getter
public class ResponseMessage implements Cloneable {

    // 1. request line
    // 2. response line
    // 3. method
    // 4. status code
    // 5. reason
    // 6. version
    private String startLine;

    private Map<String, String> responseHeaders;

    private String body;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
