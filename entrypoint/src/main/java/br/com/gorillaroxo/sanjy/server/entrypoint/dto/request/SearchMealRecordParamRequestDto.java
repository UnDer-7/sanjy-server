package br.com.gorillaroxo.sanjy.server.entrypoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Schema(description = "Search parameters for meal records with pagination support")
public class SearchMealRecordParamRequestDto extends PageRequestDto {

    @Schema(
            description = "Filter meals consumed after this date/time",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true,
            format = "date-time",
            example = "2025-01-15T14:30:00Z")
    private Instant consumedAtAfter;

    @Schema(
            description = "Filter meals consumed before this date/time",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true,
            format = "date-time",
            example = "2025-01-15T14:30:00Z")
    private Instant consumedAtBefore;

    @Schema(description = """
                Filter by meal type. True returns only free meals (off-plan), false returns only standard meals (following the diet plan). \
                If not specified, returns both types
                """, requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true, example = "false")
    private Boolean isFreeMeal;
}
