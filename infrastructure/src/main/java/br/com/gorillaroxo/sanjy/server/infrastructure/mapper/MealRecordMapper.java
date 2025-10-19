package br.com.gorillaroxo.sanjy.server.infrastructure.mapper;

import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.MealTypeDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.StandardOptionDomain;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateMealRecordRequestDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordResponseDTO;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.MealRecordEntity;
import br.com.gorillaroxo.sanjy.server.infrastructure.utils.ConstantsInfrastructure;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Mapper(
    componentModel = ConstantsInfrastructure.MAPSTRUCT_COMPONENT_MODEL,
    uses = {MealTypeMapper.class, StandardOptionMapper.class},
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MealRecordMapper {

    // DTOs
    MealRecordResponseDTO toDTO(MealRecordDomain domain);
    List<MealRecordResponseDTO> toDTO(List<MealRecordDomain> domain);

    // Domains
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mealType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "standardOption", ignore = true)
    MealRecordDomain toDomain(CreateMealRecordRequestDTO dto);

    @AfterMapping
    default void toDomainAfterMapping(@MappingTarget final MealRecordDomain.MealRecordDomainBuilder target, final CreateMealRecordRequestDTO source) {
        Optional.ofNullable(source)
            .map(CreateMealRecordRequestDTO::mealTypeId)
            .map(mealTypeId -> MealTypeDomain.builder().id(mealTypeId).build())
            .ifPresent(target::mealType);
        Optional.ofNullable(source)
            .map(CreateMealRecordRequestDTO::standardOptionId)
            .map(standardOptionId -> StandardOptionDomain.builder().id(standardOptionId).build())
            .ifPresent(target::standardOption);
    }

    MealRecordDomain toDomain(MealRecordEntity entity);
    List<MealRecordDomain> toDomain(List<MealRecordEntity> entities);

    MealRecordDomain toDomain(MealRecordResponseDTO dto);
    List<MealRecordDomain> toDomainListFromMealRecordResponseDTO(List<MealRecordResponseDTO> dto);
    Set<MealRecordDomain> toDomainSetFromMealRecordResponseDTO(Set<MealRecordResponseDTO> dto);

    // Entities
    MealRecordEntity toEntity(MealRecordDomain domain);
}
