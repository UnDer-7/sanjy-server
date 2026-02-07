package br.com.gorillaroxo.sanjy.server.infrastructure.mapper;

import br.com.gorillaroxo.sanjy.server.core.ProjectInfoDomain;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ProjectInfoResponseDto;
import br.com.gorillaroxo.sanjy.server.infrastructure.utils.InfrastructureConstants;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = InfrastructureConstants.MAPSTRUCT_COMPONENT_MODEL,
    uses = MealTypeMapper.class,
    unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ProjectInfoMapper {

    ProjectInfoResponseDto toDto(ProjectInfoDomain domain);

}
