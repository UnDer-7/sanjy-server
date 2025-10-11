package br.com.gorillaroxo.sanjy.infrastructure.mapper;

import br.com.gorillaroxo.sanjy.core.domain.MealTypeDomain;
import br.com.gorillaroxo.sanjy.entrypoint.dto.respose.MealTypeResponseDTO;
import br.com.gorillaroxo.sanjy.infrastructure.jpa.entity.MealTypeEntity;
import br.com.gorillaroxo.sanjy.infrastructure.utils.ConstantsInfrastructure;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(
    componentModel = ConstantsInfrastructure.MAPSTRUCT_COMPONENT_MODEL,
    uses = StandardOptionMapper.class
)
public interface MealTypeMapper {

    // ResponseDTOs
    MealTypeResponseDTO toDTO(final MealTypeDomain domain);

    List<MealTypeResponseDTO> toDTO(final List<MealTypeDomain> domain);

    Set<MealTypeResponseDTO> toDTO(final Set<MealTypeDomain> domain);

    // Entities
    @Mapping(target = "dietPlanId", source = "dietPlan.id")
    MealTypeDomain toDomain(final MealTypeEntity dto);

    List<MealTypeDomain> toDomain(final List<MealTypeEntity> dto);

    Set<MealTypeDomain> toDomain(final Set<MealTypeEntity> dto);

    @Mapping(target = "dietPlan", ignore = true)
    MealTypeEntity toEntity(final MealTypeDomain domain);

    List<MealTypeEntity> toEntity(final List<MealTypeDomain> domain);

    Set<MealTypeEntity> toEntity(final Set<MealTypeDomain> domain);
}
