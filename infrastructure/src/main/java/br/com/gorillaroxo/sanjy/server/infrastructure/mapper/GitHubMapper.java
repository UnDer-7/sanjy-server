package br.com.gorillaroxo.sanjy.server.infrastructure.mapper;

import br.com.gorillaroxo.sanjy.server.core.domain.github.GitHubReleaseDomain;
import br.com.gorillaroxo.sanjy.server.infrastructure.client.rest.github.dto.response.GitHubReleaseResponseDto;
import br.com.gorillaroxo.sanjy.server.infrastructure.utils.InfrastructureConstants;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = InfrastructureConstants.MAPSTRUCT_COMPONENT_MODEL,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface GitHubMapper {

    // Domains
    GitHubReleaseDomain toDomain(GitHubReleaseResponseDto dto);
}
