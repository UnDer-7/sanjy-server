package br.com.gorillaroxo.sanjy.server.infrastructure.mapper;

import br.com.gorillaroxo.sanjy.server.core.exception.BusinessException;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ErrorResponseDTO;
import br.com.gorillaroxo.sanjy.server.infrastructure.utils.ConstantsInfrastructure;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = ConstantsInfrastructure.MAPSTRUCT_COMPONENT_MODEL,
    uses = OptionalMapper.class,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface BusinessExceptionMapper {

    @Mapping(target = "code", source = "exceptionCode.code")
    @Mapping(target = "message", source = "exceptionCode.message")
    ErrorResponseDTO toDTO(BusinessException exception);
}
