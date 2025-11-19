package br.com.gorillaroxo.sanjy.server.core.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class DietPlanDomain {

    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private Integer dailyCalories;
    private Integer dailyProteinInG;
    private Integer dailyCarbsInG;
    private Integer dailyFatInG;
    private String goal;
    private String nutritionistNotes;
    private List<MealTypeDomain> mealTypes;
    private LocalDateTime createdAt;

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
        this.startDate = Objects.requireNonNullElse(this.startDate, LocalDate.now());
        this.endDate = Objects.requireNonNullElse(this.endDate, startDate.plusMonths(2));
    }
}
