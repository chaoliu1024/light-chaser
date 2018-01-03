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

import org.junit.Test;

import static junit.framework.TestCase.assertSame;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class ProxyCodeTest {

    @Test
    public void testProxyCode() {
        ProxyCode okCode = ProxyCode.getProxyCode(200);
        assertSame(okCode, ProxyCode.OK);

        ProxyCode nullCode = ProxyCode.getProxyCode(201);
        assertSame(nullCode, null);
    }
}