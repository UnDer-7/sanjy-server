package br.com.gorillaroxo.sanjy.server.core.ports.driven;

import br.com.gorillaroxo.sanjy.server.core.domain.github.GitHubReleaseDomain;

public interface GitHubGateway {

    GitHubReleaseDomain getLatestRelease(String repository);
}
