package br.com.gorillaroxo.sanjy.server.infrastructure.config;

import br.com.gorillaroxo.sanjy.server.core.ports.driven.SanjyServerProps;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "sanjy-server", ignoreUnknownFields = false)
record SanjyServerPropsConfig(
    @NotNull @Valid LoggingPropImpl logging,
    @NotNull @Valid ApplicationPropImpl application,
    @NotNull @Valid ExternalHttpClientsPropImpl externalHttpClients)
    implements SanjyServerProps {

    record LoggingPropImpl(
        @NotBlank String level,
        @NotBlank String filePath,
        @NotBlank String appender) implements SanjyServerProps.LoggingProp {

    }

    record ApplicationPropImpl(
        @NotBlank String name,
        @NotBlank String version,
        @NotBlank String description,
        @Pattern(
            regexp = "^$|^/[a-zA-Z0-9]([a-zA-Z0-9._~-]|/[a-zA-Z0-9])*+$",
            message = """
                Invalid endpoints prefix. Must be empty/null or a valid URL path starting with '/' (e.g. '/api', '/server/v1'). \
                Cannot be just '/', cannot end with '/', and must contain only alphanumeric characters, '.', '_', '~', or '-'
                """)
        String endpointsPrefix,
        @NotNull @Valid ApplicationContactPropImpl contact,
        @NotNull @Valid ApplicationDocumentationPropImpl documentation)
        implements SanjyServerProps.ApplicationProp {

    }

    record ApplicationContactPropImpl(
        @NotBlank String name,
        @NotBlank @URL String url,
        @NotBlank @Email String email) implements SanjyServerProps.ApplicationContactProp {

    }

    record ApplicationDocumentationPropImpl(
        @NotBlank @URL String url, @NotBlank String description)
        implements SanjyServerProps.ApplicationDocumentationProp {

    }

    record ExternalHttpClientsPropImpl(
        @NotNull @Valid ExternalHttpClientsRetryConfigPropImpl retryConfig,
        @NotNull @Valid GenericHttpClientsPropImpl github)
        implements SanjyServerProps.ExternalHttpClientsProp {

    }

    record GenericHttpClientsPropImpl(@NotBlank @URL String url) implements SanjyServerProps.GenericHttpClientsProp {

    }

    record ExternalHttpClientsRetryConfigPropImpl(
        @NotNull @PositiveOrZero Integer maxAttempt,
        @NotNull @PositiveOrZero Integer interval,
        @NotNull @PositiveOrZero Integer backoffMultiplier)
        implements SanjyServerProps.ExternalHttpClientsRetryConfigProp {

    }

}
