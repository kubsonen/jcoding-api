package pl.jcoding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
public class JCodingApp {
    public static void main(String[] args) throws Exception {
        SpringApplication application = new SpringApplication(JCodingApp.class);

        String propertyFilePath = System.getProperty("jcoding-api-prop-file");
        File propertyFile = new File(propertyFilePath);
        if (!propertyFile.exists())
            throw new RuntimeException("Property file not found.");

        try (InputStream is = new FileInputStream(propertyFile)) {
            Properties props = new Properties();
            props.load(is);
            application.setDefaultProperties(props);
        }

        application.run(args);
    }

}
