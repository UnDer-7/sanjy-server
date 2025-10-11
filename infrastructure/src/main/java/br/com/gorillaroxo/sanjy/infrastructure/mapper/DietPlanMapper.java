package br.com.gorillaroxo.sanjy.infrastructure.mapper;

import br.com.gorillaroxo.sanjy.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.entrypoint.dto.respose.DietPlanCompleteResponseDTO;
import br.com.gorillaroxo.sanjy.infrastructure.jpa.entity.DietPlanEntity;
import br.com.gorillaroxo.sanjy.infrastructure.utils.ConstantsInfrastructure;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = ConstantsInfrastructure.MAPSTRUCT_COMPONENT_MODEL,
    uses = MealTypeMapper.class
)
public interface DietPlanMapper {

    // Entity
    DietPlanEntity toEntity(final DietPlanDomain domain);

    DietPlanDomain toDomain(final DietPlanEntity entity);

    // Request/Response DTOs
    DietPlanCompleteResponseDTO toDTO(final DietPlanDomain domain);
}
