package br.com.gorillaroxo.sanjy.server.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.gorillaroxo.sanjy.server.core.ports.driven.SanjyServerProps;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.GetLatestProjectVersionUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.core.task.SyncTaskExecutor;

@ExtendWith(MockitoExtension.class)
class ProjectInfoLoggerConfigTest {

    @Test
    void should_log_project_info_when_application_is_ready() {
        // Given
        final var taskExecutor = new SyncTaskExecutor();
        final var sanjyServerProps = mock(SanjyServerProps.class);
        final var applicationProp = mock(SanjyServerProps.ApplicationProp.class);
        final var getLatestProjectVersionUseCase = mock(GetLatestProjectVersionUseCase.class);

        when(sanjyServerProps.application()).thenReturn(applicationProp);
        when(applicationProp.version()).thenReturn("1.0.0");
        when(getLatestProjectVersionUseCase.execute()).thenReturn("1.2.0");

        final var config = new ProjectInfoLoggerConfig(taskExecutor, sanjyServerProps, getLatestProjectVersionUseCase);
        final var event = mock(ApplicationReadyEvent.class);

        // When & Then
        assertThatCode(() -> config.onApplicationEvent(event)).doesNotThrowAnyException();

        verify(getLatestProjectVersionUseCase).execute();
    }

    @Test
    void should_handle_null_version_from_use_case() {
        // Given
        final var taskExecutor = new SyncTaskExecutor();
        final var sanjyServerProps = mock(SanjyServerProps.class);
        final var applicationProp = mock(SanjyServerProps.ApplicationProp.class);
        final var getLatestProjectVersionUseCase = mock(GetLatestProjectVersionUseCase.class);

        when(sanjyServerProps.application()).thenReturn(applicationProp);
        when(applicationProp.version()).thenReturn("1.0.0");
        when(getLatestProjectVersionUseCase.execute()).thenReturn(null);

        final var config = new ProjectInfoLoggerConfig(taskExecutor, sanjyServerProps, getLatestProjectVersionUseCase);
        final var event = mock(ApplicationReadyEvent.class);

        // When & Then
        assertThatCode(() -> config.onApplicationEvent(event)).doesNotThrowAnyException();
    }

    @Test
    void should_handle_blank_version_from_use_case() {
        // Given
        final var taskExecutor = new SyncTaskExecutor();
        final var sanjyServerProps = mock(SanjyServerProps.class);
        final var applicationProp = mock(SanjyServerProps.ApplicationProp.class);
        final var getLatestProjectVersionUseCase = mock(GetLatestProjectVersionUseCase.class);

        when(sanjyServerProps.application()).thenReturn(applicationProp);
        when(applicationProp.version()).thenReturn("1.0.0");
        when(getLatestProjectVersionUseCase.execute()).thenReturn("   ");

        final var config = new ProjectInfoLoggerConfig(taskExecutor, sanjyServerProps, getLatestProjectVersionUseCase);
        final var event = mock(ApplicationReadyEvent.class);

        // When & Then
        assertThatCode(() -> config.onApplicationEvent(event)).doesNotThrowAnyException();
    }

    @Test
    void should_handle_exception_from_use_case_gracefully() {
        // Given
        final var taskExecutor = new SyncTaskExecutor();
        final var sanjyServerProps = mock(SanjyServerProps.class);
        final var applicationProp = mock(SanjyServerProps.ApplicationProp.class);
        final var getLatestProjectVersionUseCase = mock(GetLatestProjectVersionUseCase.class);

        when(sanjyServerProps.application()).thenReturn(applicationProp);
        when(applicationProp.version()).thenReturn("1.0.0");
        when(getLatestProjectVersionUseCase.execute()).thenThrow(new RuntimeException("GitHub API error"));

        final var config = new ProjectInfoLoggerConfig(taskExecutor, sanjyServerProps, getLatestProjectVersionUseCase);
        final var event = mock(ApplicationReadyEvent.class);

        // When & Then
        assertThatCode(() -> config.onApplicationEvent(event)).doesNotThrowAnyException();
    }

    @Test
    void should_detect_jvm_runtime_mode_when_native_property_not_set() {
        // Given
        System.clearProperty("org.graalvm.nativeimage.imagecode");

        final var taskExecutor = new SyncTaskExecutor();
        final var sanjyServerProps = mock(SanjyServerProps.class);
        final var applicationProp = mock(SanjyServerProps.ApplicationProp.class);
        final var getLatestProjectVersionUseCase = mock(GetLatestProjectVersionUseCase.class);

        when(sanjyServerProps.application()).thenReturn(applicationProp);
        when(applicationProp.version()).thenReturn("1.0.0");
        when(getLatestProjectVersionUseCase.execute()).thenReturn("1.2.0");

        final var config = new ProjectInfoLoggerConfig(taskExecutor, sanjyServerProps, getLatestProjectVersionUseCase);
        final var event = mock(ApplicationReadyEvent.class);

        // When & Then
        assertThatCode(() -> config.onApplicationEvent(event)).doesNotThrowAnyException();
    }

    @Test
    void should_detect_native_runtime_mode_when_native_property_is_set() {
        // Given
        System.setProperty("org.graalvm.nativeimage.imagecode", "runtime");

        final var taskExecutor = new SyncTaskExecutor();
        final var sanjyServerProps = mock(SanjyServerProps.class);
        final var applicationProp = mock(SanjyServerProps.ApplicationProp.class);
        final var getLatestProjectVersionUseCase = mock(GetLatestProjectVersionUseCase.class);

        when(sanjyServerProps.application()).thenReturn(applicationProp);
        when(applicationProp.version()).thenReturn("1.0.0");
        when(getLatestProjectVersionUseCase.execute()).thenReturn("1.2.0");

        final var config = new ProjectInfoLoggerConfig(taskExecutor, sanjyServerProps, getLatestProjectVersionUseCase);
        final var event = mock(ApplicationReadyEvent.class);

        try {
            // When & Then
            assertThatCode(() -> config.onApplicationEvent(event)).doesNotThrowAnyException();
        } finally {
            // Cleanup
            System.clearProperty("org.graalvm.nativeimage.imagecode");
        }
    }

    @Test
    void should_execute_with_provided_task_executor() {
        // Given
        final var taskExecutor = new SyncTaskExecutor();
        final var sanjyServerProps = mock(SanjyServerProps.class);
        final var applicationProp = mock(SanjyServerProps.ApplicationProp.class);
        final var getLatestProjectVersionUseCase = mock(GetLatestProjectVersionUseCase.class);

        when(sanjyServerProps.application()).thenReturn(applicationProp);
        when(applicationProp.version()).thenReturn("2.1.0");
        when(getLatestProjectVersionUseCase.execute()).thenReturn("2.2.0");

        final var config = new ProjectInfoLoggerConfig(taskExecutor, sanjyServerProps, getLatestProjectVersionUseCase);
        final var event = mock(ApplicationReadyEvent.class);

        // When
        config.onApplicationEvent(event);

        // Then - verify use case was called (means task was executed)
        verify(getLatestProjectVersionUseCase).execute();
        verify(sanjyServerProps).application();
        verify(applicationProp).version();
    }

    @Test
    void should_handle_empty_string_version_from_use_case() {
        // Given
        final var taskExecutor = new SyncTaskExecutor();
        final var sanjyServerProps = mock(SanjyServerProps.class);
        final var applicationProp = mock(SanjyServerProps.ApplicationProp.class);
        final var getLatestProjectVersionUseCase = mock(GetLatestProjectVersionUseCase.class);

        when(sanjyServerProps.application()).thenReturn(applicationProp);
        when(applicationProp.version()).thenReturn("1.0.0");
        when(getLatestProjectVersionUseCase.execute()).thenReturn("");

        final var config = new ProjectInfoLoggerConfig(taskExecutor, sanjyServerProps, getLatestProjectVersionUseCase);
        final var event = mock(ApplicationReadyEvent.class);

        // When & Then
        assertThatCode(() -> config.onApplicationEvent(event)).doesNotThrowAnyException();
    }

    @Test
    void should_log_current_version_from_properties() {
        // Given
        final var taskExecutor = new SyncTaskExecutor();
        final var sanjyServerProps = mock(SanjyServerProps.class);
        final var applicationProp = mock(SanjyServerProps.ApplicationProp.class);
        final var getLatestProjectVersionUseCase = mock(GetLatestProjectVersionUseCase.class);

        when(sanjyServerProps.application()).thenReturn(applicationProp);
        when(applicationProp.version()).thenReturn("3.0.0-SNAPSHOT");
        when(getLatestProjectVersionUseCase.execute()).thenReturn("2.9.5");

        final var config = new ProjectInfoLoggerConfig(taskExecutor, sanjyServerProps, getLatestProjectVersionUseCase);
        final var event = mock(ApplicationReadyEvent.class);

        // When
        config.onApplicationEvent(event);

        // Then
        verify(applicationProp).version();
    }
}
