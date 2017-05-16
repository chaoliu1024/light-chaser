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

package me.chaoliu.lightchaser.core.protocol.http;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * test CommonHeaders
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class CommonHeadersTest {

    @Test
    public void testMap() {
        Map<String, String> map = new HashMap<>();

        if (map.isEmpty()) {
            System.out.println("map is null");
        } else {
            System.out.println(map);
        }
    }

    @Test
    public void testCommonHeaders(){
        CommonHeaders defaultHeaders = new CommonHeaders();
        Map<String, String> headers = defaultHeaders.getHeaders();
        System.out.println(headers);
    }
}