package br.com.gorillaroxo.sanjy.server.entrypoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Pagination request parameters")
public class PageRequestDTO {

    @NotNull
    @Positive
    @Schema(
        description = "Page number to retrieve (zero-based, where 0 is the first page)",
        requiredMode = Schema.RequiredMode.REQUIRED,
        nullable = false,
        example = "0")
    private Integer pageNumber;

    @Positive
    @Builder.Default
    @Schema(
        description = "Number of items per page. If not specified, returns 10 items per page",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        nullable = true,
        defaultValue = "10",
        example = "10")
    private Integer pageSize = 10;

}
