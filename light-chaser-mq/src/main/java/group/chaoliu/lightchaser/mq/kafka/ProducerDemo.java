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

package group.chaoliu.lightchaser.mq.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class ProducerDemo {


    public static void init() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "10.10.30.153:9092");
        // 'acks' controls the criteria under which requests are considered complete
        props.put("acks", "all");
        // when request fails, don't retry
        props.put("retries", 0);
        // The producer maintains buffers of unsent records for each partition.
        props.put("batch.size", 16384);
        // reduce the number of requests, Nagle's algorithm
        props.put("linger.ms", 1);
        // controls the total amount of memory available to the producer for buffering.
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // The producer consists of a pool of buffer space
        Producer<String, String> producer = new KafkaProducer<>(props);
        for (int i = 0; i < 100; i++) {
            // send() method is asynchronous.
            // When called it adds the record to a buffer of pending record sends and immediately returns.
            producer.send(new ProducerRecord<String, String>("test", Integer.toString(i), Integer.toString(i)));
        }

        producer.close();
    }

    public static void main(String[] args) {
        init();
    }
}
