package br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record DietPlanCompleteResponseDTO(
    Long id,
    String name,
    LocalDate startDate,
    LocalDate endDate,
    Boolean isActive,
    Integer dailyCalories,
    Integer dailyProteinInG,
    Integer dailyCarbsInG,
    Integer dailyFatInG,
    String goal,
    String nutritionistNotes,
    Set<MealTypeResponseDTO> mealTypes,
    LocalDateTime createdAt
) {

}
