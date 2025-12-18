package br.com.gorillaroxo.sanjy.server.core.usecase;

import br.com.gorillaroxo.sanjy.server.core.domain.LogField;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.GitHubGateway;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.SanjyServerProps;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.GetLatestProjectVersionUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class GetLatestProjectVersionUseCaseImpl implements GetLatestProjectVersionUseCase {

    private final GitHubGateway gitHubGateway;
    private final SanjyServerProps sanjyServerProps;

    @Override
    public String execute() {
        log.info(
                LogField.Placeholders.ONE.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Getting project latest release version"));

        final String latestVersion = gitHubGateway
                .getLatestRelease(sanjyServerProps.application().name())
                .tagName();

        log.info(
                LogField.Placeholders.TWO.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Successfully got project latest release version"),
                StructuredArguments.kv(LogField.PROJECT_LATEST_VERSION.label(), latestVersion));

        return latestVersion;
    }
}
