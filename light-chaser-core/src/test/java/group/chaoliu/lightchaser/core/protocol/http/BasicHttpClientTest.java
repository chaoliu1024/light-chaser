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

import org.junit.Test;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class BasicHttpClientTest {

    @Test
    public void testGetDomainName() {
        String url1 = "http://www.66ip.cn/1.html";
        String url2 = "https://www.google.com.hk/";
        String url3 = "http://vacations.ctrip.com/tours/d-rizhao-622/grouptravel";
        String domainName1 = BasicHttpClient.getDomainName(url1);
        String domainName2 = BasicHttpClient.getDomainName(url2);
        String domainName3 = BasicHttpClient.getDomainName(url3);
        System.out.println(domainName1);
        System.out.println(domainName2);
        System.out.println(domainName3);

        System.out.println(BasicHttpClient.getDomainKey(url1));
        System.out.println(BasicHttpClient.getDomainKey(url2));
        System.out.println(BasicHttpClient.getDomainKey(url3));
    }
}
