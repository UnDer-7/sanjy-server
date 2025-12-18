package br.com.gorillaroxo.sanjy.server.core.domain.github;

import lombok.Builder;

@Builder(toBuilder = true)
public record GitHubReleaseDomain(String url, String assetsUrl, String uploadUrl, Long id, String tagName) {}
