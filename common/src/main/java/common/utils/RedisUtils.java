package common.utils;

import common.utils.play.BaseGlobal;
import play.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.UnsupportedEncodingException;
import java.util.function.Function;

/**
 * Created by liubin on 15-4-9.
 */
public class RedisUtils {

    public static final String REDIS_KEY_SEPERATOR = ":";

    public static <T> T withJedisClient(Function<Jedis, T> callback) {

        JedisPool jedisPool = BaseGlobal.injector.instanceOf(JedisPool.class);
        Jedis j = jedisPool.getResource();

        try {
            T result = callback.apply(j);

            return result;
        } finally {
            jedisPool.returnResource(j);
        }

    }

    public static String buildKey(String... parts) {

        StringBuilder sb = new StringBuilder();
        for(String part : parts) {
            sb.append(part).append(REDIS_KEY_SEPERATOR);
        }
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    public static byte[] buildByteKey(String... parts) {
        try {
            return buildKey(parts).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            Logger.error("", e);
            return null;
        }
    }

}
