package br.com.gorillaroxo.sanjy.server.infrastructure.mapper;

import br.com.gorillaroxo.sanjy.server.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateDietPlanRequestDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.DietPlanCompleteResponseDTO;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.DietPlanEntity;
import br.com.gorillaroxo.sanjy.server.infrastructure.utils.ConstantsInfrastructure;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = ConstantsInfrastructure.MAPSTRUCT_COMPONENT_MODEL,
    uses = MealTypeMapper.class,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface DietPlanMapper {

    // DTOs
    DietPlanCompleteResponseDTO toDTO(final DietPlanDomain domain);

    // Domains
    DietPlanDomain toDomain(final DietPlanEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    DietPlanDomain toDomain(CreateDietPlanRequestDTO request);

    // Entities
    DietPlanEntity toEntity(final DietPlanDomain domain);
}
