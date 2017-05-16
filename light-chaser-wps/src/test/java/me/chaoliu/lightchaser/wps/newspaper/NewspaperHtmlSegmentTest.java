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

package me.chaoliu.lightchaser.wps.newspaper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class NewspaperHtmlSegmentTest {

    private NewspaperHtmlSegment htmlSegment;
    private Document doc;

    @Before
    public void initData() throws IOException {
        this.doc = Jsoup.connect("http://news.163.com/17/0312/10/CFAP3Q9G000189FH.html").get();
//        this.doc = Jsoup.connect("http://vacations.ctrip.com/grouptravel/p5953067s12.html").get();
//        this.doc = Jsoup.connect("http://cn.secretchina.com/news/gb/2017/03/24/817857.html.%E9%83%91%E4%B8%AD%E5%8E%9F%EF%BC%9A%E5%AE%98%E6%96%B9%E6%94%BE%E9%A3%8E%E6%B1%9F%E6%B3%BD%E6%B0%91%E5%AD%99%E5%AD%90%E6%91%8A%E4%B8%8A%E5%A4%A7%E4%BA%8B(%E7%BB%84%E5%9B%BE).html").proxy("127.0.0.1", 8580).get();
//        this.doc = Jsoup.connect("http://cn.ntdtv.com/xtr/gb/2017/03/24/a1317294.html").proxy("127.0.0.1", 8580).get();
        this.htmlSegment = new NewspaperHtmlSegment(this.doc);
    }

    @Test
    public void testAuthor() {
        String s = htmlSegment.authors();
        System.out.println(s);
    }

    @Test
    public void testMata() {
        Map<String, String> mate = htmlSegment.metaData();
        System.out.println(mate.toString());
    }

    @Test
    public void testMainContent() {
        String s = htmlSegment.mainContent();
        System.out.println(s);
    }

    @Test
    public void testFullContent() {
        String s = htmlSegment.fullContent();
        System.out.println(s);
    }

    @Test
    public void testTitle() {
        System.out.println(this.htmlSegment.title());
    }
}