package com.coral.learning.dubbo.cunsmer.control;

import com.alibaba.dubbo.config.annotation.Reference;
import com.coral.learning.dubbo.api.MessageRequest;
import com.coral.learning.dubbo.api.MessageResponse;
import com.coral.learning.dubbo.api.MessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class DubboControl {

    //定向路由
//    @Reference(url = "dubbo://127.0.0.1:20880")
//    private MessageService messageService;

    //注册中心负载
    @Reference
    private MessageService messageService;

    @GetMapping("/dubbo/test")
    @ResponseBody
    public MessageResponse doTest(){
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setTid(UUID.randomUUID().toString());
        System.out.println("call send");
        MessageResponse messageResponse = messageService.send(messageRequest);
        return messageResponse;
    }
}
