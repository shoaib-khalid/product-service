package com.kalsym.product.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import java.awt.image.BufferedImage;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import com.kalsym.product.service.repository.CustomRepositoryImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author 7cu
 */
@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories(repositoryBaseClass = CustomRepositoryImpl.class)

public class ProductServiceApplication {

    public static String VERSION;
    public static String ASSETURL ;
    public static String MARKETPLACEURL ;


    static {
        System.setProperty("spring.jpa.hibernate.naming.physical-strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
    }

    public static void main(String... args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }

    @Value("${asset.service.url}")
    private String assetServiceUrl;

    @Value("${marketplace.url}")
    private String marketPlaceUrl;

    @Bean
    CommandLineRunner lookup(ApplicationContext context) {
        return args -> {
            ASSETURL = assetServiceUrl;
            MARKETPLACEURL = marketPlaceUrl;

        };
    }


    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public HttpMessageConverter<BufferedImage> createImageHttpMessageConverter() {
        return new BufferedImageHttpMessageConverter();
    }

}
