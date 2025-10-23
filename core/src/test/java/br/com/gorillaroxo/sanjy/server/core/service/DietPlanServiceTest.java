package br.com.gorillaroxo.sanjy.server.core.service;

import br.com.gorillaroxo.sanjy.server.core.domain.DietPlanDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.StandardOptionDomain;
import br.com.gorillaroxo.sanjy.server.core.exception.StandardOptionsNotInSequence;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.DietPlanGateway;
import br.com.gorillaroxo.sanjy.server.core.test.DomainBuilder;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

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
    @ValueSource(ints = { 0, 2, 3, 4, 5, 6, 7, 8, 9, 10 })
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
        abstractThrowableAssert.isInstanceOf(StandardOptionsNotInSequence.class)
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
        abstractThrowableAssert.isInstanceOf(StandardOptionsNotInSequence.class)
            .hasMessageContaining("StandardOptions must start with number 1");
        verify(dietPlanGateway, never()).insert(any());
    }


    @ParameterizedTest
    @MethodSource("testSuite_should_fail_when_standardOptions_is_missing_optionNumber_and_therefore_out_of_order")
    void should_fail_when_standardOptions_is_missing_optionNumber_and_therefore_out_of_order(final List<StandardOptionDomain> standardOptions) {
        // Given
        final DietPlanDomain dietPlan = DomainBuilder.buildDietPlanDomain()
            .mealTypes(List.of(DomainBuilder.buildMealTypeDomain()
                .standardOptions(standardOptions)
                .build()))
            .build();

        // When
        final var abstractThrowableAssert = assertThatThrownBy(() -> dietPlanService.insert(dietPlan));

        // Then
        abstractThrowableAssert.isInstanceOf(StandardOptionsNotInSequence.class)
            .hasMessageContaining("StandardOptions with non-sequential numbers (skipped or repeated numbers detected)");
        verify(dietPlanGateway, never()).insert(any());
    }

    static Stream<Arguments> testSuite_should_fail_when_first_standardOptions_is_not_number_1_list() {
        final Function<Integer, StandardOptionDomain> buildStandardOptionDomain = optionNumber -> DomainBuilder.buildStandardOptionDomain()
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

    static Stream<Arguments> testSuite_should_fail_when_standardOptions_is_missing_optionNumber_and_therefore_out_of_order() {
        final Function<Integer, StandardOptionDomain> buildStandardOptionDomain = optionNumber -> DomainBuilder.buildStandardOptionDomain()
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