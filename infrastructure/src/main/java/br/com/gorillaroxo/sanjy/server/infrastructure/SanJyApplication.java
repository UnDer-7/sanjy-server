package br.com.gorillaroxo.sanjy.server.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "br.com.gorillaroxo.sanjy")
@EntityScan(basePackages = "br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity")
@ConfigurationPropertiesScan(basePackages = "br.com.gorillaroxo.sanjy.server.infrastructure.config")
public class SanJyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SanJyApplication.class, args);
    }

}
