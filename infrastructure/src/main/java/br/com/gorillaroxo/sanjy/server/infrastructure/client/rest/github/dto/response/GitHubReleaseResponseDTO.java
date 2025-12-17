package br.com.gorillaroxo.sanjy.server.infrastructure.client.rest.github.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GitHubReleaseResponseDTO(String url, String assetsUrl, String uploadUrl, Long id, String tagName) {}
