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

package group.chaoliu.lightchaser.core.persistence;

import group.chaoliu.lightchaser.common.Category;
import group.chaoliu.lightchaser.common.queue.message.QueueMessage;
import group.chaoliu.lightchaser.core.crawl.CrawlerMessage;
import group.chaoliu.lightchaser.core.util.MessageDigestUtil;
import group.chaoliu.lightchaser.hbase.HBaseClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Component
@Slf4j
public class WebPageStore implements Store<CrawlerMessage> {

    private Map<Category, HBaseClient> hBaseClientPool = new HashMap<>();

    @Override
    public void save(CrawlerMessage crawlerMgs) {
        QueueMessage queueMsg = crawlerMgs.getQueueMessage();
        Category category = queueMsg.getCategory();
        HBaseClient hBaseClient = getHBaseClient(category);
        if (null != hBaseClient) {
            String url = queueMsg.getRequestMsg().getURL();
            String page = crawlerMgs.getResponseMsg().getBody();
            String rowKey = MessageDigestUtil.MD5(url);
            log.debug("store page of url {} into HBase.", url);
            hBaseClient.put(rowKey, "p", "c", page);
        }
    }

    private HBaseClient getHBaseClient(Category category) {
        HBaseClient hBaseClient = hBaseClientPool.get(category);
        if (null == hBaseClient) {
            try {
                hBaseClient = new HBaseClient(category.getType());
                hBaseClientPool.put(category, hBaseClient);
            } catch (IOException e) {
                log.error("create HBase client instance error");
            }
        }
        return hBaseClient;
    }
}