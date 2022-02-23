package com.wgf.filter.command;

import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @description: Redis脚本命令
 * @author: ken 😃
 * @create: 2022-02-22 12:20
 **/
public abstract class CommandHelper {
    /**
     * 生成脚本
     *
     * @param script
     * @param values
     * @return
     */
    public static RedisScript<List> generateScript(String script, Collection<?> values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= values.size(); i++) {
            if (i != 1) {
                sb.append(",");
            }
            sb.append("ARGV[").append(i).append("]");
        }
        return new DefaultRedisScript<List>(String.format(script, sb.toString()), List.class);
    }

    public static List<Object> extract(List<Long> result, List<Object> values) {
        List<Object> list = new ArrayList<>();

        for (int i = 0; i < result.size(); i++) {
            if (1 == result.get(i)) {
                list.add(values.get(i));
            }
        }

        return list;
    }
}
