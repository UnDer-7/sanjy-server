package br.com.gorillaroxo.sanjy.server.infrastructure.config;

import br.com.gorillaroxo.sanjy.server.core.ProjectInfoDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.LogField;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.SanjyServerProps;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.ProjectInfoUseCase;
import br.com.gorillaroxo.sanjy.server.core.util.ThreadUtils;
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

    @Qualifier("applicationTaskExecutor")
    private final TaskExecutor taskExecutor;

    private final ProjectInfoUseCase projectInfoUseCase;
    private final SanjyServerProps sanjyServerProps;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent ignored) {
        ThreadUtils.runAsyncWithMdc(
                () -> {
                    final var unknown = "unknown";
                    final Optional<ProjectInfoDomain> projectInfoOpt = Optional.of(projectInfoUseCase.execute());

                    final Optional<ProjectInfoDomain.Version> versionOpt =
                            projectInfoOpt.map(ProjectInfoDomain::version);
                    final Optional<ProjectInfoDomain.Timezone> timezoneOpt =
                            projectInfoOpt.map(ProjectInfoDomain::timezone);

                    final var runtimeMode = projectInfoOpt
                            .map(ProjectInfoDomain::runtimeMode)
                            .filter(Predicate.not(String::isBlank))
                            .orElse(unknown);
                    final var latestVersion = versionOpt
                            .map(ProjectInfoDomain.Version::latest)
                            .filter(Predicate.not(String::isBlank))
                            .orElse(unknown);
                    final var currentVersion = versionOpt
                            .map(ProjectInfoDomain.Version::current)
                            .filter(Predicate.not(String::isBlank))
                            .orElse(unknown);
                    final var databaseTimezone = timezoneOpt
                            .map(ProjectInfoDomain.Timezone::database)
                            .filter(Predicate.not(String::isBlank))
                            .orElse(unknown);
                    final var applicationTimezone = timezoneOpt
                            .map(ProjectInfoDomain.Timezone::database)
                            .filter(String::isBlank)
                            .orElse(unknown);

                    log.info(
                            LogField.Placeholders.SEVEN.getPlaceholder(),
                            StructuredArguments.kv(LogField.MSG.label(), "Project information"),
                            StructuredArguments.kv(
                                    LogField.PROJECT_NAME.label(),
                                    sanjyServerProps.application().name()),
                            StructuredArguments.kv(LogField.PROJECT_CURRENT_VERSION.label(), currentVersion),
                            StructuredArguments.kv(LogField.PROJECT_LATEST_VERSION.label(), latestVersion),
                            StructuredArguments.kv(LogField.RUNTIME_MODE.label(), runtimeMode),
                            StructuredArguments.kv(LogField.APPLICATION_TIMEZONE.label(), applicationTimezone),
                            StructuredArguments.kv(LogField.DATABASE_TIMEZONE.label(), databaseTimezone));
                },
                taskExecutor);
    }
}
