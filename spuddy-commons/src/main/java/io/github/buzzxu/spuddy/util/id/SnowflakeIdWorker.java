package io.github.buzzxu.spuddy.util.id;



import io.github.buzzxu.spuddy.util.$;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

import static io.github.buzzxu.spuddy.util.Dates.asDate;


/**
 *
 * @author xux
 * @date 2018/6/6 下午2:38
 */
public class SnowflakeIdWorker {
    // ==============================Fields===========================================
    /** 开始时间截 (2020-01-01) */
    private final long twepoch = 1577808000000L;

    /** 机器id所占的位数 */
    private final long workerIdBits = 5L;

    /** 数据标识id所占的位数 */
    private final long datacenterIdBits = 5L;

    /** 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数) */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /** 支持的最大数据标识id，结果是31 */
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    /** 序列在id中占的位数 */
    private final long sequenceBits = 12L;

    /** 机器ID向左移12位 */
    private final long workerIdShift = sequenceBits;

    /** 数据标识id向左移17位(12+5) */
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    /** 时间截向左移22位(5+5+12) */
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    /** 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095) */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /**
     * 工作机器ID(0~31)
     */
    private final long workerId;

    /**
     * 数据中心ID(0~31)
     */
    private final long datacenterId;

    /** 上次生成ID的时间截 */
    private AtomicLong lastTimestamp = new AtomicLong(-1L);

    /** 毫秒内序列(0~4095) */
    private AtomicLong sequence = new AtomicLong(0L);

    /** 最大尝试次数 */
    private static final int MAX_RETRIES = 10;


    public SnowflakeIdWorker() {
        this($.getServerIdAsLong());
    }
    public SnowflakeIdWorker(long datacenterId) {
        this($.getServerIdAsLong(),datacenterId);
    }
    //==============================Constructors=====================================
    /**
     * 构造函数
     * @param workerId 工作ID (0~31)
     * @param datacenterId 数据中心ID (0~31)
     */
    public SnowflakeIdWorker(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    // ==============================Methods==========================================
    public String nextId(long id){
        return nextId()+Id.to6BitId(id);
    }
    /**
     * 获得下一个ID (该方法是线程安全的)
     * @return SnowflakeId
     */
    public  long nextId() {
        for (int i = 0; i < MAX_RETRIES; i++) {
            long timestamp = timeGen();
            long lastTimestampValue = lastTimestamp.get();

            //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
            if (timestamp < lastTimestampValue) {
                throw new RuntimeException(
                        String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestampValue - timestamp));
            }

            //如果是同一时间生成的，则进行毫秒内序列
            if (lastTimestamp.compareAndSet(lastTimestampValue, timestamp)) {
                long sequenceValue = sequence.getAndIncrement() & sequenceMask;
                //毫秒内序列溢出
                if (sequenceValue == 0) {
                    //阻塞到下一个毫秒,获得新的时间戳
                    timestamp = tilNextMillis(lastTimestampValue);
                    lastTimestamp.set(timestamp);
                }
                //移位并通过或运算拼到一起组成64位的ID
                return ((timestamp - twepoch) << timestampLeftShift) //
                        | (datacenterId << datacenterIdShift) //
                        | (workerId << workerIdShift) //
                        | sequenceValue;
            }
            if (i == MAX_RETRIES - 1) {
                // 尝试次数超过限制，让线程休眠一段时间
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    //nothing
                }
            }
        }
        throw new RuntimeException("Exceeded maximum retries to get a unique ID");
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    //==============================Test=============================================
    /** 测试 */
    public static void main(String[] args) {
        System.out.println(asDate(LocalDate.parse("2020-01-01")).getTime());
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
        long vid = idWorker.nextId();
        System.out.println(Long.toBinaryString(vid).length());
        System.out.println("=====");
        for (int i = 0; i < 1000; i++) {
            long id = idWorker.nextId();
            System.out.println(Long.toBinaryString(id));
            System.out.println(id);
        }
    }
}
