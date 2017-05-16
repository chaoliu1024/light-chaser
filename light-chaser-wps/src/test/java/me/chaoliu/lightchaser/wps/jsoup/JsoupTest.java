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

package me.chaoliu.lightchaser.wps.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

/**
 * Test jsoup function.
 * TODO When this work completed, the class can be removed.
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class JsoupTest {

    @Test
    public void testRemoveElem() throws IOException {
        Document doc = Jsoup.connect("http://news.qq.com/a/20170315/026650.htm").get();
        System.out.println(doc);
        Elements head = doc.getElementsByTag("head");
        if (null != head) {
            head.get(0).remove();
        }
        System.out.println(doc);
    }

    @Test
    public void testCssSelect() throws IOException {
        Document doc = Jsoup.connect("http://news.163.com/17/0312/10/CFAP3Q9G000189FH.html").get();
        Elements elements = doc.select("div[class~=js_N_navSelect], span[class~=drop_cap]");
        for (Element e : elements) {
            System.out.println(e);
        }
    }

    @Test
    public void testCssSelect2() throws IOException {
        Document doc = Jsoup.connect("http://news.163.com/17/0312/10/CFAP3Q9G000189FH.html").get();
        Elements elements = doc.select("li h3");
        for (Element e : elements) {
            System.out.println(e);
        }
    }

    @Test
    public void testDeleteAttr() throws IOException {
        Document doc = Jsoup.connect("http://news.qq.com/a/20170315/026650.htm").get();
        Elements body = doc.getElementsByTag("body");
        if (null != body) {
            body.get(0).removeAttr("id");
        }
    }

    /**
     * 测试注释节点
     *
     * @throws IOException
     */
    @Test
    public void testComment() throws IOException {
        Document doc = Jsoup.connect("http://www.cnblogs.com/lexus/archive/2012/02/19/2358456.html").get();
        for (Element e : doc.getAllElements()) {
            for (Node n : e.childNodes()) {
                if (n instanceof Comment) {
                    System.out.println(n);
                }
            }
        }
    }

    /**
     * 测试正则表达式
     */
    @Test
    public void testReplaceAll() {
        String h1Text = "我与总书记议国是：建设社会稳定长治久安新边疆";
        h1Text = h1Text.replaceAll("[^a-zA-Z0-9\\ ]", "");
        System.out.println(h1Text);
    }
}
