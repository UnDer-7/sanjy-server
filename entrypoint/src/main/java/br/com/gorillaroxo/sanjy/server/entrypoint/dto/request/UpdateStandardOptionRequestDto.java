package br.com.gorillaroxo.sanjy.server.entrypoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = """
        Request DTO for partially updating a standard meal option. \
        Only fields explicitly provided in the request body will be updated — \
        omitted, null, or blank fields are ignored and the existing values are preserved.
        """)
public record UpdateStandardOptionRequestDto(
        @Schema(
                description = "Complete description of foods that compose this meal option",
                example = "Pão francês sem miolo -- 45g | Ovos mexidos -- 3 ovos (150g) | Queijo minas frescal zero lactose -- 25g",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String description) {}
