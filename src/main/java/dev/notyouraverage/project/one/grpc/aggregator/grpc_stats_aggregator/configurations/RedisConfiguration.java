package dev.notyouraverage.project.one.grpc.aggregator.grpc_stats_aggregator.configurations;

import dev.notyouraverage.project.one.grpc.aggregator.grpc_stats_aggregator.constants.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfiguration {

    @Bean(Constants.COUNTER_REDIS_TEMPLATE)
    public RedisTemplate<String, Integer> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Integer> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }

}
