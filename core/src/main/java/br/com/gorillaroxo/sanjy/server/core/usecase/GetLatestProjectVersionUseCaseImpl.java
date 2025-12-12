package br.com.gorillaroxo.sanjy.server.core.usecase;

import br.com.gorillaroxo.sanjy.server.core.ports.driven.GitHubGateway;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.SanjyServerProps;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.GetLatestProjectVersionUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class GetLatestProjectVersionUseCaseImpl implements GetLatestProjectVersionUseCase {

    private final GitHubGateway gitHubGateway;
    private final SanjyServerProps sanjyServerProps;

    @Override
    public String execute() {
        return gitHubGateway.getLatestRelease(sanjyServerProps.application().name()).tagName();
    }

}
