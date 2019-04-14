package com.coral.learning.dubbo.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.coral.learning.dubbo.api.MessageRequest;
import com.coral.learning.dubbo.api.MessageResponse;
import com.coral.learning.dubbo.api.MessageService;
import org.springframework.stereotype.Component;

@Service(interfaceClass = MessageService.class)
@Component
public class MessageServiceDefault implements MessageService {

    @Override
    public MessageResponse send(MessageRequest messageRequest) {
        MessageResponse response = new MessageResponse();
        System.out.println("sendMessage Rev Tid:" + messageRequest.getTid());
        return response;
    }
}