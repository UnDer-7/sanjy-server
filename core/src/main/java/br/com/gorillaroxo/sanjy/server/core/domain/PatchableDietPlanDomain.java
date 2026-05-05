package br.com.gorillaroxo.sanjy.server.core.domain;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@AllArgsConstructor
public class PatchableDietPlanDomain {

    @Getter
    private Long id;

    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer dailyCalories;
    private Integer dailyProteinInG;
    private Integer dailyCarbsInG;
    private Integer dailyFatInG;
    private String goal;
    private String nutritionistNotes;

    public Optional<String> getName() {
        return Optional.ofNullable(name).filter(Predicate.not(String::isBlank));
    }

    public Optional<LocalDate> getStartDate() {
        return Optional.ofNullable(startDate);
    }

    public Optional<LocalDate> getEndDate() {
        return Optional.ofNullable(endDate);
    }

    public Optional<Integer> getDailyCalories() {
        return Optional.ofNullable(dailyCalories);
    }

    public Optional<Integer> getDailyProteinInG() {
        return Optional.ofNullable(dailyProteinInG);
    }

    public Optional<Integer> getDailyCarbsInG() {
        return Optional.ofNullable(dailyCarbsInG);
    }

    public Optional<Integer> getDailyFatInG() {
        return Optional.ofNullable(dailyFatInG);
    }

    public Optional<String> getGoal() {
        return Optional.ofNullable(goal).filter(Predicate.not(String::isBlank));
    }

    public Optional<String> getNutritionistNotes() {
        return Optional.ofNullable(nutritionistNotes).filter(Predicate.not(String::isBlank));
    }
}
