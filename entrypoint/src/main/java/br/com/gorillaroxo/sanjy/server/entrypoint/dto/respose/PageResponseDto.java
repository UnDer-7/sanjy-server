package br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Generic paginated response wrapper containing page metadata and content.
 *
 * <p>This DTO provides a standardized structure for paginated API responses, including pagination metadata (total
 * pages, current page, page size, total items) and the actual content list.
 *
 * <p><b>IMPORTANT - Swagger/OpenAPI Limitation:</b>
 *
 * <p>Due to a known limitation in Swagger with generic types, <b>DO NOT use this class directly as a controller return
 * type</b>. Instead, create a concrete class that extends this generic class. Swagger cannot properly render generic
 * types in the API documentation, which would result in broken or incomplete schema representation in Swagger UI.
 *
 * <p><b>Correct usage pattern:</b>
 *
 * <ol>
 *   <li>Create a concrete class extending {@code PageResponseDto<YourDto>}
 *   <li>Use the concrete class as the controller return type
 *   <li>Map your paginated data to the concrete class before returning
 * </ol>
 *
 * <p>Example concrete class:
 *
 * <pre>{@code
 * public class PageResponseYourEntityDto extends PageResponseDto<YourEntityDto> {
 *     public PageResponseYourEntityDto(final PageResponseDto<YourEntityDto> page) {
 *         super(page.getTotalPages(), page.getCurrentPage(), page.getPageSize(),
 *               page.getTotalItems(), page.getContent());
 *     }
 * }
 * }</pre>
 *
 * <p>Controller usage:
 *
 * <pre>{@code
 * @GetMapping("/your-entities")
 * public PageResponseYourEntityDto getEntities(...) {
 *     PageResponseDto<YourEntityDto> page = // ... build page
 *     return new PageResponseYourEntityDto(page);
 * }
 * }</pre>
 *
 * @param <T> the type of content items in the page
 * @see PageResponseMealRecordDto
 * @see <a href="https://github.com/swagger-api/swagger-core/issues/3323">Swagger Generic Types Issue #3323</a>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "Paginated response wrapper containing page metadata and content")
public class PageResponseDto<T> {

    @Schema(description = "Total number of pages available", example = "5")
    private Long totalPages;

    @Schema(description = "Current page number (zero-based)", example = "0")
    private Long currentPage;

    @Schema(description = "Number of items per page", example = "20")
    private Long pageSize;

    @Schema(description = "Total number of items across all pages", example = "100")
    private Long totalItems;

    @Schema(description = "List of items in the current page")
    private List<T> content;
}
