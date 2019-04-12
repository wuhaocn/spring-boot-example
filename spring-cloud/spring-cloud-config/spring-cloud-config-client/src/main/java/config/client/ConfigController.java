package config.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class ConfigController {

    @Value("${expiretime:day!}")
    private String expiretime;
    @RequestMapping("/getInfo")
    public String getInfo(){
        return "getInfo" + expiretime;
    }
}
