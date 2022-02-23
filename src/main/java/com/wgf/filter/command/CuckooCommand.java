package com.wgf.filter.command;

import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * @description: 布谷鸟过滤器脚本
 * @author: ken 😃
 * @create: 2022-02-22 18:23
 **/
public class CuckooCommand {

    /**
     * 创建布谷鸟过滤器脚本，支持指定过期时间
     */
    private final static RedisScript<Boolean> RESERVE =
            new DefaultRedisScript<Boolean>(
                    "local exists = redis.call('EXISTS', KEYS[1])\n" +
                            "if (exists == 0) then\n" +
                            "  redis.call('CF.RESERVE', KEYS[1], ARGV[1])\n" +
                            "  if (tonumber(ARGV[2]) > 0) then\n" +
                            "    redis.call('PEXPIRE', KEYS[1], ARGV[2])\n" +
                            "  end\n" +
                            "  return true\n" +
                            "  else\n" +
                            "  return false\n" +
                            "end", Boolean.class);

    /**
     * 将一个item添加到布谷鸟过滤器，如果过滤器不存在则创建过滤器
     */
    private final static RedisScript<Boolean> ADD =
            new DefaultRedisScript<Boolean>("return redis.call('CF.ADD', KEYS[1], ARGV[1])", Boolean.class);


    /**
     * 如果item不存在，将一个item添加到布谷鸟过滤器
     */
    private final static RedisScript<Boolean> ADD_NX =
            new DefaultRedisScript<Boolean>("return redis.call('CF.ADDNX', KEYS[1], ARGV[1])", Boolean.class);

    /**
     * 确定一个item是否可能存在于布谷鸟过滤器中
     */
    private final static RedisScript<Boolean> EXISTS =
            new DefaultRedisScript<>("return redis.call('CF.EXISTS', KEYS[1], ARGV[1])", Boolean.class);

    /**
     * 删除一个item
     */
    private final static RedisScript<Boolean> DEL =
            new DefaultRedisScript<>("return redis.call('CF.DEL', KEYS[1], ARGV[1])", Boolean.class);


    /**
     * 获取创建布谷鸟过滤器脚本
     *
     * @return
     */
    public static RedisScript<Boolean> getReserveScript() {
        return RESERVE;
    }

    /**
     * 获取添加脚本
     *
     * @return
     */
    public static RedisScript<Boolean> getAddScript() {
        return ADD;
    }

    /**
     * 获取添加脚本
     *
     * @return
     */
    public static RedisScript<Boolean> getAddNxScript() {
        return ADD_NX;
    }

    /**
     * 获取判断item是否存在脚本
     *
     * @return
     */
    public static RedisScript<Boolean> getExistsScript() {
        return EXISTS;
    }

    /**
     * 获取删除item脚本
     *
     * @return
     */
    public static RedisScript<Boolean> getDelScript() {
        return DEL;
    }
}
