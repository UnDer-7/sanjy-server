package br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = """
    Generic wrapper DTO for returning only the identifier of a related entity. \
    Used when the full entity details are not needed in the response, providing a lightweight reference by ID only.
    """)
public record IdOnlyResponseDto(
        @Schema(
                description = "Unique identifier of the referenced entity",
                example = "123",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long id) {}
