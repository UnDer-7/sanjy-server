package br.com.gorillaroxo.sanjy.core.usecase;

import br.com.gorillaroxo.sanjy.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.core.domain.MealTypeDomain;
import br.com.gorillaroxo.sanjy.core.ports.driven.DietPlanGateway;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class CurrentMealTypeUseCaseImplTest {

    @Mock
    DietPlanGateway dietPlanGateway;

    @InjectMocks
    AvailableMealTypesUseCaseImpl currentMealTypeUseCase;

    @ParameterizedTest
    @MethodSource("testSuite_should_return_expected_mealType")
    void should_return_expected_mealType(final List<MealTypeDomain> expectedMealTypes, final Set<MealTypeDomain> givenMealTypes) {
        // Given
        final var dietPlan = DietPlanDomain.builder()
            .mealTypes(givenMealTypes)
            .build();

        Mockito.when(dietPlanGateway.findActive()).thenReturn(Optional.of(dietPlan));

        // When
        final List<MealTypeDomain> actualMealTypes = currentMealTypeUseCase.execute();

        // Then
        Assertions.assertThat(actualMealTypes).isEqualTo(expectedMealTypes);
    }

    static Stream<Arguments> testSuite_should_return_expected_mealType() {
        final MealTypeDomain mealTypeBreakfast = MealTypeDomain.builder()
            .id(1L)
            .name("Breakfast")
            .scheduledTime(LocalTime.of(9, 0))
            .build();
        final MealTypeDomain mealTypeLunch = MealTypeDomain.builder()
            .id(2L)
            .name("Lunch")
            .scheduledTime(LocalTime.of(14, 0))
            .build();
        final MealTypeDomain mealTypeAfternoonSnack = MealTypeDomain.builder()
            .id(3L)
            .name("afternoon snack")
            .scheduledTime(LocalTime.of(18, 0))
            .build();
        final MealTypeDomain mealTypeDinner = MealTypeDomain.builder()
            .id(4L)
            .name("dinner")
            .scheduledTime(LocalTime.of(21, 0))
            .build();

        final List<MealTypeDomain> expectedMealTypes = List.of(mealTypeBreakfast, mealTypeLunch, mealTypeAfternoonSnack, mealTypeDinner);

        return Stream.of(
            Arguments.of(expectedMealTypes, Set.of(mealTypeBreakfast, mealTypeLunch, mealTypeAfternoonSnack, mealTypeDinner)),
            Arguments.of(expectedMealTypes, Set.of(mealTypeBreakfast, mealTypeDinner, mealTypeLunch, mealTypeAfternoonSnack)),
            Arguments.of(expectedMealTypes, Set.of(mealTypeAfternoonSnack, mealTypeDinner, mealTypeLunch, mealTypeBreakfast)),
            Arguments.of(expectedMealTypes, Set.of(mealTypeAfternoonSnack, mealTypeLunch, mealTypeDinner, mealTypeBreakfast))
                        );
    }
}