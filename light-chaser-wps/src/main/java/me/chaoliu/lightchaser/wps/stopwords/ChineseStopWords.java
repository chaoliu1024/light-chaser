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

package me.chaoliu.lightchaser.wps.stopwords;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class ChineseStopWords extends StopWords {

    private Set<String> stopWords;

    public ChineseStopWords(String language) {
        super(language);
    }

    @Override
    public List<String> segmentWords(String text) {
        List<String> words = new ArrayList<>();
        List<Term> terms = StandardTokenizer.segment(text);
        for (Term term : terms) {
            String word = term.word;
            words.add(word);
        }
        return words;
    }

    @Override
    public Language language() {
        return Language.CHINESE;
    }
}
