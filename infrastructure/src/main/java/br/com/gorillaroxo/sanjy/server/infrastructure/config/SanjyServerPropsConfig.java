package br.com.gorillaroxo.sanjy.server.infrastructure.config;

import br.com.gorillaroxo.sanjy.server.core.util.SanjyServerProps;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "sanjy-server")
record SanjyServerPropsConfig(
    LoggingPropImpl logging
) implements SanjyServerProps {

    record LoggingPropImpl(
        String level,
        String filePath,
        String appender
    ) implements SanjyServerProps.LoggingProp {

    }
}
