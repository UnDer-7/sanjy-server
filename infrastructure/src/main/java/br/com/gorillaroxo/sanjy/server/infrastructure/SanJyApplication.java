package br.com.gorillaroxo.sanjy.server.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@EntityScan(basePackages = "br.com.gorillaroxo.sanjy")
@ComponentScan(basePackages = "br.com.gorillaroxo.sanjy")
@SpringBootApplication
public class SanJyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SanJyApplication.class, args);
    }

}
