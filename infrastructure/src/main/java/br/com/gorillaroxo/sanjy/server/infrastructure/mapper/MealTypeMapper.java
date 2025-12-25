package br.com.gorillaroxo.sanjy.server.infrastructure.mapper;

import br.com.gorillaroxo.sanjy.server.core.domain.MealTypeDomain;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateMealTypesRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealTypeResponseDto;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.MealTypeEntity;
import br.com.gorillaroxo.sanjy.server.infrastructure.utils.ConstantsInfrastructure;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = ConstantsInfrastructure.MAPSTRUCT_COMPONENT_MODEL,
        uses = StandardOptionMapper.class,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MealTypeMapper {

    // DTOs
    MealTypeResponseDto toDto(MealTypeDomain domain);

    List<MealTypeResponseDto> toDto(List<MealTypeDomain> domain);

    // Domains
    @Mapping(target = "dietPlanId", source = "dietPlan.id")
    MealTypeDomain toDomain(MealTypeEntity dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dietPlanId", ignore = true)
    @Mapping(target = "metadata.createdAt", ignore = true)
    @Mapping(target = "metadata.updatedAt", ignore = true)
    MealTypeDomain toDomain(CreateMealTypesRequestDto dto);

    List<MealTypeDomain> toDomainListFromMealTypeEntity(List<MealTypeEntity> dto);

    List<MealTypeDomain> toDomainListFromCreateMealTypesRequestDto(List<CreateMealTypesRequestDto> dto);

    // Entities
    @Mapping(target = "dietPlan", ignore = true)
    MealTypeEntity toEntity(MealTypeDomain domain);

    Set<MealTypeEntity> toEntity(List<MealTypeDomain> domain);
}
