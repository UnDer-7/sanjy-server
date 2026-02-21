package br.com.gorillaroxo.sanjy.server.core.service;

import br.com.gorillaroxo.sanjy.server.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.MealTypeDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.StandardOptionDomain;
import br.com.gorillaroxo.sanjy.server.core.exception.RepeatedMealTypeNamesException;
import br.com.gorillaroxo.sanjy.server.core.exception.StandardOptionsNotInSequence;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.DietPlanGateway;
import br.com.gorillaroxo.sanjy.server.core.test.DomainBuilder;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DietPlanServiceTest {

    @Mock
    DietPlanGateway dietPlanGateway;

    @InjectMocks
    DietPlanService dietPlanService;

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void should_fail_when_first_standardOptions_is_not_number_1_one_element(final long optionNumber) {
        // Given
        final DietPlanDomain dietPlan = DomainBuilder.buildDietPlanDomain()
                .mealTypes(List.of(DomainBuilder.buildMealTypeDomain()
                        .standardOptions(Collections.singletonList(DomainBuilder.buildStandardOptionDomain()
                                .optionNumber(optionNumber)
                                .build()))
                        .build()))
                .build();

        // When
        final var abstractThrowableAssert = assertThatThrownBy(() -> dietPlanService.insert(dietPlan));

        // Then
        abstractThrowableAssert
                .isInstanceOf(StandardOptionsNotInSequence.class)
                .hasMessageContaining("StandardOptions must start with number 1")
                .hasMessageContaining(Long.toString(optionNumber));
        verify(dietPlanGateway, never()).insert(any());
    }

    @ParameterizedTest
    @MethodSource("testSuite_should_fail_when_first_standardOptions_is_not_number_1_list")
    void should_fail_when_first_standardOptions_is_not_number_1_list(final List<StandardOptionDomain> standardOptions) {
        // Given
        final DietPlanDomain dietPlan = DomainBuilder.buildDietPlanDomain()
                .mealTypes(List.of(DomainBuilder.buildMealTypeDomain()
                        .standardOptions(standardOptions)
                        .build()))
                .build();

        // When
        final var abstractThrowableAssert = assertThatThrownBy(() -> dietPlanService.insert(dietPlan));

        // Then
        abstractThrowableAssert
                .isInstanceOf(StandardOptionsNotInSequence.class)
                .hasMessageContaining("StandardOptions must start with number 1");
        verify(dietPlanGateway, never()).insert(any());
    }

    @ParameterizedTest
    @MethodSource("testSuite_should_fail_when_standardOptions_is_missing_optionNumber_and_therefore_out_of_order")
    void should_fail_when_standardOptions_is_missing_optionNumber_and_therefore_out_of_order(
            final List<StandardOptionDomain> standardOptions) {
        // Given
        final DietPlanDomain dietPlan = DomainBuilder.buildDietPlanDomain()
                .mealTypes(List.of(DomainBuilder.buildMealTypeDomain()
                        .standardOptions(standardOptions)
                        .build()))
                .build();

        // When
        final var abstractThrowableAssert = assertThatThrownBy(() -> dietPlanService.insert(dietPlan));

        // Then
        abstractThrowableAssert
                .isInstanceOf(StandardOptionsNotInSequence.class)
                .hasMessageContaining(
                        "StandardOptions with non-sequential numbers (skipped or repeated numbers detected)");
        verify(dietPlanGateway, never()).insert(any());
    }

    @ParameterizedTest
    @MethodSource("testSuite_should_fail_when_passing_repeated_meal_type_names")
    void should_fail_when_passing_repeated_meal_type_names(
            final List<MealTypeDomain> mealTypes, final List<String> expectedErrorMessages) {
        // Given
        final DietPlanDomain dietPlan =
                DomainBuilder.buildDietPlanDomain().mealTypes(mealTypes).build();

        // When
        final var abstractThrowableAssert = assertThatThrownBy(() -> dietPlanService.insert(dietPlan));

        // Then
        abstractThrowableAssert
                .isInstanceOf(RepeatedMealTypeNamesException.class)
                .hasMessageContainingAll(expectedErrorMessages.toArray(new String[0]));
        verify(dietPlanGateway, never()).insert(any());
    }

    static Stream<Arguments> testSuite_should_fail_when_passing_repeated_meal_type_names() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                DomainBuilder.buildMealTypeDomain()
                                        .name("Breakfast")
                                        .build(),
                                DomainBuilder.buildMealTypeDomain()
                                        .name("Jantar")
                                        .build(),
                                DomainBuilder.buildMealTypeDomain()
                                        .name("breakfast")
                                        .build()),
                        List.of("breakfast")),
                Arguments.of(
                        List.of(
                                DomainBuilder.buildMealTypeDomain()
                                        .name("lunch")
                                        .build(),
                                DomainBuilder.buildMealTypeDomain()
                                        .name("Lunch")
                                        .build(),
                                DomainBuilder.buildMealTypeDomain()
                                        .name("Breakfast")
                                        .build()),
                        List.of("lunch")),
                Arguments.of(
                        List.of(
                                DomainBuilder.buildMealTypeDomain()
                                        .name("brunch")
                                        .build(),
                                DomainBuilder.buildMealTypeDomain()
                                        .name("BRUNCH")
                                        .build(),
                                DomainBuilder.buildMealTypeDomain()
                                        .name("Breakfast")
                                        .build()),
                        List.of("brunch")),
                Arguments.of(
                        List.of(
                                DomainBuilder.buildMealTypeDomain()
                                        .name("brunch")
                                        .build(),
                                DomainBuilder.buildMealTypeDomain()
                                        .name("brUNCh")
                                        .build(),
                                DomainBuilder.buildMealTypeDomain()
                                        .name("Breakfast")
                                        .build()),
                        List.of("brunch")),
                Arguments.of(
                        List.of(
                                DomainBuilder.buildMealTypeDomain()
                                        .name("dinner")
                                        .build(),
                                DomainBuilder.buildMealTypeDomain()
                                        .name("dinner")
                                        .build(),
                                DomainBuilder.buildMealTypeDomain()
                                        .name("Breakfast")
                                        .build(),
                                DomainBuilder.buildMealTypeDomain()
                                        .name("lunch")
                                        .build(),
                                DomainBuilder.buildMealTypeDomain()
                                        .name("Lunch")
                                        .build()),
                        List.of("lunch", "dinner")));
    }

    static Stream<Arguments> testSuite_should_fail_when_first_standardOptions_is_not_number_1_list() {
        final Function<Integer, StandardOptionDomain> buildStandardOptionDomain =
                optionNumber -> DomainBuilder.buildStandardOptionDomain()
                        .optionNumber(optionNumber.longValue())
                        .build();

        return Stream.of(
                Arguments.of(List.of(
                        buildStandardOptionDomain.apply(0),
                        buildStandardOptionDomain.apply(1),
                        buildStandardOptionDomain.apply(2),
                        buildStandardOptionDomain.apply(3),
                        buildStandardOptionDomain.apply(4),
                        buildStandardOptionDomain.apply(5))),
                Arguments.of(List.of(
                        buildStandardOptionDomain.apply(3),
                        buildStandardOptionDomain.apply(7),
                        buildStandardOptionDomain.apply(4),
                        buildStandardOptionDomain.apply(2))),
                Arguments.of(List.of(
                        buildStandardOptionDomain.apply(2),
                        buildStandardOptionDomain.apply(4),
                        buildStandardOptionDomain.apply(0),
                        buildStandardOptionDomain.apply(5),
                        buildStandardOptionDomain.apply(6))),
                Arguments.of(List.of(
                        buildStandardOptionDomain.apply(2),
                        buildStandardOptionDomain.apply(3),
                        buildStandardOptionDomain.apply(4),
                        buildStandardOptionDomain.apply(5),
                        buildStandardOptionDomain.apply(5),
                        buildStandardOptionDomain.apply(6))),
                Arguments.of(List.of(
                        buildStandardOptionDomain.apply(3),
                        buildStandardOptionDomain.apply(4),
                        buildStandardOptionDomain.apply(5),
                        buildStandardOptionDomain.apply(6))));
    }

    static Stream<Arguments>
            testSuite_should_fail_when_standardOptions_is_missing_optionNumber_and_therefore_out_of_order() {
        final Function<Integer, StandardOptionDomain> buildStandardOptionDomain =
                optionNumber -> DomainBuilder.buildStandardOptionDomain()
                        .optionNumber(optionNumber.longValue())
                        .build();

        return Stream.of(
                Arguments.of(List.of(
                        buildStandardOptionDomain.apply(1),
                        buildStandardOptionDomain.apply(2),
                        buildStandardOptionDomain.apply(4),
                        buildStandardOptionDomain.apply(5),
                        buildStandardOptionDomain.apply(6))),
                Arguments.of(List.of(
                        buildStandardOptionDomain.apply(3),
                        buildStandardOptionDomain.apply(7),
                        buildStandardOptionDomain.apply(4),
                        buildStandardOptionDomain.apply(1),
                        buildStandardOptionDomain.apply(2))),
                Arguments.of(List.of(
                        buildStandardOptionDomain.apply(1),
                        buildStandardOptionDomain.apply(2),
                        buildStandardOptionDomain.apply(4),
                        buildStandardOptionDomain.apply(5),
                        buildStandardOptionDomain.apply(5),
                        buildStandardOptionDomain.apply(6))),
                Arguments.of(List.of(
                        buildStandardOptionDomain.apply(1),
                        buildStandardOptionDomain.apply(2),
                        buildStandardOptionDomain.apply(3),
                        buildStandardOptionDomain.apply(4),
                        buildStandardOptionDomain.apply(5),
                        buildStandardOptionDomain.apply(5),
                        buildStandardOptionDomain.apply(6))),
                Arguments.of(List.of(
                        buildStandardOptionDomain.apply(1),
                        buildStandardOptionDomain.apply(2),
                        buildStandardOptionDomain.apply(5),
                        buildStandardOptionDomain.apply(3),
                        buildStandardOptionDomain.apply(4),
                        buildStandardOptionDomain.apply(5),
                        buildStandardOptionDomain.apply(6))));
    }
}
