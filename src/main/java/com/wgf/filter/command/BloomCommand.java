package com.wgf.filter.command;

import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collection;
import java.util.List;

/**
 * @description: 布隆过滤器脚本
 * @author: ken 😃
 * @create: 2022-02-21 22:22
 **/
public class BloomCommand {

    /**
     * 创建布隆过滤器脚本，支持指定过期时间
     */
    private final static RedisScript<Boolean> RESERVE =
            new DefaultRedisScript<Boolean>(
                    "local exists = redis.call('EXISTS', KEYS[1])\n" +
                            "if (exists == 0) then\n" +
                            "  redis.call('BF.RESERVE', KEYS[1], ARGV[1], ARGV[2])\n" +
                            "  if (tonumber(ARGV[3]) > 0) then\n" +
                            "    redis.call('PEXPIRE', KEYS[1], ARGV[3])\n" +
                            "  end\n" +
                            "  return true\n" +
                            "  else\n" +
                            "  return false\n" +
                            "end", Boolean.class);

    /**
     * 将一个item添加到布隆过滤器，如果过滤器不存在则创建过滤器
     */
    private final static RedisScript<Boolean> ADD =
            new DefaultRedisScript<Boolean>("return redis.call('BF.ADD', KEYS[1], ARGV[1])", Boolean.class);

    /**
     * 将多个item添加到布隆过滤器
     */
    private final static String M_ADD = "return redis.call('BF.MADD', KEYS[1], %s)";

    /**
     * 确定一个item是否可能存在于布隆过滤器中
     */
    private final static RedisScript<Boolean> EXISTS =
            new DefaultRedisScript<>("return redis.call('BF.EXISTS', KEYS[1], ARGV[1])", Boolean.class);

    /**
     * 确定过滤器中是否存在一个或多个item
     */
    private final static String M_EXISTS = "return redis.call('BF.MEXISTS', KEYS[1], %s)";


    /**
     * 获取创建布隆过滤器脚本
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
     * 获取批量添加脚本
     *
     * @param values
     * @return
     */
    public static RedisScript<List> getMultiAdd(Collection<Object> values) {
        return CommandHelper.generateScript(M_ADD, values);
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
     * 获取批量判断存在脚本
     *
     * @return
     */
    public static RedisScript<List> getMultiExists(Collection<?> values) {
        return CommandHelper.generateScript(M_EXISTS, values);
    }
}