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

package group.chaoliu.lightchaser.mq.redis;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class RedisConstants {

    public static final String IP = "redis.ip";

    public static final String PORT = "redis.port";

    public static final String DB = "redis.db";

    public static final String CONNECTION_TIMEOUT = "redis.connectionTimeout";

    public static final String MAX_IDLE = "redis.maxIdle";

    public static final String MIN_IDLE = "redis.minIdle";

    public static final String MAX_WAIT_MILLIS = "redis.maxWaitMillis";

    public static final String MIN_EVICTABLE_IDLE_TIME_MILLIS = "redis.minEvictableIdleTimeMillis";

    public static final String SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS = "redis.softMinEvictableIdleTimeMillis";

    public static final String NUM_TESTS_PER_EVICTION_RUN = "redis.numTestsPerEvictionRun";

    public static final String TEST_ON_BORROW = "redis.testOnBorrow";

    public static final String TEST_ON_RETURN = "redis.testOnReturn";

    public static final String TEST_WHILE_IDLE = "redis.testWhileIdle";

    public static final String TIME_BETWEEN_EVICTION_RUN_MILLIS = "redis.timeBetweenEvictionRunsMillis";

    public static final String BLOCK_WHEN_EXHAUSTED = "redis.blockWhenExhausted";

}