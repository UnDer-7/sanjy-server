package br.com.gorillaroxo.sanjy.entrypoint.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for creating a meal type within a diet plan")
public class CreateMealTypesRequestDTO {

    @NotBlank
    @Schema(description = "Meal type name",
        example = "Breakfast",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 50)
    private String name;

    @Schema(description = "Scheduled time for this meal",
        example = "06:20:00",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalTime scheduledTime;

    @Valid
    @NotNull
    @NotEmpty
    @Schema(description = "Set of standard food options for this meal type",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private Set<CreateStandardOptionRequestDTO> standardOptions;
}
