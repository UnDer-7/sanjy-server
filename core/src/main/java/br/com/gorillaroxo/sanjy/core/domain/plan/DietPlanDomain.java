package br.com.gorillaroxo.sanjy.core.domain.plan;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Getter
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
    private Set<MealType> mealTypes;

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
        this.startDate = Objects.requireNonNullElse(this.startDate, LocalDate.now());
        this.endDate = Objects.requireNonNullElse(this.endDate, startDate.plusMonths(2));
    }
}
