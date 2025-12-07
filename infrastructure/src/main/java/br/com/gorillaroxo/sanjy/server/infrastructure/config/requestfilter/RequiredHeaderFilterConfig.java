package br.com.gorillaroxo.sanjy.server.infrastructure.config.requestfilter;

import br.com.gorillaroxo.sanjy.server.core.exception.InvalidValuesException;
import br.com.gorillaroxo.sanjy.server.entrypoint.util.RequestConstants;
import br.com.gorillaroxo.sanjy.server.infrastructure.mapper.BusinessExceptionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.ai.mcp.server.autoconfigure.McpServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Order(2)
@Configuration
public class RequiredHeaderFilterConfig extends OncePerRequestFilter {
    private static final String WILDCARD_PATH = "/**/*";

    private final AntPathMatcher pathMatcher;
    private final SwaggerUiConfigProperties swaggerUiConfigProperties;
    private final SpringDocConfigProperties springDocConfigProperties;
    private final McpServerProperties mcpServerProperties;
    private final ObjectMapper objectMapper;
    private final BusinessExceptionMapper businessExceptionMapper;
    private final Set<String> ignoredPaths;

    public RequiredHeaderFilterConfig(
            final AntPathMatcher pathMatcher,
            final SwaggerUiConfigProperties swaggerUiConfigProperties,
            final SpringDocConfigProperties springDocConfigProperties,
            final McpServerProperties mcpServerProperties,
            final ObjectMapper objectMapper,
            final BusinessExceptionMapper businessExceptionMapper) {

        this.pathMatcher = pathMatcher;

        this.swaggerUiConfigProperties = swaggerUiConfigProperties;
        this.springDocConfigProperties = springDocConfigProperties;
        this.mcpServerProperties = mcpServerProperties;
        this.objectMapper = objectMapper;
        this.businessExceptionMapper = businessExceptionMapper;

        final var customIgnoredPath = List.of("/favicon.ico");

        this.ignoredPaths = Stream.of(getApiDocsPaths(), getSwaggerUiPaths(), getMcpServerPaths(), customIgnoredPath)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {

        final var requestUrl = request.getRequestURI();
        return ignoredPaths.stream().anyMatch(ignoredPath -> pathMatcher.match(ignoredPath, requestUrl));
    }

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {

        final boolean isValid = validateRequiredHeaders(request, response);
        if (isValid) {
            filterChain.doFilter(request, response);
        }
    }

    private boolean validateRequiredHeaders(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
        final String correlationId = request.getHeader(RequestConstants.Headers.X_CORRELATION_ID);
        final String channel = request.getHeader(RequestConstants.Headers.X_CHANNEL);

        final List<String> missingHeaders = new ArrayList<>();

        if (correlationId == null || correlationId.isBlank()) {
            missingHeaders.add(RequestConstants.Headers.X_CORRELATION_ID);
        }

        if (channel == null || channel.isBlank()) {
            missingHeaders.add(RequestConstants.Headers.X_CHANNEL);
        }

        if (!missingHeaders.isEmpty()) {
            sendMissingHeadersErrorResponse(response, missingHeaders);
            return false;
        }

        if (!isValidUuid(correlationId)) {
            sendInvalidUuidErrorResponse(response, correlationId);
            return false;
        }

        return true;
    }

    private static boolean isValidUuid(final String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException _) {
            return false;
        }
    }

    private void sendInvalidUuidErrorResponse(final HttpServletResponse response, final String invalidValue)
            throws IOException {
        final var errorResponse = new InvalidValuesException("Header '%s' must be a valid UUID format. Received: '%s'"
                .formatted(RequestConstants.Headers.X_CORRELATION_ID, invalidValue));

        errorResponse.executeLogging();

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(businessExceptionMapper.toDto(errorResponse)));
    }

    private void sendMissingHeadersErrorResponse(final HttpServletResponse response, final List<String> missingHeaders)
            throws IOException {
        final var errorResponse =
                new InvalidValuesException("Missing headers. Headers: %s are required".formatted(missingHeaders));

        errorResponse.executeLogging();

        response.setStatus(errorResponse.getHttpStatusCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(businessExceptionMapper.toDto(errorResponse)));
    }

    private List<String> getApiDocsPaths() {
        final String path = springDocConfigProperties.getApiDocs().getPath();
        return List.of(path, path + WILDCARD_PATH);
    }

    private List<String> getSwaggerUiPaths() {
        final String path = swaggerUiConfigProperties.getPath();
        return List.of(path, "/swagger-ui" + WILDCARD_PATH);
    }

    private List<String> getMcpServerPaths() {
        final String sseMessageEndpoint = mcpServerProperties.getSseMessageEndpoint();
        final String sseEndpoint = mcpServerProperties.getSseEndpoint();

        return List.of(
                sseMessageEndpoint, sseMessageEndpoint + WILDCARD_PATH, sseEndpoint, sseEndpoint + WILDCARD_PATH);
    }
}
