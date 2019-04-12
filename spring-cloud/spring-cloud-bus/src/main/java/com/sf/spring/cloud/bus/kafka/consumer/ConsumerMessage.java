package com.sf.spring.cloud.bus.kafka.consumer;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(Sink.class)
public class ConsumerMessage {

    @StreamListener(Sink.INPUT)
    private void receive(String vote) {
        System.out.println("receive message : " + vote);
    }
}
