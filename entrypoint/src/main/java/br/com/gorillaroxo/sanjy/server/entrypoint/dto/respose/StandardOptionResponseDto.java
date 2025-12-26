package br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record StandardOptionResponseDto(
        @Schema(
                description = "Unique identifier of the Standard Option",
                example = "123",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,

        @Schema(
                description = "Option number within the meal type (1, 2, 3, etc)",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long optionNumber,

        @Schema(
                description = "Complete description of foods that compose this meal option",
                example = "2 slices of whole grain bread + 2 scrambled eggs + 1 banana + 200ml of coffee without sugar",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String description,

        @Schema(
                description = "Identifier of the meal type this standard option belongs to",
                example = "789",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long mealTypeId,

        @Schema(
                description = "Metadata information containing creation and last update timestamps, along with other contextual data",
                requiredMode = Schema.RequiredMode.REQUIRED)
        MetadataResponseDto metadata) {}
