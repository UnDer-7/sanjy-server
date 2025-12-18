package br.com.gorillaroxo.sanjy.server.core.util.function;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExceptionWrapperTest {

    @Test
    void should_create_exception_wrapper_with_throwable_cause() {
        // Given
        final String errorMessage = "Original error message";
        final IOException originalException = new IOException(errorMessage);

        // When
        final ExceptionWrapper exceptionWrapper = new ExceptionWrapper(originalException);

        // Then
        assertThat(exceptionWrapper).isInstanceOf(RuntimeException.class).hasCause(originalException);
    }

    @Test
    void should_preserve_original_exception_message_in_cause() {
        // Given
        final String expectedMessage = "Database connection failed";
        final RuntimeException originalException = new IllegalStateException(expectedMessage);

        // When
        final ExceptionWrapper exceptionWrapper = new ExceptionWrapper(originalException);

        // Then
        assertThat(exceptionWrapper.getCause())
                .isNotNull()
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    void should_wrap_checked_exception() {
        // Given
        final Exception checkedException = new Exception("Checked exception");

        // When
        final ExceptionWrapper exceptionWrapper = new ExceptionWrapper(checkedException);

        // Then
        assertThat(exceptionWrapper)
                .isInstanceOf(RuntimeException.class)
                .hasCause(checkedException)
                .cause()
                .hasMessage("Checked exception");
    }

    @Test
    void should_wrap_runtime_exception() {
        // Given
        final RuntimeException runtimeException = new NullPointerException("Null value encountered");

        // When
        final ExceptionWrapper exceptionWrapper = new ExceptionWrapper(runtimeException);

        // Then
        assertThat(exceptionWrapper)
                .isInstanceOf(RuntimeException.class)
                .hasCause(runtimeException)
                .cause()
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Null value encountered");
    }

    @Test
    void should_maintain_exception_chain() {
        // Given
        final IOException rootCause = new IOException("Root cause");
        final RuntimeException intermediateCause = new RuntimeException("Intermediate cause", rootCause);

        // When
        final ExceptionWrapper exceptionWrapper = new ExceptionWrapper(intermediateCause);

        // Then
        assertThat(exceptionWrapper)
                .hasCause(intermediateCause)
                .cause()
                .hasCause(rootCause)
                .cause()
                .isInstanceOf(IOException.class)
                .hasMessage("Root cause");
    }
}
