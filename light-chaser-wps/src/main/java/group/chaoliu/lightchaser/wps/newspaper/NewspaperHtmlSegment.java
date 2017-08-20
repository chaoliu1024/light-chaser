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

package group.chaoliu.lightchaser.wps.newspaper;

import group.chaoliu.lightchaser.wps.HtmlSegment;
import group.chaoliu.lightchaser.wps.newspaper.clean.DocumentCleaner;
import group.chaoliu.lightchaser.wps.newspaper.entity.Author;
import group.chaoliu.lightchaser.wps.newspaper.entity.Content;
import group.chaoliu.lightchaser.wps.newspaper.entity.Meta;
import group.chaoliu.lightchaser.wps.newspaper.entity.Title;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Map;

/**
 * The core algorithm of this web page segmentation is inspired by the project
 * <a @href="https://github.com/codelucas/newspaper">newspaper</a>.
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class NewspaperHtmlSegment implements HtmlSegment {

    private Document doc;

    private DocumentCleaner cleaner = new DocumentCleaner();

    public NewspaperHtmlSegment(Document doc) {
        this.doc = doc;
    }

    @Override
    public String title() {
        return new Title().title(this.doc);
    }

    @Override
    public String authors() {
        return new Author().author(this.doc);
    }

    @Override
    public String metaDescription() {
        return null;
    }

    @Override
    public String metaKeywords() {
        return null;
    }

    @Override
    public Map<String, String> metaData() {
        return new Meta().metaInfo(this.doc);
    }

    @Override
    public String publishDate() {
        return null;
    }

    @Override
    public String mainContent() {
        Content content = new Content();
        Document doc = this.doc.clone();
        cleaner.clean(doc);
        Element topNode = content.calculateBestNode(doc);
        Element node = content.postHandleNode(topNode);
        OutputFormatter formatter = new OutputFormatter(node);
        return formatter.format();
    }

    @Override
    public String fullContent() {
        Content content = new Content();
        Document doc = this.doc.clone();
        cleaner.clean(doc);
        Elements textEles = content.textElements(doc);
        Elements eles = content.candidateNodes(textEles);

        StringBuilder allText = new StringBuilder();

        for (Element ele : eles) {
            OutputFormatter formatter = new OutputFormatter(ele);
            allText.append(formatter.format());
        }
        return allText.toString();
    }
}
