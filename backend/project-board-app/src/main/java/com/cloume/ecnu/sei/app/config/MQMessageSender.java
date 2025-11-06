package com.cloume.ecnu.sei.app.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

/**
 * RabbitMQ消息生产者工具类
 */
@Component
public class MQMessageSender implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public final static String TOPIC_NEW_USER = "topic.new_users";

    public final static String NEW_MESSAGE = "message.new";

    public final static String TOPIC_FILE_EXPORT = "topic.file_export";
    public final static String TOPIC_KEY_FILE_EXPORT = "key.1";
    public final static String TOPIC_QUEUE_FILE_EXPORT = "topic.file_export";
    public final static String TOPIC_QUEUE_FILE_EXPORT_UI = "topic.file_export_ui";
    public final static String TOPIC_QUEUE_KEY_FILE_EXPORT_UI = "ui";

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            System.out.println("消息发送成功:" + correlationData);
        } else {
            System.out.println("消息发送失败:" + cause);
        }
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println(message.getMessageProperties().getCorrelationId() + " 发送失败");
    }

    /**
     * 发送消息，不需要实现任何接口，供外部调用。
     *
     * @param msg
     */
    public void send(String msg, String topic) {

        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());

        try {
            System.out.println("开始发送消息 : " + msg.toLowerCase());
            rabbitTemplate.convertAndSend(topic, "", msg, correlationId);
            System.out.println("结束发送消息 : " + msg.toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(String msg, String topic, String routingKey) {

        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());

        try {
            System.out.println("开始发送消息 : " + msg.toLowerCase());
            rabbitTemplate.convertAndSend(topic, routingKey, msg, correlationId);
            System.out.println("结束发送消息 : " + msg.toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
