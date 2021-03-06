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

package group.chaoliu.lightchaser.core.persistence.hbase;

import group.chaoliu.lightchaser.common.Category;
import group.chaoliu.lightchaser.common.Category;
import group.chaoliu.lightchaser.core.util.MessageDigestUtil;
import group.chaoliu.lightchaser.hbase.HBaseClient;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

import java.io.IOException;

public class HBaseClientTest {

    @Test
    public void testGet() {
        System.setProperty("hadoop.home.dir", "d:\\hadoop_home");
        try {
            Category category = new Category("ota", "ctrip");
            HBaseClient hBaseClient = new HBaseClient(category.getType());
            String rowKey = MessageDigestUtil.MD5("http://vacations.ctrip.com/bookingnext/Calendar/CalendarInfo?ProductID=3090323&StartCity=141&SalesCity=141&MinPrice=2650&EffectDate=2017-04-13&ExpireDate=2017-09-07");
            Result result = hBaseClient.get(rowKey, "p", "c");
            String resRow = Bytes.toString(result.getRow());
            System.out.println("Row: " + resRow);
            byte[] val = null;
            if (result.containsColumn(Bytes.toBytes("p"), Bytes.toBytes("c"))) {
                val = result.getValue(Bytes.toBytes("p"), Bytes.toBytes("c"));
                System.out.println("value: " + Bytes.toString(val));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
