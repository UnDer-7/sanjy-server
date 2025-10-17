package br.com.gorillaroxo.sanjy.entrypoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request DTO for creating a standard meal option within a meal type")
public record CreateStandardOptionRequestDTO(

    @Schema(description = "Option number within the meal type (1, 2, 3, etc)",
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
