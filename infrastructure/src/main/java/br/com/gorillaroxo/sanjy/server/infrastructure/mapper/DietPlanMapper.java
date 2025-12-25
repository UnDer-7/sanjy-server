package br.com.gorillaroxo.sanjy.server.infrastructure.mapper;

import br.com.gorillaroxo.sanjy.server.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateDietPlanRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.DietPlanCompleteResponseDto;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.DietPlanEntity;
import br.com.gorillaroxo.sanjy.server.infrastructure.utils.ConstantsInfrastructure;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = ConstantsInfrastructure.MAPSTRUCT_COMPONENT_MODEL,
        uses = MealTypeMapper.class,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DietPlanMapper {

    // DTOs
    DietPlanCompleteResponseDto toDto(DietPlanDomain domain);

    // Domains
    DietPlanDomain toDomain(DietPlanEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "metadata.createdAt", ignore = true)
    @Mapping(target = "metadata.updatedAt", ignore = true)
    DietPlanDomain toDomain(CreateDietPlanRequestDto request);

    // Entities
    DietPlanEntity toEntity(DietPlanDomain domain);
}
