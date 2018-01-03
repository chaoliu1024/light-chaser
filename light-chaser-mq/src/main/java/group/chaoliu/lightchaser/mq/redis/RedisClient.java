package group.chaoliu.lightchaser.mq.redis;

import group.chaoliu.lightchaser.common.Category;
import group.chaoliu.lightchaser.common.config.PropertiesConfig;
import group.chaoliu.lightchaser.common.protocol.http.RequestMessage;
import group.chaoliu.lightchaser.common.queue.message.QueueMessage;
import group.chaoliu.lightchaser.common.util.SerializeUtil;
import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.Set;

public class RedisClient {

    private Jedis jedis;

    private static final String DEFAULT_REDIS_IP = "127.0.0.1";

    private static final int DEFAULT_REDIS_DB = 0;

    private static final RedisClient redisClient = new RedisClient();

    private RedisClient() {
    }

    public static RedisClient client() {
        return redisClient;
    }

    public void connect() {
        String ip = PropertiesConfig.getString("redis.ip");
        String dbNum = PropertiesConfig.getString("redis.db");
        int db;
        if (StringUtils.isBlank(ip)) {
            ip = DEFAULT_REDIS_IP;
        }
        try {
            db = Integer.parseInt(dbNum);
        } catch (NumberFormatException e) {
            db = DEFAULT_REDIS_DB;
        }
        jedis = new Jedis(ip);
        jedis.select(db);
    }

    public boolean isConnected() {
        return StringUtils.isNotBlank(jedis.ping());
    }

    public String get(String key) {
        return jedis.get(key);
    }

    public void set(String key, String value) {
        jedis.set(key, value);
    }

    /**
     * 将所有指定的值插入到存于 key 的列表的头部。
     *
     * @param key   key
     * @param value key
     */
    public void lpush(String key, String value) {
        jedis.lpush(key, value);
    }

    public void lpush(byte[] key, byte[] value) {
        jedis.lpush(key, value);
    }

    public void rpush(String key, String value) {
        jedis.rpush(key, value);
    }

    public void rpush(byte[] key, byte[] value) {
        jedis.rpush(key, value);
    }

    /**
     * 移除并且返回 key 对应的 list 的第一个元素。
     */
    public byte[] lpop(byte[] key) {
        return jedis.lpop(key);
    }

    public String lpop(String key) {
        return jedis.lpop(key);
    }

    public Long llen(String key) {
        return jedis.llen(key);
    }

    public Set<String> keys(String pattern) {
        return jedis.keys(pattern);
    }

    public void testPush(String key) {
        RequestMessage requestMessage = RequestMessage.requestMsgDemo(1);
        Category category = new Category("ota", "tuniu");

        QueueMessage msg = new QueueMessage();
        msg.setCategory(category);
        msg.setRequestMsg(requestMessage);
        msg.setUrlLevel(100);

        byte[] value1 = SerializeUtil.serialize(msg);
        lpush(key.getBytes(), value1);
    }

    public void testPop(String key) {
        QueueMessage msg = (QueueMessage) SerializeUtil.unserialize(lpop(key.getBytes()));
        System.out.println(msg);
    }

    public static void main(String[] args) {
        RedisClient redis = RedisClient.client();
        redis.connect();
        String key = "test";

//        redis.testPush(key);
        redis.testPop(key);
    }
}
