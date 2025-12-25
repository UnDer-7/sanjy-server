package br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Builder;

@Builder(toBuilder = true)
public record MetadataResponseDto(
        @Schema(
                description = "Timestamp when this resource was created",
                example = "2025-01-15T14:30:00Z",
                format = "date-time",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,

        @Schema(
                description = "Timestamp when this resource was updated",
                example = "2025-01-15T14:30:00Z",
                format = "date-time",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt) {}
