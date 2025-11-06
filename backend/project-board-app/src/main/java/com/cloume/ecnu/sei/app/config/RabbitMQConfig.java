package com.cloume.ecnu.sei.app.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ配置类
 */
@Configuration
public class RabbitMQConfig {

    /**
     * 发起新的数据同步任务队列
     */
    @Bean
    public Queue newTaskQueue() {
        Map<String, Object> args = new HashMap<>();// set the queue with a dead letter feature
        args.put("x-queue-type", "classic");
        return new Queue("caculateCultivate", true, false, false, args); // true表示持久化该队列
    }

    /**
     * 取消数据同步任务队列
     *
     * @return
     */
    @Bean
    public Queue abortTaskQueue() {
        return new Queue("task.abort", true);
    }

    @Bean
    public TopicExchange fileExportExchange() {
        return new TopicExchange("topic.file_export");
    }

    @Bean
    public Queue fileExportQueue() {
        return new Queue("topic.file_export", true);
    }

    @Bean
    public Queue fileExportUIQueue() {
        return new Queue("topic.file_export_ui", true);
    }

    @Bean
    public Binding fileExportBinding(Queue fileExportQueue, TopicExchange fileExportExchange) {
        return BindingBuilder.bind(fileExportQueue).to(fileExportExchange).with("key.1");
    }

    @Bean
    public Binding fileExportUIBinding(Queue fileExportUIQueue, TopicExchange fileExportExchange) {
        return BindingBuilder.bind(fileExportUIQueue).to(fileExportExchange).with("ui");
    }

}
