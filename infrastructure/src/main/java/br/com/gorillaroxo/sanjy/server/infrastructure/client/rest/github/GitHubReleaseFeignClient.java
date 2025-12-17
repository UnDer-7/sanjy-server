package br.com.gorillaroxo.sanjy.server.infrastructure.client.rest.github;

import br.com.gorillaroxo.sanjy.server.infrastructure.client.rest.github.dto.response.GitHubReleaseResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        value = "GitHubReposFeignClient",
        url = "${sanjy-server.external-http-clients.github.url}",
        path = "/repos/UnDer-7")
public interface GitHubReleaseFeignClient {

    @GetMapping("/{repo}/releases/latest")
    GitHubReleaseResponseDTO getLatestRelease(@PathVariable("repo") String repo);
}
