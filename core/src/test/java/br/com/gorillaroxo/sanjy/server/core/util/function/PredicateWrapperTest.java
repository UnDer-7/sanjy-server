package br.com.gorillaroxo.sanjy.server.core.util.function;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class PredicateWrapperTest {

    @Test
    void should_return_true_when_predicate_condition_is_met() {
        // Given
        final PredicateWrapper<String, Exception> predicateWrapper = str -> str != null && str.length() > 3;
        final Predicate<String> wrappedPredicate = PredicateWrapper.wrap(predicateWrapper);
        final String testValue = "test";

        // When
        final boolean result = wrappedPredicate.test(testValue);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void should_return_false_when_predicate_condition_is_not_met() {
        // Given
        final PredicateWrapper<String, Exception> predicateWrapper = str -> str != null && str.length() > 10;
        final Predicate<String> wrappedPredicate = PredicateWrapper.wrap(predicateWrapper);
        final String testValue = "test";

        // When
        final boolean result = wrappedPredicate.test(testValue);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void should_wrap_checked_exception_into_exception_wrapper() {
        // Given
        final IOException originalException = new IOException("Test IO exception");
        final PredicateWrapper<String, Exception> predicateWrapper = arg -> {
            throw originalException;
        };
        final Predicate<String> wrappedPredicate = PredicateWrapper.wrap(predicateWrapper);

        // When
        final var throwableAssert = assertThatThrownBy(() -> wrappedPredicate.test("test"));

        // Then
        throwableAssert.isInstanceOf(ExceptionWrapper.class).hasCause(originalException);
    }

    @Test
    void should_preserve_original_exception_as_cause() {
        // Given
        final String originalExceptionMessage = "Original exception message";
        final RuntimeException originalException = new IllegalArgumentException(originalExceptionMessage);
        final PredicateWrapper<String, Exception> predicateWrapper = arg -> {
            throw originalException;
        };
        final Predicate<String> wrappedPredicate = PredicateWrapper.wrap(predicateWrapper);

        // When
        final var throwableAssert = assertThatThrownBy(() -> wrappedPredicate.test("test"));

        // Then
        throwableAssert
                .isInstanceOf(ExceptionWrapper.class)
                .hasCause(originalException)
                .cause()
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(originalExceptionMessage);
    }

    @Test
    void should_handle_null_argument_when_predicate_accepts_null() {
        // Given
        final PredicateWrapper<String, Exception> predicateWrapper = Objects::isNull;
        final Predicate<String> wrappedPredicate = PredicateWrapper.wrap(predicateWrapper);

        // When
        final boolean result = wrappedPredicate.test(null);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void should_wrap_runtime_exception_into_exception_wrapper() {
        // Given
        final NullPointerException originalException = new NullPointerException("Null pointer test");
        final PredicateWrapper<String, Exception> predicateWrapper = arg -> {
            throw originalException;
        };
        final Predicate<String> wrappedPredicate = PredicateWrapper.wrap(predicateWrapper);

        // When
        final var throwableAssert = assertThatThrownBy(() -> wrappedPredicate.test("test"));

        // Then
        throwableAssert
                .isInstanceOf(ExceptionWrapper.class)
                .hasCause(originalException)
                .cause()
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Null pointer test");
    }

    @Test
    void should_allow_multiple_invocations_of_wrapped_predicate() {
        // Given
        final PredicateWrapper<Integer, Exception> predicateWrapper = num -> num > 0;
        final Predicate<Integer> wrappedPredicate = PredicateWrapper.wrap(predicateWrapper);

        // When
        final boolean result1 = wrappedPredicate.test(5);
        final boolean result2 = wrappedPredicate.test(-3);
        final boolean result3 = wrappedPredicate.test(0);

        // Then
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
        assertThat(result3).isFalse();
    }

    @Test
    void should_work_with_complex_predicate_logic() {
        // Given
        final PredicateWrapper<String, Exception> predicateWrapper =
                str -> str != null && str.startsWith("test") && str.length() > 5;
        final Predicate<String> wrappedPredicate = PredicateWrapper.wrap(predicateWrapper);

        // When
        final boolean result1 = wrappedPredicate.test("testing");
        final boolean result2 = wrappedPredicate.test("test");
        final boolean result3 = wrappedPredicate.test("other");

        // Then
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
        assertThat(result3).isFalse();
    }
}
