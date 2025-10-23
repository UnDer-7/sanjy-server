package br.com.gorillaroxo.sanjy.server.entrypoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request DTO for creating a standard meal option within a meal type")
public record CreateStandardOptionRequestDTO(

    @Schema(description = "Option number within the meal type. Must follow a complete sequence starting from 1 with no gaps. " +
                            "The sequence can be sent in any order (backend will sort), but all numbers must be present. " +
                            "Valid examples: [1,2,3,4,5] or [6,5,1,2,4,3]. " +
                            "Invalid examples: [6,2,3,4,5] (missing 1), [1,2,4,5,6] (missing 3)",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    Long optionNumber,

    @Schema(description = "Complete description of foods that compose this meal option",
            example = "2 slices of whole grain bread + 2 scrambled eggs + 1 banana + 200ml of coffee without sugar",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String description
) {

}
