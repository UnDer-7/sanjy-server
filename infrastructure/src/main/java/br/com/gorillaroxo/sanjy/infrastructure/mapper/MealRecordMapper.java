package br.com.gorillaroxo.sanjy.infrastructure.mapper;

import br.com.gorillaroxo.sanjy.core.domain.MealRecordDomain;
import br.com.gorillaroxo.sanjy.core.domain.MealTypeDomain;
import br.com.gorillaroxo.sanjy.core.domain.StandardOptionDomain;
import br.com.gorillaroxo.sanjy.entrypoint.dto.request.CreateMealRecordRequestDTO;
import br.com.gorillaroxo.sanjy.entrypoint.dto.respose.MealRecordResponseDTO;
import br.com.gorillaroxo.sanjy.infrastructure.jpa.entity.MealRecordEntity;
import br.com.gorillaroxo.sanjy.infrastructure.utils.ConstantsInfrastructure;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.Optional;

@Mapper(
    componentModel = ConstantsInfrastructure.MAPSTRUCT_COMPONENT_MODEL,
    uses = {MealTypeMapper.class, StandardOptionMapper.class}
)
public interface MealRecordMapper {

    // DTOs
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

    MealRecordResponseDTO toDTO(MealRecordDomain domain);

    // Entities
    MealRecordEntity toEntity(MealRecordDomain domain);

    MealRecordDomain toDomain(MealRecordEntity entity);
}
