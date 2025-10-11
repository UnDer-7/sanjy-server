package br.com.gorillaroxo.sanjy.infrastructure.mapper;

import br.com.gorillaroxo.sanjy.core.domain.StandardOptionDomain;
import br.com.gorillaroxo.sanjy.entrypoint.dto.respose.StandardOptionResponseDTO;
import br.com.gorillaroxo.sanjy.infrastructure.jpa.entity.StandardOptionEntity;
import br.com.gorillaroxo.sanjy.infrastructure.utils.ConstantsInfrastructure;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(
    componentModel = ConstantsInfrastructure.MAPSTRUCT_COMPONENT_MODEL
)
public interface StandardOptionMapper {

    // ResponseDTOs
    StandardOptionResponseDTO toDTO(StandardOptionDomain domain);

    List<StandardOptionResponseDTO> toDTO(List<StandardOptionDomain> domain);

    Set<StandardOptionResponseDTO> toDTO(Set<StandardOptionDomain> domain);

    // Entities
    @Mapping(target = "mealTypeId", source = "mealType.id")
    StandardOptionDomain toDomain(StandardOptionEntity entity);

    List<StandardOptionDomain> toDomain(List<StandardOptionEntity> entity);

    Set<StandardOptionDomain> toDomain(Set<StandardOptionEntity> entity);

    @Mapping(target = "mealType", ignore = true)
    StandardOptionEntity toEntity(StandardOptionDomain domain);

    List<StandardOptionEntity> toEntity(List<StandardOptionDomain> domain);

    Set<StandardOptionEntity> toEntity(Set<StandardOptionDomain> domain);
}
