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

import group.chaoliu.lightchaser.core.crawl.CrawlerMessage;
import group.chaoliu.lightchaser.core.daemon.Job;
import group.chaoliu.lightchaser.core.persistence.hbase.HBaseClient;
import group.chaoliu.lightchaser.core.util.MessageDigestUtil;
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

    private Map<Job, HBaseClient> hBaseClientPool = new HashMap<>();

    @Override
    public void save(CrawlerMessage crawlerMgs) {
        Job job = crawlerMgs.getJob();
        HBaseClient hBaseClient = getHBaseClient(job);
        if (null != hBaseClient) {
            String url = crawlerMgs.getRequestMsg().getURL();
            String page = crawlerMgs.getResponseMsg().getBody();
            String rowKey = MessageDigestUtil.MD5(url);
            log.debug("store page of url {} into HBase.", url);
            hBaseClient.put(rowKey, "p", "c", page);
        }
    }

    private HBaseClient getHBaseClient(Job job) {
        HBaseClient hBaseClient = hBaseClientPool.get(job);
        if (null == hBaseClient) {
            try {
                hBaseClient = new HBaseClient(job);
                hBaseClientPool.put(job, hBaseClient);
            } catch (IOException e) {
                log.error("create HBase client instance error");
            }
        }
        return hBaseClient;
    }
}