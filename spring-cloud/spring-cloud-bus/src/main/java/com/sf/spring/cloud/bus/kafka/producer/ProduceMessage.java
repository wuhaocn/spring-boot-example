package com.sf.spring.cloud.bus.kafka.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;

import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.support.MessageBuilder;

@EnableBinding(Source.class)
public class ProduceMessage {
    /**
     *
     */
    @Autowired
    public Source send;
    public void sendMessage(String message) {
        try {
            send.output().send(MessageBuilder.withPayload(message).build());
            System.out.println("消息发送chenggpmg，原因：");
        } catch (Exception e) {
            System.out.println("消息发送失败，原因："+e);
        }
    }

}
