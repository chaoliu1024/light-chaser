/*
 * Copyright (c) 2016, Chao Liu (chaoliu1024@gmail.com). All rights reserved.
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

package group.chaoliu.lightchaser.mq.memory;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * These messages have been visited
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class VisitedURLs {

    private Set<String> visitedURLs = new ConcurrentSkipListSet<String>();

    /**
     * check whether a url is visited or not
     *
     * @param url
     * @return
     */
    public boolean isCrawled(String url) {
        return visitedURLs.contains(url);
    }

    public void addVisitedURL(String url) {
        visitedURLs.add(url);
    }
}
