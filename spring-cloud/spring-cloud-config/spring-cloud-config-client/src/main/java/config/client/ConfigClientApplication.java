package config.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
@RestController
public class ConfigClientApplication {
    @RequestMapping("/")
    public String home() {
        return "configclient index";
    }
    @RequestMapping("/info")
    public String info() {
        return "configclient";
    }

    public static void main(String[] args) throws IOException {
        Properties configProperties = new Properties();
        InputStream config = ConfigClientApplication.class.getClassLoader().getResourceAsStream("application-configclient.properties");
        configProperties.load(config);
        SpringApplication springApplication = new SpringApplication(ConfigClientApplication.class);
        springApplication.setDefaultProperties(configProperties);
        springApplication.run(args);
    }
}