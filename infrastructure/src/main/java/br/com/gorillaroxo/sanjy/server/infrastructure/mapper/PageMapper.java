package br.com.gorillaroxo.sanjy.server.infrastructure.mapper;

import br.com.gorillaroxo.sanjy.server.core.domain.MealRecordDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.PageResultDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.SearchMealRecordParamDomain;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.SearchMealRecordParamRequestDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.MealRecordResponseDTO;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.PageResponseDTO;
import br.com.gorillaroxo.sanjy.server.infrastructure.utils.ConstantsInfrastructure;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = ConstantsInfrastructure.MAPSTRUCT_COMPONENT_MODEL,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = MealRecordMapper.class
)
public interface PageMapper {

    // DTOs - MealRecordDomain
    PageResponseDTO<MealRecordResponseDTO> toDTO(PageResultDomain<MealRecordDomain> domain);

    // Domains
    SearchMealRecordParamDomain toDomain(SearchMealRecordParamRequestDTO dto);

    // Domains - MealRecordDomain
    PageResultDomain<MealRecordDomain> toDomain(PageResponseDTO<MealRecordResponseDTO> dto);
}
