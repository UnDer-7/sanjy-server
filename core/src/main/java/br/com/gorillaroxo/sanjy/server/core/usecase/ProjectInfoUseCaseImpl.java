package br.com.gorillaroxo.sanjy.server.core.usecase;

import br.com.gorillaroxo.sanjy.server.core.ProjectInfoDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.LogField;
import br.com.gorillaroxo.sanjy.server.core.domain.github.GitHubReleaseDomain;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.DatabaseTimeZoneGateway;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.GitHubGateway;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.SanjyServerProps;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.SemanticVersioningComparatorGateway;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.ProjectInfoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
class ProjectInfoUseCaseImpl implements ProjectInfoUseCase {

    private static final String NATIVE_IMAGE_PROPERTY = "org.graalvm.nativeimage.imagecode";
    private static final String RUNTIME_MODE_NATIVE = "Native";
    private static final String RUNTIME_MODE_JVM = "JVM";

    private final SanjyServerProps sanjyServerProps;
    private final GitHubGateway gitHubGateway;
    private final DatabaseTimeZoneGateway databaseTimeZoneGateway;
    private final SemanticVersioningComparatorGateway semanticVersioningComparatorGateway;

    @Override
    public ProjectInfoDomain execute() {
        final var databaseTimezone = fetchDatabaseTimeZone();
        final var runtimeMode = detectRuntimeMode();
        final var version = buildVersion();

        return ProjectInfoDomain.builder()
            .runtimeMode(runtimeMode)
            .version(version)
            .timezone(ProjectInfoDomain.Timezone.builder()
                .application(ZoneId.systemDefault().toString())
                .database(databaseTimezone)
                .build())
            .build();
    }

    private ProjectInfoDomain.Version buildVersion() {
        final var currentVersion = sanjyServerProps.application().version();
        final var latestVersion = fetchLatestVersionFromGitHub();

        final BooleanSupplier getIsLatest = () -> {
            if (Objects.isNull(latestVersion)) {
                return true;
            }

            final var comparatorResult = semanticVersioningComparatorGateway.compare(currentVersion, latestVersion);
            return comparatorResult >= 0;
        };

        return ProjectInfoDomain.Version.builder()
            .current(currentVersion)
            .latest(latestVersion)
            .isLatest(getIsLatest.getAsBoolean())
            .build();
    }

    private String fetchLatestVersionFromGitHub() {
        try {
            final GitHubReleaseDomain latestRelease = gitHubGateway.getLatestRelease(sanjyServerProps.application().name());
            return Optional.ofNullable(latestRelease.tagName())
                .filter(Predicate.not(String::isBlank))
                .orElse(null);
        } catch (final Exception e) {
            log.warn(
                LogField.Placeholders.TWO.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Error fetching latest version from GitHub"),
                StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), e.getMessage()),
                e);
            return null;
        }
    }

    private String fetchDatabaseTimeZone() {
        try {
            return databaseTimeZoneGateway.get();
        } catch (final Exception e) {
            log.warn(
                LogField.Placeholders.TWO.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Error fetching database timezone"),
                StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), e.getMessage()),
                e);
            return null;
        }
    }

    private static String detectRuntimeMode() {
        final String nativeImageProperty = System.getProperty(NATIVE_IMAGE_PROPERTY);
        return nativeImageProperty != null ? RUNTIME_MODE_NATIVE : RUNTIME_MODE_JVM;
    }
}
