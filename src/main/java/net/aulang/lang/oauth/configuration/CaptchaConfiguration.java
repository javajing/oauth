package net.aulang.lang.oauth.configuration;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Configuration
public class CaptchaConfiguration {
    @Bean
    public DefaultKaptcha kaptcha() throws IOException {
        Resource resource = new ClassPathResource("kaptcha.properties");
        try (InputStream inputStream = resource.getInputStream()) {
            Properties properties = new Properties();
            properties.load(inputStream);

            DefaultKaptcha kaptcha = new DefaultKaptcha();
            Config config = new Config(properties);
            kaptcha.setConfig(config);
            return kaptcha;
        }
    }
}
