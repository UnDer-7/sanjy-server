package br.com.gorillaroxo.sanjy.server.infrastructure.mapper;

import br.com.gorillaroxo.sanjy.server.core.domain.StandardOptionDomain;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateStandardOptionRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.StandardOptionResponseDto;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.StandardOptionEntity;
import br.com.gorillaroxo.sanjy.server.infrastructure.utils.ConstantsInfrastructure;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = ConstantsInfrastructure.MAPSTRUCT_COMPONENT_MODEL,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface StandardOptionMapper {

    // DTOs
    StandardOptionResponseDto toDto(StandardOptionDomain domain);

    List<StandardOptionResponseDto> toDto(List<StandardOptionDomain> domain);

    // Domains
    @Mapping(target = "mealTypeId", source = "mealType.id")
    StandardOptionDomain toDomain(StandardOptionEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mealTypeId", ignore = true)
    @Mapping(target = "metadata.createdAt", ignore = true)
    @Mapping(target = "metadata.updatedAt", ignore = true)
    StandardOptionDomain toDomain(CreateStandardOptionRequestDto dto);

    List<StandardOptionDomain> toDomainListFromStandardOptionEntity(List<StandardOptionEntity> entity);

    List<StandardOptionDomain> toDomainListFromStandardOptionDomain(List<CreateStandardOptionRequestDto> dto);

    // Entities
    @Mapping(target = "mealType", ignore = true)
    StandardOptionEntity toEntity(StandardOptionDomain domain);

    List<StandardOptionEntity> toEntity(List<StandardOptionDomain> domain);
}
