package net.aulang.lang.oauth.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.annotation.PostConstruct;

@Configuration
public class ThymeleafConfiguration {
    @Value("${app.domain:gzyijian.net}")
    private String domain;

    @Autowired
    private ThymeleafViewResolver viewResolver;

    @PostConstruct
    public void addStaticVariable() {
        viewResolver.addStaticVariable("domain", domain);
    }
}

