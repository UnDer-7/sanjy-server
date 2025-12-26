package br.com.gorillaroxo.sanjy.server.infrastructure.test.builder;

import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.DietPlanEntity;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.MealTypeEntity;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.MetadataEmbeddedEntity;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.StandardOptionEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

public final class EntityBuilders {

    private EntityBuilders() {
        throw new IllegalStateException("Utility class");
    }

    public static DietPlanEntity.DietPlanEntityBuilder buildDietPlanEntity() {
        return DietPlanEntity.builder()
                .id(2L)
                .name("Plan N°02 - Cutting")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(3))
                .isActive(true)
                .dailyCalories(2266)
                .dailyProteinInG(186)
                .dailyCarbsInG(288)
                .dailyFatInG(30)
                .goal("Body fat reduction with muscle mass preservation")
                .nutritionistNotes("Patient has lactose intolerance. Avoid dairy products.")
                .metadata(buildMetadataEmbeddedEntity().build());
    }

    public static MetadataEmbeddedEntity.MetadataEmbeddedEntityBuilder buildMetadataEmbeddedEntity() {
        return MetadataEmbeddedEntity.builder()
            .createdAt(Instant.now())
            .updatedAt(Instant.now());
    }

    public static MealTypeEntity.MealTypeEntityBuilder buildMealTypeEntity() {
        return MealTypeEntity.builder()
                .id(3L)
                .dietPlan(DietPlanEntity.builder().build())
                .name("Breakfast")
                .scheduledTime(LocalTime.now())
                .observation("45 g proteína | 35 g carbo | 6 g gordura | 380 kcal")
                .metadata(buildMetadataEmbeddedEntity().build());
    }

    public static StandardOptionEntity.StandardOptionEntityBuilder buildStandardOptionEntity() {
        return StandardOptionEntity.builder()
                .id(4L)
                .optionNumber(1)
                .description(
                        "Pão francês sem miolo -- 45g | Ovos mexidos -- 3 ovos (150g) | Queijo minas frescal zero lactose -- 25g")
                .metadata(buildMetadataEmbeddedEntity().build());
    }
}
