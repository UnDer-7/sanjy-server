package br.com.gorillaroxo.sanjy.server.infrastructure.test;

import br.com.gorillaroxo.sanjy.server.infrastructure.SanJyApplication;
import br.com.gorillaroxo.sanjy.server.infrastructure.test.config.TestcontainersConfig;
import io.restassured.RestAssured;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;

@Slf4j
@SpringBootTest(classes = SanJyApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {TestcontainersConfig.class})
public abstract class IntegrationTestController {

    @LocalServerPort
    public int serverPort;

    @PostConstruct
    public void initRestAssured() {
        RestAssured.port = serverPort;
        RestAssured.urlEncodingEnabled = false;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
}
