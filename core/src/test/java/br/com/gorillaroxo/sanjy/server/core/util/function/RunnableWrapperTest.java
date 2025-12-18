package br.com.gorillaroxo.sanjy.server.core.util.function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RunnableWrapperTest {

    @Test
    void should_execute_runnable_successfully_when_no_exception_is_thrown() {
        // Given
        final List<String> capturedValues = new ArrayList<>();
        final RunnableWrapper<Exception> runnableWrapper = () -> capturedValues.add("executed");
        final Runnable wrappedRunnable = RunnableWrapper.wrap(runnableWrapper);

        // When
        assertThatCode(wrappedRunnable::run).doesNotThrowAnyException();

        // Then
        assertThat(capturedValues).containsExactly("executed");
    }

    @Test
    void should_execute_side_effect_without_exception() {
        // Given
        final AtomicInteger counter = new AtomicInteger(0);
        final RunnableWrapper<Exception> runnableWrapper = counter::incrementAndGet;
        final Runnable wrappedRunnable = RunnableWrapper.wrap(runnableWrapper);

        // When
        wrappedRunnable.run();

        // Then
        assertThat(counter.get()).isEqualTo(1);
    }

    @Test
    void should_wrap_checked_exception_into_exception_wrapper() {
        // Given
        final IOException originalException = new IOException("Test IO exception");
        final RunnableWrapper<Exception> runnableWrapper = () -> {
            throw originalException;
        };
        final Runnable wrappedRunnable = RunnableWrapper.wrap(runnableWrapper);

        // When
        final var throwableAssert = assertThatThrownBy(wrappedRunnable::run);

        // Then
        throwableAssert.isInstanceOf(ExceptionWrapper.class).hasCause(originalException);
    }

    @Test
    void should_preserve_original_exception_as_cause() {
        // Given
        final String originalExceptionMessage = "Original exception message";
        final RuntimeException originalException = new IllegalArgumentException(originalExceptionMessage);
        final RunnableWrapper<Exception> runnableWrapper = () -> {
            throw originalException;
        };
        final Runnable wrappedRunnable = RunnableWrapper.wrap(runnableWrapper);

        // When
        final var throwableAssert = assertThatThrownBy(wrappedRunnable::run);

        // Then
        throwableAssert
                .isInstanceOf(ExceptionWrapper.class)
                .hasCause(originalException)
                .cause()
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(originalExceptionMessage);
    }

    @Test
    void should_wrap_runtime_exception_into_exception_wrapper() {
        // Given
        final NullPointerException originalException = new NullPointerException("Null pointer test");
        final RunnableWrapper<Exception> runnableWrapper = () -> {
            throw originalException;
        };
        final Runnable wrappedRunnable = RunnableWrapper.wrap(runnableWrapper);

        // When
        final var throwableAssert = assertThatThrownBy(wrappedRunnable::run);

        // Then
        throwableAssert
                .isInstanceOf(ExceptionWrapper.class)
                .hasCause(originalException)
                .cause()
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Null pointer test");
    }

    @Test
    void should_allow_multiple_invocations_of_wrapped_runnable() {
        // Given
        final AtomicInteger counter = new AtomicInteger(0);
        final RunnableWrapper<Exception> runnableWrapper = counter::incrementAndGet;
        final Runnable wrappedRunnable = RunnableWrapper.wrap(runnableWrapper);

        // When
        assertThatCode(() -> {
                    wrappedRunnable.run();
                    wrappedRunnable.run();
                    wrappedRunnable.run();
                })
                .doesNotThrowAnyException();

        // Then
        assertThat(counter.get()).isEqualTo(3);
    }

    @Test
    void should_execute_empty_runnable_without_exception() {
        // Given
        final RunnableWrapper<Exception> runnableWrapper = () -> {
            // Empty implementation - do nothing
        };
        final Runnable wrappedRunnable = RunnableWrapper.wrap(runnableWrapper);

        // When & Then
        assertThatCode(wrappedRunnable::run).doesNotThrowAnyException();
    }
}
