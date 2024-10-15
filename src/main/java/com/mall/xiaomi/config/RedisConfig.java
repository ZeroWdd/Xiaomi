package com.mall.xiaomi.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

        @Bean
        public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
                RedisTemplate<String, Object> template = new RedisTemplate<>();
                template.setConnectionFactory(redisConnectionFactory);

                // 使用 Jackson2JsonRedisSerializer 来序列化和反序列化 redis 的 value 值
                Jackson2JsonRedisSerializer<Object> jacksonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
                objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
                jacksonSerializer.setObjectMapper(objectMapper);

                // 设置 key 和 value 的序列化规则
                template.setKeySerializer(new StringRedisSerializer()); // key 使用 String 序列化器
                template.setValueSerializer(jacksonSerializer);         // value 使用 Jackson 序列化器

                // 对 hash 的 key 和 value 设置序列化规则
                template.setHashKeySerializer(new StringRedisSerializer());
                template.setHashValueSerializer(jacksonSerializer);

                template.afterPropertiesSet();
                return template;
        }
}
