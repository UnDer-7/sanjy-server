package br.com.gorillaroxo.sanjy.server.core.util.function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FunctionWrapperTest {

    @Test
    void should_execute_function_successfully_when_no_exception_is_thrown() {
        // Given
        final FunctionWrapper<String, Integer, Exception> functionWrapper = String::length;
        final Function<String, Integer> wrappedFunction = FunctionWrapper.wrap(functionWrapper);
        final String testValue = "test";

        // When
        final Integer result = wrappedFunction.apply(testValue);

        // Then
        assertThat(result).isEqualTo(4);
    }

    @Test
    void should_return_correct_result_when_function_executes_successfully() {
        // Given
        final FunctionWrapper<Integer, String, Exception> functionWrapper = num -> "Number: " + num;
        final Function<Integer, String> wrappedFunction = FunctionWrapper.wrap(functionWrapper);

        // When
        final String result = wrappedFunction.apply(42);

        // Then
        assertThat(result).isEqualTo("Number: 42");
    }

    @Test
    void should_wrap_checked_exception_into_exception_wrapper() {
        // Given
        final IOException originalException = new IOException("Test IO exception");
        final FunctionWrapper<String, String, Exception> functionWrapper = arg -> {
            throw originalException;
        };
        final Function<String, String> wrappedFunction = FunctionWrapper.wrap(functionWrapper);

        // When
        final var throwableAssert = assertThatThrownBy(() -> wrappedFunction.apply("test"));

        // Then
        throwableAssert.isInstanceOf(ExceptionWrapper.class).hasCause(originalException);
    }

    @Test
    void should_preserve_original_exception_as_cause() {
        // Given
        final String originalExceptionMessage = "Original exception message";
        final RuntimeException originalException = new IllegalArgumentException(originalExceptionMessage);
        final FunctionWrapper<String, Integer, Exception> functionWrapper = arg -> {
            throw originalException;
        };
        final Function<String, Integer> wrappedFunction = FunctionWrapper.wrap(functionWrapper);

        // When
        final var throwableAssert = assertThatThrownBy(() -> wrappedFunction.apply("test"));

        // Then
        throwableAssert
                .isInstanceOf(ExceptionWrapper.class)
                .hasCause(originalException)
                .cause()
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(originalExceptionMessage);
    }

    @Test
    void should_handle_null_argument_when_function_accepts_null() {
        // Given
        final FunctionWrapper<String, String, Exception> functionWrapper =
                arg -> arg == null ? "null received" : arg.toUpperCase();
        final Function<String, String> wrappedFunction = FunctionWrapper.wrap(functionWrapper);

        // When
        final String result = wrappedFunction.apply(null);

        // Then
        assertThat(result).isEqualTo("null received");
    }

    @Test
    void should_handle_null_return_value() {
        // Given
        final FunctionWrapper<String, String, Exception> functionWrapper = arg -> null;
        final Function<String, String> wrappedFunction = FunctionWrapper.wrap(functionWrapper);

        // When
        final String result = wrappedFunction.apply("test");

        // Then
        assertThat(result).isNull();
    }

    @Test
    void should_wrap_runtime_exception_into_exception_wrapper() {
        // Given
        final NullPointerException originalException = new NullPointerException("Null pointer test");
        final FunctionWrapper<String, Integer, Exception> functionWrapper = arg -> {
            throw originalException;
        };
        final Function<String, Integer> wrappedFunction = FunctionWrapper.wrap(functionWrapper);

        // When
        final var throwableAssert = assertThatThrownBy(() -> wrappedFunction.apply("test"));

        // Then
        throwableAssert
                .isInstanceOf(ExceptionWrapper.class)
                .hasCause(originalException)
                .cause()
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Null pointer test");
    }

    @Test
    void should_allow_multiple_invocations_of_wrapped_function() {
        // Given
        final FunctionWrapper<Integer, Integer, Exception> functionWrapper = num -> num * 2;
        final Function<Integer, Integer> wrappedFunction = FunctionWrapper.wrap(functionWrapper);

        // When
        final Integer result1 = wrappedFunction.apply(5);
        final Integer result2 = wrappedFunction.apply(10);
        final Integer result3 = wrappedFunction.apply(15);

        // Then
        assertThat(result1).isEqualTo(10);
        assertThat(result2).isEqualTo(20);
        assertThat(result3).isEqualTo(30);
    }
}
