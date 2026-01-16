package br.com.gorillaroxo.sanjy.server.infrastructure.config;

import br.com.gorillaroxo.sanjy.server.core.domain.LogField;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.SanjyServerProps;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.GetLatestProjectVersionUseCase;
import br.com.gorillaroxo.sanjy.server.core.util.ThreadUtils;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.repository.GetDatabaseTimeZoneRepository;
import java.time.ZoneId;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ProjectInfoLoggerConfig implements ApplicationListener<ApplicationReadyEvent> {

    private static final String NATIVE_IMAGE_PROPERTY = "org.graalvm.nativeimage.imagecode";
    private static final String RUNTIME_MODE_NATIVE = "Native";
    private static final String RUNTIME_MODE_JVM = "JVM";

    @Qualifier("applicationTaskExecutor")
    private final TaskExecutor taskExecutor;

    private final SanjyServerProps sanjyServerProps;
    private final GetLatestProjectVersionUseCase getLatestProjectVersionUseCase;
    private final GetDatabaseTimeZoneRepository getDatabaseTimeZoneRepository;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent ignored) {
        ThreadUtils.runAsyncWithMdc(
                () -> {
                    final String runtimeMode = detectRuntimeMode();
                    final String latestVersion = fetchLatestVersionFromGitHub();
                    final String databaseTimezone = fetchDatabaseTimeZone();
                    final SanjyServerProps.ApplicationProp application = sanjyServerProps.application();

                    log.info(
                            LogField.Placeholders.SEVEN.getPlaceholder(),
                            StructuredArguments.kv(LogField.MSG.label(), "Project information"),
                            StructuredArguments.kv(LogField.PROJECT_NAME.label(), application.name()),
                            StructuredArguments.kv(LogField.PROJECT_CURRENT_VERSION.label(), application.version()),
                            StructuredArguments.kv(LogField.PROJECT_LATEST_VERSION.label(), latestVersion),
                            StructuredArguments.kv(LogField.RUNTIME_MODE.label(), runtimeMode),
                            StructuredArguments.kv(LogField.APPLICATION_TIMEZONE.label(), ZoneId.systemDefault()),
                            StructuredArguments.kv(LogField.DATABASE_TIMEZONE.label(), databaseTimezone));
                },
                taskExecutor);
    }

    private static String detectRuntimeMode() {
        final String nativeImageProperty = System.getProperty(NATIVE_IMAGE_PROPERTY);
        return nativeImageProperty != null ? RUNTIME_MODE_NATIVE : RUNTIME_MODE_JVM;
    }

    private String fetchLatestVersionFromGitHub() {
        final var unknown = "unknown";

        try {
            return Optional.ofNullable(getLatestProjectVersionUseCase.execute())
                    .filter(Predicate.not(String::isBlank))
                    .orElse(unknown);
        } catch (final Exception e) {
            log.warn(
                    LogField.Placeholders.TWO.getPlaceholder(),
                    StructuredArguments.kv(LogField.MSG.label(), "Error fetching latest version from GitHub"),
                    StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), e.getMessage()),
                    e);
            return unknown;
        }
    }

    private String fetchDatabaseTimeZone() {
        final var unknown = "unknown";
        try {
            return getDatabaseTimeZoneRepository.getDatabaseTimeZone();
        } catch (final Exception e) {
            log.warn(
                    LogField.Placeholders.TWO.getPlaceholder(),
                    StructuredArguments.kv(LogField.MSG.label(), "Error fetching database timezone"),
                    StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), e.getMessage()),
                    e);
            return unknown;
        }
    }
}
