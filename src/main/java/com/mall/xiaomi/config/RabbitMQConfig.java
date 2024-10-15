package com.mall.xiaomi.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Configuration
public class RabbitMQConfig {



        @Bean
        public Queue queue() {
                return QueueBuilder.durable("seckill_order")
                        .withArgument("x-dead-letter-exchange", "seckill_dead_letter_exchange")  // 配置死信交换机
                        .withArgument("x-dead-letter-routing-key", "seckill_dead_letter_key")    // 配置死信路由键
                        .build();
        }

        // 声明死信队列
        @Bean
        public Queue deadLetterQueue() {
                return QueueBuilder.durable("seckill_dead_letter_queue").build();
        }

        // 声明死信交换机
        @Bean
        public DirectExchange deadLetterExchange() {
                return new DirectExchange("seckill_dead_letter_exchange");
        }

        // 绑定死信队列和死信交换机
        @Bean
        public Binding deadLetterBinding() {
                return BindingBuilder.bind(deadLetterQueue())
                        .to(deadLetterExchange())
                        .with("seckill_dead_letter_key");
        }

        @Bean
        public RetryOperationsInterceptor retryOperationsInterceptor() {
                return RetryInterceptorBuilder.stateless()
                        .maxAttempts(3) // 设置最大重试次数
                        .backOffOptions(1000, 2.0, 10000) // 初始延迟、乘数和最大延迟
                        .recoverer(new RejectAndDontRequeueRecoverer()) // 达到最大重试次数后的处理逻辑
                        .build();
        }



        @Bean
        public Jackson2JsonMessageConverter jsonMessageConverter() {
                return new Jackson2JsonMessageConverter();
        }

        @Bean
        public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter jsonMessageConverter) {
                RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
                rabbitTemplate.setMessageConverter(jsonMessageConverter);
                return rabbitTemplate;
        }

        @Bean
        public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter jsonMessageConverter) {
                SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
                factory.setConnectionFactory(connectionFactory);
                factory.setMessageConverter(jsonMessageConverter);
                factory.setAdviceChain(retryOperationsInterceptor());
                factory.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 手动确认
                return factory;
        }

}
