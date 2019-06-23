package net.aulang.lang.oauth.configuration;

import net.aulang.lang.oauth.factory.HttpConnectionFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpClientConfiguration {

    @Bean
    @Qualifier("trustStoreSslSocketFactory")
    public SSLConnectionSocketFactory trustStoreSslSocketFactory() {
        return new SSLConnectionSocketFactory(HttpConnectionFactory.sslSocketFactory(), HttpConnectionFactory.hostnameVerifier());
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(HttpConnectionFactory.clientHttpRequestFactory());
    }

}
