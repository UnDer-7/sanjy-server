package br.com.gorillaroxo.sanjy.server.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.gorillaroxo.sanjy.server.core.ports.driven.SanjyServerProps;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.GetLatestProjectVersionUseCase;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.repository.GetDatabaseTimeZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@ExtendWith(MockitoExtension.class)
class ProjectInfoLoggerConfigTest {

    @Mock
    SanjyServerProps sanjyServerProps;

    @Mock
    SanjyServerProps.ApplicationProp applicationProp;

    @Mock
    GetLatestProjectVersionUseCase getLatestProjectVersionUseCase;

    @Mock
    GetDatabaseTimeZoneRepository getDatabaseTimeZoneRepository;

    @Mock
    ApplicationReadyEvent applicationReadyEvent;

    @Spy
    TaskExecutor taskExecutor = new SyncTaskExecutor();

    @InjectMocks
    ProjectInfoLoggerConfig projectInfoLoggerConfig;

    @BeforeEach
    void setup() {
        when(sanjyServerProps.application()).thenReturn(applicationProp);
    }

    @Test
    void should_log_project_info_when_application_is_ready() {
        // Given
        when(applicationProp.version()).thenReturn("1.0.0");
        when(getLatestProjectVersionUseCase.execute()).thenReturn("1.2.0");
        when(getDatabaseTimeZoneRepository.getDatabaseTimeZone()).thenReturn("Asia/Kolkata");

        // When & Then
        assertThatCode(() -> projectInfoLoggerConfig.onApplicationEvent(applicationReadyEvent))
                .doesNotThrowAnyException();

        verify(getLatestProjectVersionUseCase, times(1)).execute();
        verify(getDatabaseTimeZoneRepository, times(1)).getDatabaseTimeZone();
        verify(applicationProp, times(1)).version();
    }

    @Nested
    @DisplayName("Testing GetDatabaseTimeZoneRepository")
    class TestCaseGetDatabaseTimeZoneRepository {
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"", "  ", "    "})
        void should_handle_null_version_from_databaseTimeZoneRepository(final String databaseTimezone) {
            // Given
            when(applicationProp.version()).thenReturn("1.0.0");
            when(getLatestProjectVersionUseCase.execute()).thenReturn("1.1.1");
            when(getDatabaseTimeZoneRepository.getDatabaseTimeZone()).thenReturn(databaseTimezone);

            // When & Then
            assertThatCode(() -> projectInfoLoggerConfig.onApplicationEvent(applicationReadyEvent))
                    .doesNotThrowAnyException();
        }

        @Test
        void should_handle_exception_from_databaseTimeZoneRepository_gracefully() {
            // Given
            when(applicationProp.version()).thenReturn("1.0.0");
            when(getLatestProjectVersionUseCase.execute()).thenReturn("1.1.1");
            when(getDatabaseTimeZoneRepository.getDatabaseTimeZone()).thenThrow(new RuntimeException("Database error"));

            // When & Then
            assertThatCode(() -> projectInfoLoggerConfig.onApplicationEvent(applicationReadyEvent))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Testing GetLatestProjectVersionUseCase")
    class TestCaseGetLatestProjectVersionUseCase {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"", "  ", "    "})
        void should_handle_null_version_from_latestProjectVersionUseCase(final String version) {
            // Given
            when(applicationProp.version()).thenReturn("1.0.0");
            when(getLatestProjectVersionUseCase.execute()).thenReturn(version);
            when(getDatabaseTimeZoneRepository.getDatabaseTimeZone()).thenReturn("Asia/Kolkata");

            // When & Then
            assertThatCode(() -> projectInfoLoggerConfig.onApplicationEvent(applicationReadyEvent))
                    .doesNotThrowAnyException();
        }

        @Test
        void should_handle_exception_from_latestProjectVersionUseCase_gracefully() {
            // Given
            when(applicationProp.version()).thenReturn("1.0.0");
            when(getLatestProjectVersionUseCase.execute()).thenThrow(new RuntimeException("GitHub API error"));
            when(getDatabaseTimeZoneRepository.getDatabaseTimeZone()).thenReturn("Asia/Kolkata");

            // When & Then
            assertThatCode(() -> projectInfoLoggerConfig.onApplicationEvent(applicationReadyEvent))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Testing ProjectRuntime")
    class TestCaseProjectRuntime {
        @Test
        void should_detect_jvm_runtime_mode_when_native_property_not_set() {
            // Given
            System.clearProperty("org.graalvm.nativeimage.imagecode");

            when(applicationProp.version()).thenReturn("1.0.0");
            when(getLatestProjectVersionUseCase.execute()).thenReturn("1.2.0");
            when(getDatabaseTimeZoneRepository.getDatabaseTimeZone()).thenReturn("Asia/Kolkata");

            // When & Then
            assertThatCode(() -> projectInfoLoggerConfig.onApplicationEvent(applicationReadyEvent))
                    .doesNotThrowAnyException();
        }

        @Test
        void should_detect_native_runtime_mode_when_native_property_is_set() {
            // Given
            System.setProperty("org.graalvm.nativeimage.imagecode", "runtime");

            when(sanjyServerProps.application()).thenReturn(applicationProp);
            when(applicationProp.version()).thenReturn("1.0.0");
            when(getLatestProjectVersionUseCase.execute()).thenReturn("1.2.0");
            when(getDatabaseTimeZoneRepository.getDatabaseTimeZone()).thenReturn("Asia/Kolkata");

            try {
                // When & Then
                assertThatCode(() -> projectInfoLoggerConfig.onApplicationEvent(applicationReadyEvent))
                        .doesNotThrowAnyException();
            } finally {
                // Cleanup
                System.clearProperty("org.graalvm.nativeimage.imagecode");
            }
        }
    }
}
