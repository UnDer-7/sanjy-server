package br.com.gorillaroxo.sanjy.server.core.test;

import br.com.gorillaroxo.sanjy.server.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.MealTypeDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.StandardOptionDomain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public final class DomainBuilder {

    private DomainBuilder() {
        throw new UnsupportedOperationException("Cannot instantiate DomainBuilder");
    }

    public static DietPlanDomain.DietPlanDomainBuilder buildDietPlanDomain() {
        return DietPlanDomain.builder()
            .id(100L)
            .name("Plan N°02 - Cutting")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusMonths(5))
            .isActive(true)
            .dailyCalories(2266)
            .dailyProteinInG(186)
            .dailyCarbsInG(288)
            .dailyFatInG(30)
            .goal("Body fat reduction with muscle mass preservation")
            .nutritionistNotes("Patient has lactose intolerance. Avoid dairy products.")
            .mealTypes(List.of(buildMealTypeDomain().build()));
    }

    public static MealTypeDomain.MealTypeDomainBuilder buildMealTypeDomain() {
        return MealTypeDomain.builder()
            .id(1L)
            .name("Breakfast")
            .scheduledTime(LocalTime.now())
            .dietPlanId(100L)
            .standardOptions(List.of(buildStandardOptionDomain().build()));
    }

    public static StandardOptionDomain.StandardOptionDomainBuilder buildStandardOptionDomain() {
        return StandardOptionDomain.builder()
            .id(1L)
            .optionNumber(1L)
            .description("Pão francês sem miolo -- 45g | Ovos mexidos -- 3 ovos (150g) | Queijo minas frescal zero lactose -- 25g")
            .mealTypeId(1L);
    }
}
