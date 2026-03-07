package br.com.gorillaroxo.sanjy.server.core.util.function;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ConsumerWrapperTest {

    @Test
    void should_execute_consumer_successfully_when_no_exception_is_thrown() {
        // Given
        final List<String> capturedValues = new ArrayList<>();
        final ConsumerWrapper<String, Exception> consumerWrapper = capturedValues::add;
        final Consumer<String> wrappedConsumer = ConsumerWrapper.wrap(consumerWrapper);
        final String testValue = "test value";

        // When
        assertThatCode(() -> wrappedConsumer.accept(testValue)).doesNotThrowAnyException();

        // Then
        assertThat(capturedValues).containsExactly(testValue);
    }

    @Test
    void should_wrap_checked_exception_into_exception_wrapper() {
        // Given
        final IOException originalException = new IOException("Test IO exception");
        final ConsumerWrapper<String, Exception> consumerWrapper = arg -> {
            throw originalException;
        };
        final Consumer<String> wrappedConsumer = ConsumerWrapper.wrap(consumerWrapper);

        // When
        final var throwableAssert = assertThatThrownBy(() -> wrappedConsumer.accept("test"));

        // Then
        throwableAssert.isInstanceOf(ExceptionWrapper.class).hasCause(originalException);
    }

    @Test
    void should_preserve_original_exception_as_cause() {
        // Given
        final String originalExceptionMessage = "Original exception message";
        final RuntimeException originalException = new IllegalArgumentException(originalExceptionMessage);
        final ConsumerWrapper<String, Exception> consumerWrapper = arg -> {
            throw originalException;
        };
        final Consumer<String> wrappedConsumer = ConsumerWrapper.wrap(consumerWrapper);

        // When
        final var throwableAssert = assertThatThrownBy(() -> wrappedConsumer.accept("test"));

        // Then
        throwableAssert
                .isInstanceOf(ExceptionWrapper.class)
                .hasCause(originalException)
                .cause()
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(originalExceptionMessage);
    }

    @Test
    void should_handle_null_argument_without_exception_when_consumer_accepts_null() {
        // Given
        final List<String> capturedValues = new ArrayList<>();
        final ConsumerWrapper<String, Exception> consumerWrapper = capturedValues::add;
        final Consumer<String> wrappedConsumer = ConsumerWrapper.wrap(consumerWrapper);

        // When
        assertThatCode(() -> wrappedConsumer.accept(null)).doesNotThrowAnyException();

        // Then
        assertThat(capturedValues).containsExactly((String) null);
    }

    @Test
    void should_wrap_runtime_exception_into_exception_wrapper() {
        // Given
        final NullPointerException originalException = new NullPointerException("Null pointer test");
        final ConsumerWrapper<String, Exception> consumerWrapper = arg -> {
            throw originalException;
        };
        final Consumer<String> wrappedConsumer = ConsumerWrapper.wrap(consumerWrapper);

        // When
        final var throwableAssert = assertThatThrownBy(() -> wrappedConsumer.accept("test"));

        // Then
        throwableAssert
                .isInstanceOf(ExceptionWrapper.class)
                .hasCause(originalException)
                .cause()
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Null pointer test");
    }

    @Test
    void should_allow_multiple_invocations_of_wrapped_consumer() {
        // Given
        final List<Integer> capturedValues = new ArrayList<>();
        final ConsumerWrapper<Integer, Exception> consumerWrapper = capturedValues::add;
        final Consumer<Integer> wrappedConsumer = ConsumerWrapper.wrap(consumerWrapper);

        // When
        assertThatCode(() -> {
                    wrappedConsumer.accept(1);
                    wrappedConsumer.accept(2);
                    wrappedConsumer.accept(3);
                })
                .doesNotThrowAnyException();

        // Then
        assertThat(capturedValues).containsExactly(1, 2, 3);
    }
}
