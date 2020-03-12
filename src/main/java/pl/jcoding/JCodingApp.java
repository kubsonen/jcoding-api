package pl.jcoding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class JCodingApp {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(JCodingApp.class);
        application.run(args);
    }

}
