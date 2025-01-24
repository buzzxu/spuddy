package io.github.buzzxu.spuddy.messaging.redis;

import io.github.buzzxu.spuddy.messaging.*;
import io.github.buzzxu.spuddy.redis.Redis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.params.XClaimParams;
import redis.clients.jedis.params.XPendingParams;
import redis.clients.jedis.params.XReadGroupParams;
import redis.clients.jedis.resps.StreamEntry;
import redis.clients.jedis.resps.StreamPendingEntry;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static redis.clients.jedis.StreamEntryID.UNRECEIVED_ENTRY;

/**
 * @author xux
 * @date 2025年01月18日 21:16:21
 */
public class JedisConumser implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger("message-conumser");
    private Redis redis;
    protected final MessageHandler<?> messageHandler;
    protected String topic, group, consumer;

    public JedisConumser(Redis redis, MessageHandler<?> messageHandler, String topic, String group, String consumer) {
        this.redis = redis;
        this.messageHandler = messageHandler;
        this.topic = topic;
        this.group = group;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        try {
            redis.execute(redis -> {
                startMessageConsumer(redis);
                return null;
            });
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }



    private  void startMessageConsumer(Jedis jedis) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                List<Map.Entry<String, List<StreamEntry>>> entries = jedis.xreadGroup(group, consumer, XReadGroupParams.xReadGroupParams().count(1).block(0), Map.of(topic, UNRECEIVED_ENTRY));
                if (entries != null) {
                    for (Map.Entry<String, List<StreamEntry>> message : entries) {
                        List<StreamEntry> streams = message.getValue();
                        for (StreamEntry stream : streams) {
                            logger.info("Receive redis stream '{}',group '{}',conumer '{}',messageId '{}'", topic, group, consumer, stream.getID());
                            processMessage(jedis,stream);
                        }
                    }
                }

                Thread.sleep(100);
            }catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.error("Error processing message: {}",e.getMessage());
            }

        }
//        CompletableFuture.runAsync(() -> {
//
//        });
    }

    private void processMessage(Jedis jedis,StreamEntry stream){
        try {
            Map<String, String> fields = stream.getFields();
            String data = fields.get(JedisPublisher.KEY);
            if (data == null) {
                jedis.xdel(topic, stream.getID());
                logger.error("Receive redis stream '{}', messageId '{}',data is null,del it", topic, stream.getID());
                return;
            }
            // 将JSON转换为对象
            messageHandler.accept(data);
            jedis.xack(topic, group, stream.getID());
        }  catch (MessageRetryException ex) {
            logger.error("Receive redis stream '{}', messageId '{}',error '{}',retry this message!", topic, stream.getID(), ex.getMessage(),ex);
        } catch (MessageRejectException ex) {
            logger.warn("Receive redis stream '{}', messageId '{}',error '{}',reject this message!", topic, stream.getID(), ex.getMessage());
            if (ex.isReject()) {
                logger.warn("Receive redis stream '{}', messageId '{}',error '{}',del this message!", topic, stream.getID(), ex.getMessage());
                jedis.xdel(topic, stream.getID());
            }
        } catch (MessagingAckException ex) {
            logger.error("Receive redis[auto-ack] stream '{}',group '{}',conumser '{}',messageId '{}', error '{}'", topic, group, consumer, stream.getID(), ex.getMessage());
            jedis.xack(topic, group, stream.getID());
        } catch (IllegalArgumentException ex) {
            logger.error("Receive redis stream '{}',group '{}',conumser '{}', error '{}'", topic, group, consumer, ex.getMessage(), ex);
        }catch (Exception e) {
            logger.warn("Failed to process message: " + stream.getID() + ", error: " + e.getMessage());
        }
    }
    public void startPendingMessageProcessor() {
        processPendingMessages();
    }

    private void processPendingMessages() {
        redis.execute(redis->{
            try {
                List<StreamPendingEntry> pendingMessages = redis.xpending(topic, group, XPendingParams.xPendingParams().count(10).consumer(consumer));
                long pendingCount;
                long expired = 5 * 60 * 1000; // 5分钟超时
                Instant now = Instant.now();
                if((pendingCount = pendingMessages.stream()
                        .filter(v -> v.getDeliveredTimes() > 1
                                || Duration.between(Instant.ofEpochMilli(v.getIdleTime()), now).toMinutes() > 1)
                        .count()) > 0) {
                    logger.info("Pending redis stream '{}',group '{}',conumer '{}',count '{}'", topic, group, consumer, pendingCount);
                    StreamEntryID offset = new StreamEntryID();
                    for(StreamPendingEntry pendingMessage : pendingMessages){
                        List<StreamEntry> claimedEntries = redis.xclaim(
                                topic,
                                group,
                                consumer,
                                expired,
                                XClaimParams.xClaimParams().retryCount(1),
                                pendingMessage.getID()
                        );
                        for(StreamEntry claimedEntry : claimedEntries){
                            processMessage(redis,claimedEntry);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Error processing pending messages: {}" ,e.getMessage());
            }

            return null;
        });
    }

    public JedisConumser createGroup(){
        redis.execute(redis->{
            if(!redis.exists(topic)){
                logger.info("redis not exists `{}`,create it",topic);
                try {
                    //创建group
                    redis.xgroupCreate(topic, group, StreamEntryID.LAST_ENTRY, true);
                } catch (Exception ex) {
                    logger.error("Redis stream '{}', create group '{}' error.'{}'", topic, group, ex.getMessage());
                }
            }
            return null;
        });
        return this;
    }
}
