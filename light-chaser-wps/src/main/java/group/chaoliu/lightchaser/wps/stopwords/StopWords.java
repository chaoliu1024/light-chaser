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

package group.chaoliu.lightchaser.wps.stopwords;

import group.chaoliu.lightchaser.wps.util.ResourceFile;
import org.apache.commons.lang.StringUtils;

import java.io.InputStream;
import java.util.*;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public abstract class StopWords {

    public abstract Language language();

    public Map<String, Set<String>> cacheStopWords = new HashMap<>();

    public Set<String> stopWordsSet;

    public StopWords(String language) {
        cacheStopWords.putIfAbsent(language, loadStopWords(language));
        stopWordsSet = cacheStopWords.get(language);
    }

    public Set<String> loadStopWords(String language) {
        String fileName = "/stopwords-" + language + ".txt";
        InputStream in = this.getClass().getResourceAsStream(fileName);
        return new HashSet<>(ResourceFile.ResourceFileContent(in));
    }

    public List<String> segmentWords(String text) {
        return Arrays.asList(text.split(" "));
    }

    public WordStats stopWordStats(String text) {
        WordStats wordStats = new WordStats();
        if (StringUtils.isBlank(text)) {
            return wordStats;
        }

        List<String> stopWords = new ArrayList<>();
        List<String> segmentWords = segmentWords(text);
        for (String word : segmentWords) {
            if (stopWordsSet.contains(word.toLowerCase())) {
                stopWords.add(word.toLowerCase());
            }
        }
        wordStats.setStopWordCount(stopWords.size());
        wordStats.setStopWords(stopWords);
        wordStats.setWordCount(segmentWords.size());

        return wordStats;
    }
}
