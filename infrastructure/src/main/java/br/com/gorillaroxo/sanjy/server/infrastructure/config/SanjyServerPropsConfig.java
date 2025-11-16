package br.com.gorillaroxo.sanjy.server.infrastructure.config;

import br.com.gorillaroxo.sanjy.server.core.ports.driven.SanjyServerProps;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "sanjy-server", ignoreUnknownFields = false)
record SanjyServerPropsConfig(
        @NotNull @Valid LoggingPropImpl logging,
        @NotNull @Valid ApplicationPropImpl application) implements SanjyServerProps {

    record LoggingPropImpl(
            @NotBlank String level,
            @NotBlank String filePath,
            @NotBlank String appender) implements SanjyServerProps.LoggingProp {}

    record ApplicationPropImpl(
            @NotBlank String name,
            @NotBlank String version,
            @NotBlank String description,
            @NotNull @Valid ApplicationContactPropImpl contact,
            @NotNull @Valid ApplicationDocumentationPropImpl documentation)
            implements SanjyServerProps.ApplicationProp {}

    record ApplicationContactPropImpl(
            @NotBlank String name,
            @NotBlank @URL String url,
            @NotBlank @Email String email) implements SanjyServerProps.ApplicationContactProp {}

    record ApplicationDocumentationPropImpl(
            @NotBlank @URL String url, @NotBlank String description)
            implements SanjyServerProps.ApplicationDocumentationProp {}
}
