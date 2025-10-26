package br.com.gorillaroxo.sanjy.server.entrypoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Schema(description = "Search parameters for meal records with pagination support")
public class SearchMealRecordParamRequestDTO extends PageRequestDTO {

    @Schema(
        description = "Filter meals consumed after this date/time",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        nullable = true,
        example = "2024-01-01T00:00:00")
    private LocalDateTime consumedAtAfter;

    @Schema(
        description = "Filter meals consumed before this date/time",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        nullable = true,
        example = "2024-12-31T23:59:59")
    private LocalDateTime consumedAtBefore;

    @Schema(
        description = "Filter by meal type. True returns only free meals (off-plan), false returns only standard meals (following the diet plan). If not specified, returns both types",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        nullable = true,
        example = "false")
    private Boolean isFreeMeal;

}
