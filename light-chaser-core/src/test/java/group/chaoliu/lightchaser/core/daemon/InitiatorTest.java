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

package group.chaoliu.lightchaser.core.daemon;

import group.chaoliu.lightchaser.common.Category;
import org.junit.Test;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class InitiatorTest {

    @Test
    public void testInitMySqlTable() {
        Category category = new Category("diy", "ctrip");
        Initiator initiator = new Initiator();
        initiator.initMySqlTable(category);
    }
}
