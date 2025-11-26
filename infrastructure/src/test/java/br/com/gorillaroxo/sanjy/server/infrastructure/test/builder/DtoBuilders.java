package br.com.gorillaroxo.sanjy.server.infrastructure.test.builder;

import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateDietPlanRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateMealTypesRequestDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.request.CreateStandardOptionRequestDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public final class DtoBuilders {

    private DtoBuilders() {
        throw new IllegalStateException("Utility class");
    }

    public static CreateDietPlanRequestDto.CreateDietPlanRequestDtoBuilder buildCreateDietPlanRequestDto() {
        return CreateDietPlanRequestDto.builder()
            .name("Plan N°02 - Cutting")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusMonths(3))
            .dailyCalories(2266)
            .dailyProteinInG(186)
            .dailyCarbsInG(288)
            .dailyFatInG(30)
            .goal("Body fat reduction with muscle mass preservation")
            .nutritionistNotes("Patient has lactose intolerance. Avoid dairy products.")
            .mealTypes(List.of(buildCreateMealTypesRequestDto().build()));
    }

    public static CreateMealTypesRequestDto.CreateMealTypesRequestDtoBuilder buildCreateMealTypesRequestDto() {
        return CreateMealTypesRequestDto.builder()
            .name("Breakfast")
            .scheduledTime(LocalTime.now())
            .observation("45 g proteína | 35 g carbo | 6 g gordura | 380 kcal")
            .standardOptions(List.of(buildCreateStandardOptionRequestDto().build()));
    }

    public static CreateStandardOptionRequestDto.CreateStandardOptionRequestDtoBuilder buildCreateStandardOptionRequestDto() {
        return CreateStandardOptionRequestDto.builder()
            .optionNumber(1L)
            .description("Pão francês sem miolo -- 45g | Ovos mexidos -- 3 ovos (150g) | Queijo minas frescal zero lactose -- 25g");
    }
}
