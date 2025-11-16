package br.com.gorillaroxo.sanjy.server.infrastructure.config;

import br.com.gorillaroxo.sanjy.server.core.domain.LogField;
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
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RequestLoggingFilterConfig extends OncePerRequestFilter {

    private static final Set<String> IGNORE_PATHS = Set.of(
            // SWAGGER
            "/",
            "/**/api-docs",
            "/**/api-docs/**",
            "/swagger-ui/**",
            "/**/swagger-resources/**",
            // ACTUATOR
            "/actuator",
            "/actuator/**",
            // MCP
            "/sse",
            "/sse/**",
            "/mcp",
            "/mcp/**");

    private final ObjectMapper objectMapper;
    private final BusinessExceptionMapper businessExceptionMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean shouldNotFilter(final HttpServletRequest request) {
        final String requestPath = request.getRequestURI();
        return IGNORE_PATHS.stream().anyMatch(ignoredPath -> pathMatcher.match(ignoredPath, requestPath));
    }

    @Override
    public void doFilterInternal(
            final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {
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
            return;
        }

        if (!isValidUuid(correlationId)) {
            sendInvalidUuidErrorResponse(response, correlationId);
            return;
        }

        putHeadersInMdc(request, correlationId, channel);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private static boolean isValidUuid(final String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static void putHeadersInMdc(
            final HttpServletRequest request, final String correlationId, final String channel) {
        MDC.put(LogField.TRANSACTION_ID.label(), UUID.randomUUID().toString());
        MDC.put(LogField.HTTP_REQUEST.label(), "%s %s".formatted(request.getMethod(), request.getRequestURI()));
        MDC.put(LogField.CORRELATION_ID.label(), correlationId);
        MDC.put(LogField.CHANNEL.label(), channel);
    }

    private void sendMissingHeadersErrorResponse(final HttpServletResponse response, final List<String> missingHeaders)
            throws IOException {
        final var errorResponse =
                new InvalidValuesException("Missing headers. Headers: %s are required".formatted(missingHeaders));

        response.setStatus(errorResponse.getHttpStatusCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(businessExceptionMapper.toDTO(errorResponse)));
    }

    private void sendInvalidUuidErrorResponse(final HttpServletResponse response, final String invalidValue)
            throws IOException {
        final var errorResponse = new InvalidValuesException("Header '%s' must be a valid UUID format. Received: '%s'"
                .formatted(RequestConstants.Headers.X_CORRELATION_ID, invalidValue));

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(businessExceptionMapper.toDTO(errorResponse)));
    }
}
