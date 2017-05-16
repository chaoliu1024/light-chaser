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

package me.chaoliu.lightchaser.wps.newspaper.entity;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import me.chaoliu.lightchaser.wps.stopwords.ChineseStopWords;
import me.chaoliu.lightchaser.wps.stopwords.Language;
import me.chaoliu.lightchaser.wps.stopwords.StopWords;
import me.chaoliu.lightchaser.wps.stopwords.WordStats;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Calculate content of HTML.
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class Content {

    public static final String SCORE_ATTR = "gravityScore";
    public static final String NODES_ATTR = "gravityNodes";

    private float startBoost = 1.0f;
    private float negativeScore = 0;

    // TODO 递归深度
    private int parentNodeRecursive = 2;

    private StopWords stopWords;

    public Content(Language language) {
        this.stopWords = new ChineseStopWords(language.getName());
    }

    public Content() {
        this.stopWords = new ChineseStopWords(Language.CHINESE.getName());
    }

    public Element calculateBestNode(Document doc) {
        Elements textEles = textElements(doc);
        Elements candidateNodes = candidateNodes(textEles);
        return topScoreNode(candidateNodes);
    }

    /**
     * Remove any divs that looks like non-content, clusters of links,
     * or paras with no gusto; add adjacent nodes which look contenty.
     *
     * @param topNode
     * @return
     */
    public Element postHandleNode(Element topNode) {
        Element node = addSiblings(topNode);
        for (Element e : node.children()) {
            String eTag = e.tagName();
            if (!eTag.equals("p") && isHighLinkDensity(e)) {
                e.remove();
            }
        }
        return node;
    }

    /**
     * 如果topNode的兄弟节点可能是正文内容，把这些兄弟节点插入到topNode中
     *
     * @param node
     * @return
     */
    public Element addSiblings(Element node) {
        int baseScore = baseLineScore(node);
        Elements siblings = node.siblingElements();
        for (Element currentNode : siblings) {
            Elements siblingNodes = highScoreSiblingNodes(currentNode, baseScore);
            node.insertChildren(0, siblingNodes);
        }
        return node;
    }

    /**
     * Adds any siblings that may have a decent score to this node.
     * 返回topNode节点的兄弟节点有得分大于 baseLineScore的
     *
     * @param node
     * @param baseScore
     * @return
     */
    public Elements highScoreSiblingNodes(Element node, int baseScore) {

        float coefficientScore = 1;
        float thresholdScore = baseScore * coefficientScore;
        Elements siblings = new Elements();

        if (node.tagName().equals("p") && node.ownText().length() > 0) {
            if (!isHighLinkDensity(node)) {
                siblings.add(node.clone());
            }
            return siblings;
        } else {
            Elements potentialParagraphs = node.getElementsByTag("p");
            if (potentialParagraphs.isEmpty()) {
                return siblings;
            } else {
                for (Element paragraph : potentialParagraphs) {
                    String text = paragraph.text();
                    if (StringUtils.isNotBlank(text)) {
                        WordStats wordStats = stopWords.stopWordStats(text);
                        if (wordStats.getStopWordCount() > 2 && !isHighLinkDensity(paragraph)) {
                            int paragraphScore = wordStats.getStopWordCount();
                            if (paragraphScore > thresholdScore && !isHighLinkDensity(paragraph)) {
                                Element p = paragraph.clone();
                                p.tagName("p");
                                siblings.add(p);
                            }
                        }
                    }
                }
            }
        }
        return siblings;
    }

    /**
     * We could have long articles that have tons of paragraphs
     * so if we tried to calculate the base score against
     * the total text score of those paragraphs it would be unfair.
     * So we need to normalize the score based on the average scoring
     * of the paragraphs within the top node.
     * For example if our total score of 10 paragraphs was 1000
     * but each had an average value of 100 then 100 should be our base.
     *
     * @param node
     * @return
     */
    public int baseLineScore(Element node) {

        // TODO is ok?
        int base = 100000;

        Elements nodes = node.getElementsByTag("p");

        int paragraphNumber = 0;
        int totalParagraphScore = 0;

        for (Element p : nodes) {
            String text = p.text();
            WordStats wordStats = stopWords.stopWordStats(text);

            if (wordStats.getStopWordCount() > 2 && !isHighLinkDensity(p)) {
                paragraphNumber += 1;
                totalParagraphScore += wordStats.getStopWordCount();
            }
        }
        if (paragraphNumber > 0) {
            base = totalParagraphScore / paragraphNumber;
        }
        return base;
    }

    /**
     * return the top score node.
     *
     * @param candidateNodes candidate nodes
     * @return top score node
     */
    private Element topScoreNode(Elements candidateNodes) {
        Element topNode = null;
        double topScore = 0;
        for (Element node : candidateNodes) {
            double score = getScore(node);
            if (score >= topScore) {
                topNode = node;
                topScore = score;
            }
        }
        return topNode;
    }

    private double getScore(Element node) {
        if (node == null)
            return 0;
        return Double.parseDouble(node.attr(SCORE_ATTR));
    }

    public Elements candidateNodes(Elements textEles) {

        Elements candidateNodes = new Elements();
        List<Element> nodesWithText = new ArrayList<>();

        for (Element textEle : textEles) {
            WordStats wordStats = stopWords.stopWordStats(textEle.text());
            // TODO 为什么要有两个停止次？？？？
            if (wordStats.getStopWordCount() > 2 && !isHighLinkDensity(textEle)) {
                nodesWithText.add(textEle);
            }
        }

        int nodesCount = nodesWithText.size();
        float bottomNegativeNodes = (float) (nodesCount * 0.25);

        for (int i = 0; i < nodesWithText.size(); i++) {
            Element node = nodesWithText.get(i);
            double boostScore = calculateBoostScore(node, bottomNegativeNodes, i, nodesCount);

            WordStats wordStats = stopWords.stopWordStats(node.text());

            double upScore = wordStats.getStopWordCount() + boostScore;

            updateParentNodes(candidateNodes, node, upScore, parentNodeRecursive);
        }
        return candidateNodes;
    }

    /**
     * TODO
     *
     * @param node
     * @param bottomNegativeNodes
     * @param i
     * @param nodesCount
     * @return
     */
    private double calculateBoostScore(Element node, float bottomNegativeNodes, int i, int nodesCount) {
        double boostScore = 0;
        if (isBoostable(node)) {
            if (i > 0) {
                boostScore = ((1 / startBoost) * 50);
                startBoost += 1;
            }
            if (nodesCount > 15) {
                if ((nodesCount - i) <= bottomNegativeNodes) {
                    float booster = bottomNegativeNodes - (nodesCount - i);
                    boostScore = Math.pow(booster, 2);
                    double negScore = boostScore + negativeScore;
                    if (negScore > 40) {
                        boostScore = 5;
                    }
                }
            }
        }
        return boostScore;
    }


    /**
     * 递归更新父节点的评分
     *
     * @param parentNodes
     * @param node
     * @param upScore
     * @param recursive
     */
    private void updateParentNodes(Elements parentNodes, Element node, double upScore, int recursive) {
        recursive -= 1;
        if (recursive == 0) {
            return;
        }
        Element parent = node.parent();
        if (null != parent) {
            updateScore(parent, upScore);
            updateNodesCount(parent, 1);
            if (!parentNodes.contains(parent)) {
                parentNodes.add(parent);
            }
        }
        upScore = upScore / 2;
        updateParentNodes(parentNodes, parent, upScore, recursive);
    }

    private void updateScore(Element node, double score) {
        double currentScore = 0;
        if (StringUtils.isNotBlank(node.attr(SCORE_ATTR))) {
            currentScore = Double.parseDouble(node.attr(SCORE_ATTR));
        }
        score += currentScore;
        node.attr(SCORE_ATTR, Double.toString(score));
    }

    private void updateNodesCount(Element node, int nodesCount) {
        double currentCount = 0;
        if (StringUtils.isNotBlank(node.attr(NODES_ATTR))) {
            currentCount = Integer.parseInt(node.attr(NODES_ATTR));
        }
        nodesCount += currentCount;
        node.attr(NODES_ATTR, Integer.toString(nodesCount));
    }

    /**
     * A lot of times the first paragraph might be the caption under an image
     * so we'll want to make sure if we're going to boost a parent node that
     * it should be connected to other paragraphs, at least for the first n
     * paragraphs so we'll want to make sure that the next sibling is a
     * paragraph and has at least some substantial weight to it.
     * 如果兄弟节点包含长文本的话，则需要加分。
     *
     * @param element element
     * @return need to boost or not
     */
    private boolean isBoostable(Element element) {
        String tag = "p";
        int stepsAway = 0;
        int minStopWordCount = 5;

        // TODO 为什么要设置步长？为了节省循环次数？
        int maxStepsAway = 3;

        Elements siblingElements = element.siblingElements();

        for (Element sibling : siblingElements) {
            String tagName = sibling.tagName();
            if (tag.equals(tagName)) {
                if (stepsAway >= maxStepsAway) {
                    return false;
                }
                String tagText = sibling.text();
                WordStats wordStats = stopWords.stopWordStats(tagText);
                if (wordStats.getStopWordCount() > minStopWordCount) {
                    return true;
                }
                stepsAway += 1;
            }
        }
        return false;
    }


    /**
     * Checks the density of links within a node, if there is a high link to text ratio,
     * then the text is less likely to be relevant
     * <p>
     * 通过 linksCount * linkWordsCount / wordsCount; 计算
     *
     * @return
     */
    public boolean isHighLinkDensity(Element node) {

        Elements links = node.getElementsByTag("a");
        if (links.isEmpty()) {
            return false;
        }

        List<Term> words = StandardTokenizer.segment(node.text());
        if (words.isEmpty()) {
            return true;
        }

        float wordsCount = (float) words.size();

        StringBuilder linksText = new StringBuilder();
        for (Element link : links) {
            linksText.append(link.text());
        }

        List<Term> linksWord = StandardTokenizer.segment(linksText.toString());

        float linkWordsCount = (float) linksWord.size();
        float linksCount = (float) links.size();

        float score = linksCount * linkWordsCount / wordsCount;

        if (score >= 1.0)
            return true;
        else {
            return false;
        }
    }


    /**
     * Returns a list of nodes we want to search on like paragraphs and tables.
     *
     * @param doc Document
     * @return node
     */
    public Elements textElements(Document doc) {
        // <p>: 定义段落
        // <pre>: 定义预格式文本
        // <td>: 定义表格中的单元
        String[] textTags = new String[]{"p", "pre", "td"};
        Elements elements = new Elements();
        for (String tag : textTags) {
            elements.addAll(doc.getElementsByTag(tag));
        }
        return elements;
    }
}
