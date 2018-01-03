package group.chaoliu.lightchaser.mq.redis;

import group.chaoliu.lightchaser.common.config.PropertiesConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {

    private static JedisPool jedisPool;

    private RedisPool() {
    }

    private synchronized static void initJedisPool() {
        if (null == jedisPool) {
            JedisPoolConfig config = new JedisPoolConfig();
            String ip = PropertiesConfig.getString(RedisConstants.IP);
            int port = PropertiesConfig.getInt(RedisConstants.PORT);
            int db = PropertiesConfig.getInt(RedisConstants.DB);
            int connectionTimeout = PropertiesConfig.getInt(RedisConstants.CONNECTION_TIMEOUT);
            int maxIdle = PropertiesConfig.getInt(RedisConstants.MAX_IDLE);
            int minIdle = PropertiesConfig.getInt(RedisConstants.MIN_IDLE);
            int maxWaitMillis = PropertiesConfig.getInt(RedisConstants.MAX_WAIT_MILLIS);
            int minEvictableIdleTimeMillis = PropertiesConfig.getInt(RedisConstants.MIN_EVICTABLE_IDLE_TIME_MILLIS);
            int softMinEvictableIdleTimeMillis = PropertiesConfig.getInt(RedisConstants.SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS);
            int numTestsPerEvictionRun = PropertiesConfig.getInt(RedisConstants.NUM_TESTS_PER_EVICTION_RUN);
            boolean testOnBorrow = PropertiesConfig.getBoolean(RedisConstants.TEST_ON_BORROW);
            boolean testOnReturn = PropertiesConfig.getBoolean(RedisConstants.TEST_ON_RETURN);
            boolean testWhileIdle = PropertiesConfig.getBoolean(RedisConstants.TEST_WHILE_IDLE);
            int timeBetweenEvictionRunsMillis = PropertiesConfig.getInt(RedisConstants.TIME_BETWEEN_EVICTION_RUN_MILLIS);
            boolean blockWhenExhausted = PropertiesConfig.getBoolean(RedisConstants.BLOCK_WHEN_EXHAUSTED);

            config.setMaxIdle(maxIdle);
            config.setMinIdle(minIdle);
            config.setMaxWaitMillis(maxWaitMillis);
            config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
            config.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
            config.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
            config.setTestOnBorrow(testOnBorrow);
            config.setTestOnReturn(testOnReturn);
            config.setTestWhileIdle(testWhileIdle);
            config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
            config.setBlockWhenExhausted(blockWhenExhausted);
            jedisPool = new JedisPool(config, ip, port, connectionTimeout, null, db);
        }
    }

    /**
     * Get one redis instance from jedis pool.
     *
     * @return jedis obj
     */
    public synchronized static Jedis redis() {
        if (null != jedisPool) {
            return jedisPool.getResource();
        } else {
            initJedisPool();
            return jedisPool.getResource();
        }
    }

    public static void releaseJedis(final Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    public synchronized static void destroyPool() {
        jedisPool.destroy();
    }
}
