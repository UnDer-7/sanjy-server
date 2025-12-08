package br.com.gorillaroxo.sanjy.server.infrastructure.mapper;

import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordStatisticsDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.MealTypeDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.StandardOptionDomain;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateMealRecordRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordStatisticsResponseDto;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.MealRecordEntity;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.projection.MealRecordStatisticsProjection;
import br.com.gorillaroxo.sanjy.server.infrastructure.utils.ConstantsInfrastructure;
import java.util.List;
import java.util.Optional;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = ConstantsInfrastructure.MAPSTRUCT_COMPONENT_MODEL,
        uses = {MealTypeMapper.class, StandardOptionMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MealRecordMapper {

    // DTOs
    MealRecordResponseDto toDto(MealRecordDomain domain);

    List<MealRecordResponseDto> toDto(List<MealRecordDomain> domain);

    MealRecordStatisticsResponseDto toDto(MealRecordStatisticsDomain domain);

    // Domains
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mealType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "standardOption", ignore = true)
    MealRecordDomain toDomain(CreateMealRecordRequestDto dto);

    MealRecordDomain toDomain(MealRecordEntity entity);

    List<MealRecordDomain> toDomain(List<MealRecordEntity> entities);

    @Mapping(target = "mealType.name", ignore = true)
    @Mapping(target = "mealType.scheduledTime", ignore = true)
    @Mapping(target = "mealType.dietPlanId", ignore = true)
    @Mapping(target = "mealType.observation", ignore = true)
    @Mapping(target = "mealType.standardOptions", ignore = true)
    @Mapping(target = "standardOption.optionNumber", ignore = true)
    @Mapping(target = "standardOption.description", ignore = true)
    @Mapping(target = "standardOption.mealTypeId", ignore = true)
    MealRecordDomain toDomain(MealRecordResponseDto dto);

    MealRecordStatisticsDomain toDomain(MealRecordStatisticsProjection projection);

    @AfterMapping
    default void toDomainAfterMapping(
            @MappingTarget final MealRecordDomain.MealRecordDomainBuilder target,
            final CreateMealRecordRequestDto source) {
        Optional.ofNullable(source)
                .map(CreateMealRecordRequestDto::mealTypeId)
                .map(mealTypeId -> MealTypeDomain.builder().id(mealTypeId).build())
                .ifPresent(target::mealType);
        Optional.ofNullable(source)
                .map(CreateMealRecordRequestDto::standardOptionId)
                .map(standardOptionId ->
                        StandardOptionDomain.builder().id(standardOptionId).build())
                .ifPresent(target::standardOption);
    }

    List<MealRecordDomain> toDomainListFromMealRecordResponseDto(List<MealRecordResponseDto> dto);

    // Entities
    MealRecordEntity toEntity(MealRecordDomain domain);
}
