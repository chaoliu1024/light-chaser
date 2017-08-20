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

package group.chaoliu.lightchaser.wps.newspaper.clean;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;

import java.util.regex.Pattern;

/**
 * HTML Cleaner, remove useless tags and node attributes.
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class DocumentCleaner {


    public void clean(Document doc) {

        BodyClassesCleaner bodyCleaner = new BodyClassesCleaner();
        ArticleTagsCleaner articleCleaner = new ArticleTagsCleaner();
        EmTagsCleaner emCleaner = new EmTagsCleaner();
        ParagraphSpanCleaner spansCleaner = new ParagraphSpanCleaner();
        DropCapCleaner dropCapCleaner = new DropCapCleaner();
        CssLinkRemover cssLinkRemover = new CssLinkRemover();
        ScriptStyleRemover scriptStyleRemover = new ScriptStyleRemover();
        BadTagRemover badTagRemover = new BadTagRemover();
        CommentRemover commentRemover = new CommentRemover();
        NodesByRegexRemover nodesByRegexRemover = new NodesByRegexRemover();
        CopyRightRemover copyRightRemover = new CopyRightRemover();
        Div2Paragraph div2Para = new Div2Paragraph("div");
        Div2Paragraph span2Para = new Div2Paragraph("span");

        articleCleaner.setCleaner(bodyCleaner);
        emCleaner.setCleaner(articleCleaner);
        spansCleaner.setCleaner(emCleaner);
        dropCapCleaner.setCleaner(spansCleaner);
        cssLinkRemover.setCleaner(dropCapCleaner);
        scriptStyleRemover.setCleaner(cssLinkRemover);
        badTagRemover.setCleaner(scriptStyleRemover);
        commentRemover.setCleaner(badTagRemover);
        nodesByRegexRemover.setCleaner(commentRemover);
        copyRightRemover.setCleaner(nodesByRegexRemover);
        div2Para.setCleaner(copyRightRemover);
        span2Para.setCleaner(div2Para);

        span2Para.clean(doc);
    }

    /**
     * Remove the all classes of <b>body</b> tag.
     */
    private class BodyClassesCleaner extends CleanDecorator {

        @Override
        public void clean(Document doc) {
            super.clean(doc);
            Elements body = doc.getElementsByTag("body");
            if (null != body) {
                body.get(0).removeAttr("class");
            }
        }
    }

    /**
     * Remove id, name, class attributes of <b>article</b> tag.
     */
    private class ArticleTagsCleaner extends CleanDecorator {

        @Override
        public void clean(Document doc) {
            super.clean(doc);
            Elements articles = doc.getElementsByTag("article");
            for (Element article : articles) {
                String[] attributes = new String[]{"id", "name", "class"};
                for (String attr : attributes) {
                    article.removeAttr(attr);
                }
            }
        }
    }

    /**
     * Unwrap <b>img</b> tags in the tag of em.
     */
    private class EmTagsCleaner extends CleanDecorator {
        @Override
        public void clean(Document doc) {
            super.clean(doc);
            Elements ems = doc.getElementsByTag("em");
            for (Element em : ems) {
                Elements imgs = em.getElementsByTag("img");
                if (imgs.isEmpty()) {
                    em.unwrap();
                }
            }
        }
    }

    /**
     * Remove the <b>span</b> tags, which after p tag.
     */
    private class ParagraphSpanCleaner extends CleanDecorator {
        @Override
        public void clean(Document doc) {
            super.clean(doc);
            Elements elements = doc.select("p span");
            for (Element ele : elements) {
                ele.unwrap();
            }
        }
    }

    /**
     * Unwrap the <b>span</b> nodes, which class match the regular expression of
     * "dropcap" or "drop_cap".
     */
    private class DropCapCleaner extends CleanDecorator {

        @Override
        public void clean(Document doc) {
            super.clean(doc);
            Elements elements = doc.select("span[class~=dropcap], span[class~=drop_cap]");
            for (Element ele : elements) {
                ele.unwrap();
            }
        }
    }

    /**
     * Remove <b>link</b> tag of css href.
     */
    private class CssLinkRemover extends CleanDecorator {
        @Override
        public void clean(Document doc) {
            super.clean(doc);
            Elements elements = doc.select("link[rel=stylesheet]");
            for (Element ele : elements) {
                ele.remove();
            }
        }
    }

    /**
     * Remove <b>comment</b> tag.
     */
    private class CommentRemover extends CleanDecorator {

        @Override
        public void clean(Document doc) {
            super.clean(doc);
            removeComments(doc);
        }

        /**
         * As we are removing child nodes while iterating,
         * we cannot use a normal foreach over children,
         * or will get a concurrent list modification error.
         */
        private void removeComments(Node node) {
            int i = 0;
            while (i < node.childNodes().size()) {
                Node child = node.childNode(i);
                if (child instanceof Comment)
                    child.remove();
                else {
                    removeComments(child);
                    i++;
                }
            }
        }
    }

    /**
     * Remove <b>script</b> and <b>style</b> tags.
     */
    private class ScriptStyleRemover extends CleanDecorator {
        @Override
        public void clean(Document doc) {
            super.clean(doc);
            // remove scripts
            Elements scripts = doc.getElementsByTag("script");
            for (Element script : scripts) {
                if (null == script.parent()) {
                    continue;
                }
                script.remove();
            }
            // remove styles
            Elements styles = doc.getElementsByTag("style");
            for (Element style : styles) {
                if (null == style.parent()) {
                    continue;
                }
                style.remove();
            }
        }
    }

    /**
     * Remove the <b>Element</b> which attributes of "id", "name" or "class" match the string we given.
     */
    private class BadTagRemover extends CleanDecorator {

        private static final String REMOVE_NODES_ATTRIBUTE =
                // TODO 这儿的标签可以结合每次抓取抽取结果，进行训练更新
                "^side$|combx|retweet|mediaarticlerelated|menucontainer" +
                        "|navbar|storytopbar-bucket|utility-bar|inline-share-tools|comment" +
                        "|PopularQuestions|contact|foot|footer|Footer|footnote|cnn_strycaptiontxt" +
                        "|cnn_html_slideshow|cnn_strylftcntnt|links|meta$|shoutbox|sponsor|tags" +
                        "|socialnetworking|socialNetworking|cnnStryHghLght|cnn_stryspcvbx|^inset$" +
                        "|pagetools|post-attributes|welcome_form|contentTools2|the_answers" +
                        "|communitypromo|runaroundLeft|subscribe|vcard|articleheadings|date" +
                        "|^print$|popup|author-dropdown|tools|socialtools|byline|konafilter" +
                        "|KonaFilter|breadcrumbs|^fn$|wp-caption-text|legende|ajoutVideo|timestamp|js_replies";

        @Override
        public void clean(Document doc) {
            super.clean(doc);
            Elements elements = doc.getAllElements();
            String[] tagAttrs = REMOVE_NODES_ATTRIBUTE.split("\\|");
            // ids
            removeElements(elements, tagAttrs, "id");
            // class
            removeElements(elements, tagAttrs, "class");
            // name
            removeElements(elements, tagAttrs, "name");
        }

        /**
         * remove elements
         *
         * @param elements elements
         * @param tagAttrs tag attributes name
         * @param tag      unwrap tag name
         */
        private void removeElements(Elements elements, String[] tagAttrs, String tag) {
            for (Element ele : elements) {
                if (null == ele.parent()) {
                    continue;
                }
                String attr = ele.attr(tag);
                for (String tagAttr : tagAttrs) {
                    if (tagAttr.startsWith("^") && tagAttr.endsWith("$") && attr.equals(tagAttr.substring(1, tagAttr.length() - 1))) {
                        ele.remove();
                        break;
                    } else if (attr.contains(tagAttr)) {
                        ele.remove();
                        break;
                    }
                }
            }
        }
    }

    /**
     * Remove the <b>Element</b> which attributes of "id" or "class" match the regular expression we given.
     */
    private class NodesByRegexRemover extends CleanDecorator {

        private final String[] REMOVE_NODES_ATTRIBUTE_REGEX = new String[]
                {"^caption$", " google ", "^[^entry-]more.*$", "[^-]facebook",
                        "facebook-broadcasting", "[^-]twitter"};

        @Override
        public void clean(Document doc) {
            super.clean(doc);
            for (String regex : REMOVE_NODES_ATTRIBUTE_REGEX) {
                removeNodeByRegex(doc, regex);
            }
        }

        private void removeNodeByRegex(Document doc, String regex) {
            Elements elements = doc.getAllElements();
            String[] attrTags = new String[]{"id", "class"};
            for (Element ele : elements) {
                if (null == ele.parent()) {
                    continue;
                }
                for (String attrTag : attrTags) {
                    String attr = ele.attr(attrTag);
                    Pattern p = Pattern.compile(regex);
                    if (p.matcher(attr).matches()) {
                        ele.remove();
                    }
                }
            }
        }
    }

    /**
     * Remove <b>CopyRight</b> elements.
     */
    private class CopyRightRemover extends CleanDecorator {
        @Override
        public void clean(Document doc) {
            super.clean(doc);
            Elements children = doc.getAllElements();
            for (int i = children.size() - 1; i > 1; i--) {
                if (null == children.get(i).parent()) {
                    continue;
                }
                if (children.get(i).hasText()) {
                    String text = children.get(i).text();
                    if (text.contains("©") || text.contains("版权所有")
                            || text.contains("ICP备") || text.contains("联系我们")) {
                        children.get(i).remove();
                    }
                }
            }
        }
    }

    /**
     * Transform div tag to paragraph tag (p).
     */
    private class Div2Paragraph extends CleanDecorator {

        private String tagName;

        public Div2Paragraph(String tagName) {
            this.tagName = tagName;
        }

        @Override
        public void clean(Document doc) {
            super.clean(doc);
            Elements divs = doc.getElementsByTag(tagName);

            String[] tags = new String[]{"a", "img", "p", "blockquote",
                    "div", "pre", "table", "dl", "ol", "ul"};

            for (Element div : divs) {
                Elements items = getElementsByTags(div, tags);
                if (null != div && CollectionUtils.isEmpty(items)) {
                    div.tagName("p");
                } else if (null != div && StringUtils.isNotBlank(div.ownText())) {
                    div.tagName("p");   // 直接包含文字的节点改为p节点
                }
            }
        }

        /**
         * Return these element of tags name is
         * "a, img, p, blockquote, div, pre table, dl, ol, ul"
         * which in the given div.
         *
         * @param element div elements
         * @param tags    which is:
         *                <ul>
         *                <li>a: 锚</li>
         *                <li>img: 图像</li>
         *                <li>p: 段落</li>
         *                <li>blockquote: 长的引用</li>
         *                <li>div: 文档中的节</li>
         *                <li>pre: 预格式文本</li>
         *                <li>table: 表格</li>
         *                <li>dl: 定义列表</li>
         *                <li>ol: 有序列表</li>
         *                <li>ul: 无序列表</li>
         *                </ul>
         */
        private Elements getElementsByTags(Element element, String[] tags) {
            Elements items = new Elements();
            if (tags == null || tags.length == 0) {
                return items;
            }
            for (String tag : tags) {
                items.addAll(element.getElementsByTag(tag));
            }
            return items;
        }
    }
}