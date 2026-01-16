package br.com.gorillaroxo.sanjy.server.entrypoint.dto.request;

import br.com.gorillaroxo.sanjy.server.entrypoint.util.OpenApiConstants;
import br.com.gorillaroxo.sanjy.server.entrypoint.util.RequestConstants;
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
            description = "Filter meals consumed after this date/time, in UTC timezone (ISO 8601 format).",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true,
            format = RequestConstants.DateTimeFormats.DATE_TIME_FORMAT,
            example = OpenApiConstants.Examples.DATE_TIME)
    private Instant consumedAtAfter;

    @Schema(
            description = "Filter meals consumed before this date/time, in UTC timezone (ISO 8601 format).",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true,
            format = RequestConstants.DateTimeFormats.DATE_TIME_FORMAT,
            example = OpenApiConstants.Examples.DATE_TIME)
    private Instant consumedAtBefore;

    @Schema(description = """
                Filter by meal type. True returns only free meals (off-plan), false returns only standard meals (following the diet plan). \
                If not specified, returns both types
                """, requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true, example = "false")
    private Boolean isFreeMeal;
}
