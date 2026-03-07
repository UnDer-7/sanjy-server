package br.com.gorillaroxo.sanjy.server.infrastructure.config;

import br.com.gorillaroxo.sanjy.server.core.ProjectInfoDomain;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.SanjyServerProps;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.ProjectInfoUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectInfoLoggerConfigTest {

    @Mock
    ProjectInfoUseCase projectInfoUseCase;

    @Mock
    SanjyServerProps sanjyServerProps;

    @Mock
    SanjyServerProps.ApplicationProp applicationProp;

    @Mock
    ApplicationReadyEvent applicationReadyEvent;

    @Spy
    TaskExecutor taskExecutor = new SyncTaskExecutor();

    @InjectMocks
    ProjectInfoLoggerConfig projectInfoLoggerConfig;

    @BeforeEach
    void setup() {
        lenient().when(sanjyServerProps.application()).thenReturn(applicationProp);
    }

    @Test
    void should_log_project_info_when_application_is_ready() {
        // Given
        when(projectInfoUseCase.execute()).thenReturn(buildFullDomain());

        // When & Then
        assertThatCode(() -> projectInfoLoggerConfig.onApplicationEvent(applicationReadyEvent))
                .doesNotThrowAnyException();

        verify(projectInfoUseCase, times(1)).execute();
    }

    @Nested
    @DisplayName("Testing ProjectInfoUseCase exception handling")
    class TestCaseProjectInfoUseCaseException {

        @Test
        void should_handle_exception_from_use_case_gracefully() {
            // Given
            when(projectInfoUseCase.execute()).thenThrow(new RuntimeException("Use case error"));

            // When & Then
            assertThatCode(() -> projectInfoLoggerConfig.onApplicationEvent(applicationReadyEvent))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Testing null/blank runtimeMode")
    class TestCaseRuntimeMode {

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", "  ", "    "})
        void should_handle_null_or_blank_runtime_mode(final String runtimeMode) {
            // Given
            final var domain = buildDomainWithRuntimeMode(runtimeMode);
            when(projectInfoUseCase.execute()).thenReturn(domain);

            // When & Then
            assertThatCode(() -> projectInfoLoggerConfig.onApplicationEvent(applicationReadyEvent))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Testing null/blank version fields")
    class TestCaseVersionFields {

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", "  ", "    "})
        void should_handle_null_or_blank_latest_version(final String latestVersion) {
            // Given
            final var domain = buildDomainWithVersion("1.0.0", latestVersion);
            when(projectInfoUseCase.execute()).thenReturn(domain);

            // When & Then
            assertThatCode(() -> projectInfoLoggerConfig.onApplicationEvent(applicationReadyEvent))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", "  ", "    "})
        void should_handle_null_or_blank_current_version(final String currentVersion) {
            // Given
            final var domain = buildDomainWithVersion(currentVersion, "1.2.0");
            when(projectInfoUseCase.execute()).thenReturn(domain);

            // When & Then
            assertThatCode(() -> projectInfoLoggerConfig.onApplicationEvent(applicationReadyEvent))
                    .doesNotThrowAnyException();
        }

        @Test
        void should_handle_null_version_record() {
            // Given
            final var domain = ProjectInfoDomain.builder()
                    .runtimeMode("JVM")
                    .version(null)
                    .timezone(ProjectInfoDomain.Timezone.builder()
                            .application("UTC")
                            .database("UTC")
                            .build())
                    .build();
            when(projectInfoUseCase.execute()).thenReturn(domain);

            // When & Then
            assertThatCode(() -> projectInfoLoggerConfig.onApplicationEvent(applicationReadyEvent))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Testing null/blank timezone fields")
    class TestCaseTimezoneFields {

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", "  ", "    "})
        void should_handle_null_or_blank_database_timezone(final String databaseTimezone) {
            // Given
            final var domain = buildDomainWithTimezone("UTC", databaseTimezone);
            when(projectInfoUseCase.execute()).thenReturn(domain);

            // When & Then
            assertThatCode(() -> projectInfoLoggerConfig.onApplicationEvent(applicationReadyEvent))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", "  ", "    "})
        void should_handle_null_or_blank_application_timezone(final String applicationTimezone) {
            // Given
            final var domain = buildDomainWithTimezone(applicationTimezone, "UTC");
            when(projectInfoUseCase.execute()).thenReturn(domain);

            // When & Then
            assertThatCode(() -> projectInfoLoggerConfig.onApplicationEvent(applicationReadyEvent))
                    .doesNotThrowAnyException();
        }

        @Test
        void should_handle_null_timezone_record() {
            // Given
            final var domain = ProjectInfoDomain.builder()
                    .runtimeMode("JVM")
                    .version(ProjectInfoDomain.Version.builder()
                            .current("1.0.0")
                            .latest("1.2.0")
                            .isLatest(false)
                            .build())
                    .timezone(null)
                    .build();
            when(projectInfoUseCase.execute()).thenReturn(domain);

            // When & Then
            assertThatCode(() -> projectInfoLoggerConfig.onApplicationEvent(applicationReadyEvent))
                    .doesNotThrowAnyException();
        }
    }

    private static ProjectInfoDomain buildFullDomain() {
        return ProjectInfoDomain.builder()
                .runtimeMode("JVM")
                .version(ProjectInfoDomain.Version.builder()
                        .current("1.0.0")
                        .latest("1.2.0")
                        .isLatest(false)
                        .build())
                .timezone(ProjectInfoDomain.Timezone.builder()
                        .application("UTC")
                        .database("UTC")
                        .build())
                .build();
    }

    private static ProjectInfoDomain buildDomainWithRuntimeMode(final String runtimeMode) {
        return ProjectInfoDomain.builder()
                .runtimeMode(runtimeMode)
                .version(ProjectInfoDomain.Version.builder()
                        .current("1.0.0")
                        .latest("1.2.0")
                        .isLatest(false)
                        .build())
                .timezone(ProjectInfoDomain.Timezone.builder()
                        .application("UTC")
                        .database("UTC")
                        .build())
                .build();
    }

    private static ProjectInfoDomain buildDomainWithVersion(final String current, final String latest) {
        return ProjectInfoDomain.builder()
                .runtimeMode("JVM")
                .version(ProjectInfoDomain.Version.builder()
                        .current(current)
                        .latest(latest)
                        .isLatest(false)
                        .build())
                .timezone(ProjectInfoDomain.Timezone.builder()
                        .application("UTC")
                        .database("UTC")
                        .build())
                .build();
    }

    private static ProjectInfoDomain buildDomainWithTimezone(final String application, final String database) {
        return ProjectInfoDomain.builder()
                .runtimeMode("JVM")
                .version(ProjectInfoDomain.Version.builder()
                        .current("1.0.0")
                        .latest("1.2.0")
                        .isLatest(false)
                        .build())
                .timezone(ProjectInfoDomain.Timezone.builder()
                        .application(application)
                        .database(database)
                        .build())
                .build();
    }
}
