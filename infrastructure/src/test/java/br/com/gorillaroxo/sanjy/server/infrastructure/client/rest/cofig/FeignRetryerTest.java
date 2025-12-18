package br.com.gorillaroxo.sanjy.server.infrastructure.client.rest.cofig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.com.gorillaroxo.sanjy.server.core.ports.driven.SanjyServerProps;
import feign.RetryableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeignRetryerTest {

    @Mock
    SanjyServerProps.ExternalHttpClientsRetryConfigProp retryConfig;

    @Test
    void should_retry_when_attempt_is_less_than_max_attempt() {
        // Given
        when(retryConfig.maxAttempt()).thenReturn(3);
        when(retryConfig.interval()).thenReturn(100);
        when(retryConfig.backoffMultiplier()).thenReturn(2);

        final var retryer = new FeignRetryer(retryConfig);
        final var exception = mock(RetryableException.class);
        when(exception.getMessage()).thenReturn("Test endpoint");

        // When & Then
        assertThatCode(() -> retryer.continueOrPropagate(exception)).doesNotThrowAnyException();
    }

    @Test
    void should_throw_exception_when_max_attempt_is_reached() {
        // Given
        when(retryConfig.maxAttempt()).thenReturn(1);

        final var retryer = new FeignRetryer(retryConfig);
        final var exception = mock(RetryableException.class);
        when(exception.getMessage()).thenReturn("Test endpoint");

        // When
        final var throwableAssert = assertThatThrownBy(() -> retryer.continueOrPropagate(exception));

        // Then
        throwableAssert.isInstanceOf(RetryableException.class).isSameAs(exception);
    }

    @Test
    void should_increment_attempt_counter_on_each_retry() {
        // Given
        when(retryConfig.maxAttempt()).thenReturn(3);
        when(retryConfig.interval()).thenReturn(10);
        when(retryConfig.backoffMultiplier()).thenReturn(1);

        final var retryer = new FeignRetryer(retryConfig);
        final var exception = mock(RetryableException.class);
        when(exception.getMessage()).thenReturn("Test endpoint");

        // When
        retryer.continueOrPropagate(exception);

        // Then - second attempt should not throw
        assertThatCode(() -> retryer.continueOrPropagate(exception)).doesNotThrowAnyException();

        // When - third attempt should throw (maxAttempt = 3)
        final var throwableAssert = assertThatThrownBy(() -> retryer.continueOrPropagate(exception));

        // Then
        throwableAssert.isInstanceOf(RetryableException.class);
    }

    @Test
    void should_calculate_retry_interval_with_exponential_backoff() {
        // Given
        when(retryConfig.maxAttempt()).thenReturn(5);
        when(retryConfig.interval()).thenReturn(100);
        when(retryConfig.backoffMultiplier()).thenReturn(2);

        final var retryer = new FeignRetryer(retryConfig);
        final var exception = mock(RetryableException.class);
        when(exception.getMessage()).thenReturn("Test endpoint");

        // When - First retry (interval should be 100 * 2^2 = 400ms based on attempt = 2 after first call)
        final long startTime = System.currentTimeMillis();
        retryer.continueOrPropagate(exception);
        final long elapsedTime = System.currentTimeMillis() - startTime;

        // Then - Should have waited at least close to the calculated interval
        assertThat(elapsedTime).isGreaterThanOrEqualTo(350L); // Allow some margin
    }

    @Test
    void should_clone_retryer_with_same_configuration() {
        // Given

        final var originalRetryer = new FeignRetryer(retryConfig);

        // When
        final var clonedRetryer = originalRetryer.clone();

        // Then
        assertThat(clonedRetryer).isNotNull().isNotSameAs(originalRetryer).isInstanceOf(FeignRetryer.class);
    }

    @Test
    void should_reset_attempt_counter_on_clone() {
        // Given
        when(retryConfig.maxAttempt()).thenReturn(5);
        when(retryConfig.interval()).thenReturn(10);
        when(retryConfig.backoffMultiplier()).thenReturn(1);

        final var originalRetryer = new FeignRetryer(retryConfig);
        final var exception = mock(RetryableException.class);
        when(exception.getMessage()).thenReturn("Test endpoint");

        // Increment attempt counter
        originalRetryer.continueOrPropagate(exception);
        originalRetryer.continueOrPropagate(exception);

        // When
        final var clonedRetryer = (FeignRetryer) originalRetryer.clone();

        // Then - cloned retryer should start from attempt 1 again
        assertThatCode(() -> {
                    clonedRetryer.continueOrPropagate(exception);
                    clonedRetryer.continueOrPropagate(exception);
                    clonedRetryer.continueOrPropagate(exception);
                    clonedRetryer.continueOrPropagate(exception);
                })
                .doesNotThrowAnyException();
    }

    @Test
    void should_handle_interrupted_exception_during_sleep() {
        // Given
        when(retryConfig.maxAttempt()).thenReturn(3);
        when(retryConfig.interval()).thenReturn(10000); // Long sleep to interrupt
        when(retryConfig.backoffMultiplier()).thenReturn(1);

        final var retryer = new FeignRetryer(retryConfig);
        final var exception = mock(RetryableException.class);
        when(exception.getMessage()).thenReturn("Test endpoint");

        // Interrupt the current thread before retry
        Thread.currentThread().interrupt();

        // When
        assertThatCode(() -> retryer.continueOrPropagate(exception)).doesNotThrowAnyException();

        // Then - Thread interrupt flag should still be set
        assertThat(Thread.interrupted()).isTrue();
    }

    @Test
    void should_apply_backoff_multiplier_correctly() {
        // Given
        when(retryConfig.maxAttempt()).thenReturn(4);
        when(retryConfig.interval()).thenReturn(50);
        when(retryConfig.backoffMultiplier()).thenReturn(3);

        final var retryer = new FeignRetryer(retryConfig);
        final var exception = mock(RetryableException.class);
        when(exception.getMessage()).thenReturn("Test endpoint");

        // When - First retry (attempt = 2, interval = 50 * 3^2 = 450ms)
        final long startTime1 = System.currentTimeMillis();
        retryer.continueOrPropagate(exception);
        final long elapsed1 = System.currentTimeMillis() - startTime1;

        // When - Second retry (attempt = 3, interval = 50 * 3^3 = 1350ms)
        final long startTime2 = System.currentTimeMillis();
        retryer.continueOrPropagate(exception);
        final long elapsed2 = System.currentTimeMillis() - startTime2;

        // Then - Second retry should take longer due to exponential backoff
        assertThat(elapsed1).isGreaterThanOrEqualTo(400L);
        assertThat(elapsed2).isGreaterThanOrEqualTo(1300L);
        assertThat(elapsed2).isGreaterThan(elapsed1);
    }
}
