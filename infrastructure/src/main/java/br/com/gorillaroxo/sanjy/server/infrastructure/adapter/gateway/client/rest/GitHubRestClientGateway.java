package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.gateway.client.rest;

import br.com.gorillaroxo.sanjy.server.core.domain.github.GitHubReleaseDomain;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.GitHubGateway;
import br.com.gorillaroxo.sanjy.server.infrastructure.client.rest.github.GitHubReleaseFeignClient;
import br.com.gorillaroxo.sanjy.server.infrastructure.client.rest.github.dto.response.GitHubReleaseResponseDTO;
import br.com.gorillaroxo.sanjy.server.infrastructure.mapper.GitHubMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubRestClientGateway implements GitHubGateway {

    private final GitHubReleaseFeignClient gitHubReleaseFeignClient;
    private final GitHubMapper gitHubMapper;

    @Override
    public GitHubReleaseDomain getLatestRelease(final String repository) {
        final GitHubReleaseResponseDTO latestRelease = gitHubReleaseFeignClient.getLatestRelease(repository);
        return gitHubMapper.toDomain(latestRelease);
    }
}
