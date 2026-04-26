package br.com.gorillaroxo.sanjy.server.core.domain;

import java.time.LocalDate;
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
    private MetadataDomain metadata;

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
        this.startDate = Objects.requireNonNullElse(this.startDate, LocalDate.now());
        this.endDate = Objects.requireNonNullElse(this.endDate, startDate.plusMonths(2));
    }

    public void patch(final PatchableDietPlanDomain patchableDietPlan) {
        patchableDietPlan.getName().ifPresent(n -> this.name = n);
        patchableDietPlan.getStartDate().ifPresent(sd -> this.startDate = sd);
        patchableDietPlan.getEndDate().ifPresent(ed -> this.endDate = ed);
        patchableDietPlan.getDailyCalories().ifPresent(dc -> this.dailyCalories = dc);
        patchableDietPlan.getDailyProteinInG().ifPresent(dp -> this.dailyProteinInG = dp);
        patchableDietPlan.getDailyCarbsInG().ifPresent(dc -> this.dailyCarbsInG = dc);
        patchableDietPlan.getDailyFatInG().ifPresent(df -> this.dailyFatInG = df);
        patchableDietPlan.getGoal().ifPresent(g -> this.goal = g);
        patchableDietPlan.getNutritionistNotes().ifPresent(nn -> this.nutritionistNotes = nn);
    }

    public String toPatchableFieldsString() {
        return "( name=" + name +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            ", dailyCalories=" + dailyCalories +
            ", dailyProteinInG=" + dailyProteinInG +
            ", dailyCarbsInG=" + dailyCarbsInG +
            ", dailyFatInG=" + dailyFatInG +
            ", goal=" + goal +
            ", nutritionistNotes=" + nutritionistNotes + " )";
    }
}
