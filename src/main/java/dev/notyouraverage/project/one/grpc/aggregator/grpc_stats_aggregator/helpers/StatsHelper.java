package dev.notyouraverage.project.one.grpc.aggregator.grpc_stats_aggregator.helpers;

import dev.notyouraverage.project.base.annotations.ServiceHelper;
import dev.notyouraverage.project.one.grpc.aggregator.grpc_stats_aggregator.constants.Constants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;

@ServiceHelper
public class StatsHelper {

    private final RedisTemplate<String, Integer> redisTemplate;

    public StatsHelper(@Qualifier(Constants.COUNTER_REDIS_TEMPLATE) RedisTemplate<String, Integer> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Integer incrementAndGetCount(String name) {
        return Math.toIntExact(redisTemplate.opsForValue().increment(name, 1));
    }

    public Integer getCount(String name) {
        return redisTemplate.opsForValue().get(name);
    }
}
