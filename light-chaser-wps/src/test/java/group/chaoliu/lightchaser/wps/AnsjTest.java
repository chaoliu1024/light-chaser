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

package group.chaoliu.lightchaser.wps;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.junit.Test;

import java.util.Iterator;

/**
 * <a @href="https://github.com/NLPchina/ansj_seg">ansj</a> test demo
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class AnsjTest {

    @Test
    public void testToAnalysis() {
        Result terms = ToAnalysis.parse("网易新闻");
        Iterator<Term> iterator = terms.iterator();
        while (iterator.hasNext()) {
            Term term = iterator.next();
            System.out.println(term.getName() + "\t" + term.getNatureStr() + "\t" + term.getRealName());
            System.out.println(term.getSubTerm() + "\t" + term.getSynonyms());
            System.out.println("=========================");
        }
    }
}
