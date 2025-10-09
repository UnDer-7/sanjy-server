package br.com.gorillaroxo.sanjy.infrastructure.mapper;

import br.com.gorillaroxo.sanjy.core.domain.plan.DietPlanDomain;
import br.com.gorillaroxo.sanjy.infrastructure.jpa.entity.DietPlanEntity;
import br.com.gorillaroxo.sanjy.infrastructure.utils.ConstantsInfrastructure;
import org.mapstruct.Mapper;

@Mapper(componentModel = ConstantsInfrastructure.MAPSTRUCT_COMPONENT_MODEL)
public interface DietPlanMapper {

    DietPlanEntity toEntity(final DietPlanDomain domain);

    DietPlanDomain toDomain(final DietPlanEntity entity);
}
