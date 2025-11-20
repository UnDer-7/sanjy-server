package br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
@Schema(description = "Paginated response wrapper containing page metadata and content")
public record PageResponseDto<T>(
        @Schema(description = "Total number of pages available", example = "5")
        Long totalPages,

        @Schema(description = "Current page number (zero-based)", example = "0")
        Long currentPage,

        @Schema(description = "Number of items per page", example = "20")
        Long pageSize,

        @Schema(description = "Total number of items across all pages", example = "100")
        Long totalItems,

        @Schema(description = "List of items in the current page")
        List<T> content) {}
