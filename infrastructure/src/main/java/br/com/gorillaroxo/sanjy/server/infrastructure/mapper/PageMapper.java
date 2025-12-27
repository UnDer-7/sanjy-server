package br.com.gorillaroxo.sanjy.server.infrastructure.mapper;

import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.pagination.PageResultDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.pagination.SearchMealRecordParamDomain;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.SearchMealRecordParamRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.PageResponseDto;
import br.com.gorillaroxo.sanjy.server.infrastructure.utils.InfrastructureConstants;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = InfrastructureConstants.MAPSTRUCT_COMPONENT_MODEL,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = MealRecordMapper.class)
public interface PageMapper {

    // DTOs - MealRecordDomain
    PageResponseDto<MealRecordResponseDto> toDto(PageResultDomain<MealRecordDomain> domain);

    // Domains
    SearchMealRecordParamDomain toDomain(SearchMealRecordParamRequestDto dto);

    // Domains - MealRecordDomain
    PageResultDomain<MealRecordDomain> toDomain(PageResponseDto<MealRecordResponseDto> dto);
}
