package group.chaoliu.lightchaser.mq;

import group.chaoliu.lightchaser.common.Category;
import group.chaoliu.lightchaser.common.protocol.http.RequestMessage;
import group.chaoliu.lightchaser.common.queue.message.QueueMessage;
import group.chaoliu.lightchaser.common.util.SerializeUtil;
import group.chaoliu.lightchaser.mq.redis.RedisClient;

import java.util.List;

public class RedisMessagePool implements IMessagePool {

    private RedisClient client = RedisClient.client();

    public RedisMessagePool() {
        client.connect();
    }

    @Override
    public void addMessage(QueueMessage msg) {
        RequestMessage requestMsg = msg.getRequestMsg();
        Category category = msg.getCategory();
        String key = category.key();
        client.lpush(key.getBytes(), SerializeUtil.serialize(requestMsg));
    }

    @Override
    public void addMessage(List<QueueMessage> msgs) {
        for (QueueMessage msg : msgs) {
            addMessage(msg);
        }
    }

    @Override
    public QueueMessage getMessage(String key) {
        QueueMessage msg = new QueueMessage();
        RequestMessage rmsg = (RequestMessage) SerializeUtil.unserialize(client.lpop(key.getBytes()));
        msg.setRequestMsg(rmsg);
        return msg;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
