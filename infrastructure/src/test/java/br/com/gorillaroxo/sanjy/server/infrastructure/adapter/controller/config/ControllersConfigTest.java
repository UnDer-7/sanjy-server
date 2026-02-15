package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller.config;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.gorillaroxo.sanjy.server.core.ports.driven.SanjyServerProps;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;

@ExtendWith(MockitoExtension.class)
class ControllersConfigTest {

    @Mock
    private SanjyServerProps serverProps;

    @Mock
    private SanjyServerProps.ApplicationProp applicationProp;

    @Mock
    private PathMatchConfigurer pathMatchConfigurer;

    @InjectMocks
    private ControllersConfig controllersConfig;

    @Test
    void shouldAddPathPrefixWhenEndpointsPrefixIsConfigured() {
        // Given
        when(serverProps.application()).thenReturn(applicationProp);
        when(applicationProp.endpointsPrefix()).thenReturn("/api/v1");

        // When
        controllersConfig.configurePathMatch(pathMatchConfigurer);

        // Then
        var prefixCaptor = ArgumentCaptor.forClass(String.class);
        var predicateCaptor = ArgumentCaptor.forClass(HandlerTypePredicate.class);
        verify(pathMatchConfigurer).addPathPrefix(prefixCaptor.capture(), predicateCaptor.capture());

        assertThat(prefixCaptor.getValue()).isEqualTo("/api/v1");
        assertThat(predicateCaptor.getValue()).isNotNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldNotAddPathPrefixWhenEndpointsPrefixIsNullEmptyOrBlank(final String prefix) {
        // Given
        when(serverProps.application()).thenReturn(applicationProp);
        when(applicationProp.endpointsPrefix()).thenReturn(prefix);

        // When
        controllersConfig.configurePathMatch(pathMatchConfigurer);

        // Then
        verify(pathMatchConfigurer, never()).addPathPrefix(any(), any());
    }
}
