package br.com.gorillaroxo.sanjy.server.infrastructure.mapper;

import br.com.gorillaroxo.sanjy.server.core.domain.StandardOptionDomain;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateStandardOptionRequestDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.StandardOptionResponseDTO;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.StandardOptionEntity;
import br.com.gorillaroxo.sanjy.server.infrastructure.utils.ConstantsInfrastructure;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

@Mapper(
    componentModel = ConstantsInfrastructure.MAPSTRUCT_COMPONENT_MODEL,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface StandardOptionMapper {

    // DTOs
    StandardOptionResponseDTO toDTO(StandardOptionDomain domain);
    List<StandardOptionResponseDTO> toDTO(List<StandardOptionDomain> domain);

    // Domains
    @Mapping(target = "mealTypeId", source = "mealType.id")
    StandardOptionDomain toDomain(StandardOptionEntity entity);
    List<StandardOptionDomain> toDomainListFromStandardOptionEntity(List<StandardOptionEntity> entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mealTypeId", ignore = true)
    StandardOptionDomain toDomain(CreateStandardOptionRequestDTO dto);
    List<StandardOptionDomain> toDomainListFromStandardOptionDomain(List<CreateStandardOptionRequestDTO> dto);

    // Entities
    @Mapping(target = "mealType", ignore = true)
    StandardOptionEntity toEntity(StandardOptionDomain domain);
    List<StandardOptionEntity> toEntity(List<StandardOptionDomain> domain);
}
