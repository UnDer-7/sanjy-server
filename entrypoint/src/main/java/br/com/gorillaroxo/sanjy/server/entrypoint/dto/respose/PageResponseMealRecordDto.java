package br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose;

/**
 * Concrete wrapper class for paginated meal record responses.
 *
 * <p>This class exists solely to work around a limitation in Swagger/OpenAPI with generic types. While it would be
 * ideal to return {@code PageResponseDto<MealRecordResponseDto>} directly from controllers, Swagger cannot properly
 * render generic response types in the documentation. Without this concrete class, the Swagger UI would show an
 * incorrect or broken response schema.
 *
 * <p><b>Why this is necessary:</b>
 *
 * <ul>
 *   <li>Swagger generates unhelpful schema names for generic types (e.g., "PageResponseDtoMealRecordResponseDto")
 *   <li>The response schema in Swagger UI doesn't correctly expand the generic type parameter
 *   <li>API consumers would see incomplete or confusing documentation
 * </ul>
 *
 * <p><b>The workaround:</b>
 *
 * <p>Create a concrete class that extends the generic parent. This allows Swagger to correctly identify and document
 * the response structure with proper type information for the {@code content} field.
 *
 * <p>Example controller usage:
 *
 * <pre>{@code
 * @GetMapping("/meal-records")
 * public PageResponseMealRecordDto searchMealRecords(...) {
 *     // Implementation
 * }
 * }</pre>
 *
 * @see PageResponseDto
 * @see <a href="https://github.com/swagger-api/swagger-core/issues/3323">Swagger Generic Types Issue #3323</a>
 */
public class PageResponseMealRecordDto extends PageResponseDto<MealRecordResponseDto> {

    public PageResponseMealRecordDto(final PageResponseDto<MealRecordResponseDto> page) {
        super(page.getTotalPages(), page.getCurrentPage(), page.getPageSize(), page.getTotalItems(), page.getContent());
    }
}
