package br.com.gorillaroxo.sanjy.server.infrastructure.mapper;

import br.com.gorillaroxo.sanjy.server.infrastructure.utils.InfrastructureConstants;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = InfrastructureConstants.MAPSTRUCT_COMPONENT_MODEL,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
@SuppressWarnings("java:S2789")
public class OptionalMapper {

    public <T> T fromOptional(final Optional<T> optional) {
        if (optional == null) {
            return null;
        }

        return optional.orElse(null);
    }

    public <T> Optional<T> toOptional(final T value) {
        return Optional.ofNullable(value);
    }
}
