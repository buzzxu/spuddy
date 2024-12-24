package io.github.buzzxu.spuddy.util.id;

import io.github.buzzxu.spuddy.util.$;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.buzzxu.spuddy.util.Hashs.sha1;


/**
 * 53 bits unique id:
 *
 * |--------|--------|--------|--------|--------|--------|--------|--------|
 * |00000000|00011111|11111111|11111111|11111111|11111111|11111111|11111111|
 * |--------|---xxxxx|xxxxxxxx|xxxxxxxx|xxxxxxxx|xxx-----|--------|--------|
 * |--------|--------|--------|--------|--------|---xxxxx|xxxxxxxx|xxx-----|
 * |--------|--------|--------|--------|--------|--------|--------|---xxxxx|
 *
 * Maximum ID = 11111_11111111_11111111_11111111_11111111_11111111_11111111
 *
 * Maximum TS = 11111_11111111_11111111_11111111_111
 *
 * Maximum NT = ----- -------- -------- -------- ---11111_11111111_111 = 65535
 *
 * Maximum SH = ----- -------- -------- -------- -------- -------- ---11111 = 31
 *
 * It can generate 64k unique id per IP and up to 2106-02-07T06:28:15Z.
 */
@Slf4j
public class Id {
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Pattern PATTERN_LONG_ID = Pattern.compile("^([0-9]{15})([0-9a-f]{32})([0-9a-f]{3})$");

    private static final long OFFSET = LocalDate.of(2000, 1, 1).atStartOfDay(ZoneId.of("Z")).toEpochSecond();

    private static final long MAX_NEXT = 0b11111_11111111_111L;

    private static final long SHARD_ID = $.getServerIdAsLong();

    private static long offset = 0;

    private static long lastEpoch = 0;

    public static UUID uuid() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new UUID(random.nextLong(), random.nextLong());
    }


    public static long nextId() {
        return nextId(System.currentTimeMillis() / 1000);
    }

    private static long nextId(long epochSecond) {
        lock.lock();
        try {
            if (epochSecond < lastEpoch) {
                // warning: clock is turn back:
                log.warn("clock is back: " + epochSecond + " from previous:" + lastEpoch);
                epochSecond = lastEpoch;
            }
            if (lastEpoch != epochSecond) {
                lastEpoch = epochSecond;
                reset();
            }
            offset++;
            long next = offset & MAX_NEXT;
            if (next == 0) {
                log.warn("maximum id reached in 1 second in epoch: " + epochSecond);
                return nextId(epochSecond + 1);
            }
            return generateId(epochSecond, next, SHARD_ID);
        }finally {
            lock.unlock();
        }
    }

    private static void reset() {
        offset = 0;
    }

    private static long generateId(long epochSecond, long next, long shardId) {
        return ((epochSecond - OFFSET) << 21) | (next << 5) | shardId;
    }

    public static long stringIdToLongId(String stringId) {
        // a stringId id is composed as timestamp (15) + uuid (32) + serverId (000~fff).
        Matcher matcher = PATTERN_LONG_ID.matcher(stringId);
        if (matcher.matches()) {
            long epoch = Long.parseLong(matcher.group(1)) / 1000;
            String uuid = matcher.group(2);
            byte[] sha1 = sha1(uuid);
            long next = ((sha1[0] << 24) | (sha1[1] << 16) | (sha1[2] << 8) | sha1[3]) & MAX_NEXT;
            long serverId = Long.parseLong(matcher.group(3), 16);
            return generateId(epoch, next, serverId);
        }
        throw new IllegalArgumentException("Invalid id: " + stringId);
    }

    /**
     *  生成6位数字 超过6位的id截取后6位 不足6位的补0
     * @param id
     * @return
     */
    public static String to6BitId(long id) {
        // 取id的后6位作为订单号的用户ID部分
        long userIdPart = id % 1000000;
        // 格式化为6位字符串，不足6位时在前面补零
        String userIdStr = String.format("%06d", userIdPart);
        return userIdStr;
    }

    public static void main(String[] args) {
        System.out.println(Id.nextId());
        System.out.println(uuid().toString());
        System.out.println(to6BitId(143523567));
    }

}
