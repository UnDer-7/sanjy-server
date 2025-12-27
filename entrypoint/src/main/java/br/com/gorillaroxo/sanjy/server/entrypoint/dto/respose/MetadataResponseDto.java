package br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose;

import br.com.gorillaroxo.sanjy.server.entrypoint.util.OpenApiConstants;
import br.com.gorillaroxo.sanjy.server.entrypoint.util.RequestConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Builder;

@Builder(toBuilder = true)
@Schema(description = "Metadata information containing timestamps and additional contextual data about the resource")
public record MetadataResponseDto(
        @Schema(
                description = "Timestamp when this resource was created in UTC timezone (ISO 8601 format)",
                example = OpenApiConstants.Examples.DATE_TIME,
                format = RequestConstants.DateTimeFormats.DATE_TIME_FORMAT,
                requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,

        @Schema(
                description = "Timestamp when this resource was last updated in UTC timezone (ISO 8601 format)",
                example = OpenApiConstants.Examples.DATE_TIME,
                format = RequestConstants.DateTimeFormats.DATE_TIME_FORMAT,
                requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt) {}
