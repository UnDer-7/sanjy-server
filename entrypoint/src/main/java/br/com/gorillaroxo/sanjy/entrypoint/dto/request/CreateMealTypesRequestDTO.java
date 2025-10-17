package br.com.gorillaroxo.sanjy.entrypoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.Set;

@Schema(description = "Request DTO for creating a meal type within a diet plan")
public record CreateMealTypesRequestDTO(
    @NotBlank
    @Schema(description = "Meal type name",
        example = "Breakfast",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 50)
    String name,

    @Schema(description = "Scheduled time for this meal",
        example = "06:20:00",
        type = "string",
        pattern = "^([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    LocalTime scheduledTime,

    @Valid
    @NotNull
    @NotEmpty
    @Schema(description = "Set of standard food options for this meal type",
        requiredMode = Schema.RequiredMode.REQUIRED)
    Set<CreateStandardOptionRequestDTO> standardOptions
) {

}
