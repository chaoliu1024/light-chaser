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

package me.chaoliu.lightchaser.wps.newspaper.clean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class DocumentCleanerTest {

    private DocumentCleaner cleaner;
    private Document doc;

    @Before
    public void initData() throws IOException {
        this.doc = Jsoup.connect("http://news.163.com/17/0312/10/CFAP3Q9G000189FH.html").get();
        this.cleaner = new DocumentCleaner();
    }

    @Test
    public void testClean() {
        this.cleaner.clean(doc);
        System.out.println(doc);
    }
}
