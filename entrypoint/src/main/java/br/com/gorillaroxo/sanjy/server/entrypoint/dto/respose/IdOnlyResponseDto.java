package br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO used to return only the identifier of an entity")
public record IdOnlyResponseDto(
        @Schema(
                description = "Unique identifier of the entity",
                example = "123",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long id) {
}
