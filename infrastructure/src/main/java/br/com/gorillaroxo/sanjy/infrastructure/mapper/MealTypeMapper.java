package br.com.gorillaroxo.sanjy.infrastructure.mapper;

import br.com.gorillaroxo.sanjy.core.domain.MealTypeDomain;
import br.com.gorillaroxo.sanjy.entrypoint.dto.request.CreateMealTypesRequestDTO;
import br.com.gorillaroxo.sanjy.entrypoint.dto.respose.MealTypeResponseDTO;
import br.com.gorillaroxo.sanjy.infrastructure.jpa.entity.MealTypeEntity;
import br.com.gorillaroxo.sanjy.infrastructure.utils.ConstantsInfrastructure;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

@Mapper(
    componentModel = ConstantsInfrastructure.MAPSTRUCT_COMPONENT_MODEL,
    uses = StandardOptionMapper.class,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MealTypeMapper {

    // DTOs
    MealTypeResponseDTO toDTO(final MealTypeDomain domain);
    List<MealTypeResponseDTO> toDTO(final List<MealTypeDomain> domain);
    Set<MealTypeResponseDTO> toDTO(final Set<MealTypeDomain> domain);

    // Domains
    @Mapping(target = "dietPlanId", source = "dietPlan.id")
    MealTypeDomain toDomain(MealTypeEntity dto);
    List<MealTypeDomain> toDomainListFromMealTypeEntity(final List<MealTypeEntity> dto);
    Set<MealTypeDomain> toDomainSetFromMealTypeEntity(final Set<MealTypeEntity> dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dietPlanId", ignore = true)
    MealTypeDomain toDomain(CreateMealTypesRequestDTO dto);
    List<MealTypeDomain> toDomainListFromCreateMealTypesRequestDTO(List<CreateMealTypesRequestDTO> dto);
    Set<MealTypeDomain> toDomainSetFromCreateMealTypesRequestDTO(Set<CreateMealTypesRequestDTO> dto);

    // Entities
    @Mapping(target = "dietPlan", ignore = true)
    MealTypeEntity toEntity(final MealTypeDomain domain);
    List<MealTypeEntity> toEntity(final List<MealTypeDomain> domain);
    Set<MealTypeEntity> toEntity(final Set<MealTypeDomain> domain);
}
