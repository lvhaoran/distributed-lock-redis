package top.tywang.opensource.lock;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisLock {
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final String LOCK_PREFIX = "dlock_";
    private static final String LOCK_MSG = "OK";
    private static final int DEFAULT_EXPIRE_TIME = 10 * 1000;
    private static final long DEFAULT_SLEEP_TIME = 100;

    private JedisPool jedisPool;

    public RedisLock(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void lock(String key, String request) {
        Jedis jedis = jedisPool.getResource();
        while (true) {
            String result = jedis.set(LOCK_PREFIX + key, request, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, DEFAULT_EXPIRE_TIME);
            if (LOCK_MSG.equals(result)) {
                jedis.close();
                return;
            }
        }
    }

    public void lock(String key, String request, int timeout) throws InterruptedException {
        Jedis jedis = jedisPool.getResource();

        while (timeout >= 0) {
            String result = jedis.set(LOCK_PREFIX + key, request, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, DEFAULT_EXPIRE_TIME);
            if (LOCK_MSG.equals(result)) {
                jedis.close();
                return;
            }
            Thread.sleep(DEFAULT_SLEEP_TIME);
            timeout -= DEFAULT_SLEEP_TIME;
        }
    }

}
