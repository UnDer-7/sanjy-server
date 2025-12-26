package br.com.gorillaroxo.sanjy.server.core.util.function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SupplierWrapperTest {

    @Test
    void should_return_value_successfully_when_no_exception_is_thrown() {
        // Given
        final String expectedValue = "test value";
        final SupplierWrapper<String, Exception> supplierWrapper = () -> expectedValue;
        final Supplier<String> wrappedSupplier = SupplierWrapper.wrap(supplierWrapper);

        // When
        final String result = wrappedSupplier.get();

        // Then
        assertThat(result).isEqualTo(expectedValue);
    }

    @Test
    void should_return_computed_value() {
        // Given
        final SupplierWrapper<Integer, Exception> supplierWrapper = () -> 2 + 2;
        final Supplier<Integer> wrappedSupplier = SupplierWrapper.wrap(supplierWrapper);

        // When
        final Integer result = wrappedSupplier.get();

        // Then
        assertThat(result).isEqualTo(4);
    }

    @Test
    void should_wrap_checked_exception_into_exception_wrapper() {
        // Given
        final IOException originalException = new IOException("Test IO exception");
        final SupplierWrapper<String, Exception> supplierWrapper = () -> {
            throw originalException;
        };
        final Supplier<String> wrappedSupplier = SupplierWrapper.wrap(supplierWrapper);

        // When
        final var throwableAssert = assertThatThrownBy(wrappedSupplier::get);

        // Then
        throwableAssert.isInstanceOf(ExceptionWrapper.class).hasCause(originalException);
    }

    @Test
    void should_preserve_original_exception_as_cause() {
        // Given
        final String originalExceptionMessage = "Original exception message";
        final RuntimeException originalException = new IllegalArgumentException(originalExceptionMessage);
        final SupplierWrapper<String, Exception> supplierWrapper = () -> {
            throw originalException;
        };
        final Supplier<String> wrappedSupplier = SupplierWrapper.wrap(supplierWrapper);

        // When
        final var throwableAssert = assertThatThrownBy(wrappedSupplier::get);

        // Then
        throwableAssert
                .isInstanceOf(ExceptionWrapper.class)
                .hasCause(originalException)
                .cause()
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(originalExceptionMessage);
    }

    @Test
    void should_return_null_value_when_supplier_returns_null() {
        // Given
        final SupplierWrapper<String, Exception> supplierWrapper = () -> null;
        final Supplier<String> wrappedSupplier = SupplierWrapper.wrap(supplierWrapper);

        // When
        final String result = wrappedSupplier.get();

        // Then
        assertThat(result).isNull();
    }

    @Test
    void should_wrap_runtime_exception_into_exception_wrapper() {
        // Given
        final NullPointerException originalException = new NullPointerException("Null pointer test");
        final SupplierWrapper<Integer, Exception> supplierWrapper = () -> {
            throw originalException;
        };
        final Supplier<Integer> wrappedSupplier = SupplierWrapper.wrap(supplierWrapper);

        // When
        final var throwableAssert = assertThatThrownBy(wrappedSupplier::get);

        // Then
        throwableAssert
                .isInstanceOf(ExceptionWrapper.class)
                .hasCause(originalException)
                .cause()
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Null pointer test");
    }

    @Test
    void should_allow_multiple_invocations_of_wrapped_supplier() {
        // Given
        final AtomicInteger counter = new AtomicInteger(0);
        final SupplierWrapper<Integer, Exception> supplierWrapper = counter::incrementAndGet;
        final Supplier<Integer> wrappedSupplier = SupplierWrapper.wrap(supplierWrapper);

        // When
        final Integer result1 = wrappedSupplier.get();
        final Integer result2 = wrappedSupplier.get();
        final Integer result3 = wrappedSupplier.get();

        // Then
        assertThat(result1).isEqualTo(1);
        assertThat(result2).isEqualTo(2);
        assertThat(result3).isEqualTo(3);
    }

    @Test
    void should_work_with_complex_object_types() {
        // Given
        final SupplierWrapper<Instant, Exception> supplierWrapper = Instant::now;
        final Supplier<Instant> wrappedSupplier = SupplierWrapper.wrap(supplierWrapper);

        // When
        final Instant result = wrappedSupplier.get();

        // Then
        assertThat(result).isNotNull().isInstanceOf(Instant.class);
    }
}
