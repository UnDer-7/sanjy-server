package br.com.gorillaroxo.sanjy.server.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

@ExtendWith(MockitoExtension.class)
class ThreadUtilsTest {

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void should_throw_exception_when_instantiating_utility_class() {
        // Given & When
        final var throwableAssert = assertThatThrownBy(() -> {
            final var constructor = ThreadUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });

        // Then
        throwableAssert.hasCauseInstanceOf(IllegalStateException.class).cause().hasMessage("Utility class");
    }

    @Nested
    class RunAsyncWithMdcTests {

        @Test
        void should_throw_null_pointer_exception_when_runnable_is_null_with_executor() {
            // Given
            final Runnable runnable = null;
            final Executor executor = mock(Executor.class);

            // When
            final var throwableAssert = assertThatThrownBy(() -> ThreadUtils.runAsyncWithMdc(runnable, executor));

            // Then
            throwableAssert.isInstanceOf(NullPointerException.class);
        }

        @Test
        void should_throw_null_pointer_exception_when_executor_is_null() {
            // Given
            final Runnable runnable = () -> {};
            final Executor executor = null;

            // When
            final var throwableAssert = assertThatThrownBy(() -> ThreadUtils.runAsyncWithMdc(runnable, executor));

            // Then
            throwableAssert.isInstanceOf(NullPointerException.class);
        }

        @Test
        void should_throw_null_pointer_exception_when_runnable_is_null_without_executor() {
            // Given
            final Runnable runnable = null;

            // When
            final var throwableAssert = assertThatThrownBy(() -> ThreadUtils.runAsyncWithMdc(runnable));

            // Then
            throwableAssert.isInstanceOf(NullPointerException.class);
        }

        @Test
        void should_execute_runnable_with_executor_and_preserve_mdc_context() {
            // Given
            final String mdcKey = "requestId";
            final String mdcValue = "12345";
            MDC.put(mdcKey, mdcValue);

            final AtomicReference<String> capturedMdcValue = new AtomicReference<>();
            final Executor executor = Runnable::run;

            final Runnable runnable = () -> capturedMdcValue.set(MDC.get(mdcKey));

            // When
            ThreadUtils.runAsyncWithMdc(runnable, executor);

            // Then
            await().atMost(1, TimeUnit.SECONDS)
                    .untilAsserted(() -> assertThat(capturedMdcValue.get()).isEqualTo(mdcValue));
        }

        @Test
        void should_execute_runnable_without_executor_and_preserve_mdc_context() {
            // Given
            final String mdcKey = "requestId";
            final String mdcValue = "12345";
            MDC.put(mdcKey, mdcValue);

            final AtomicReference<String> capturedMdcValue = new AtomicReference<>();
            final Runnable runnable = () -> capturedMdcValue.set(MDC.get(mdcKey));

            // When
            ThreadUtils.runAsyncWithMdc(runnable);

            // Then
            await().atMost(1, TimeUnit.SECONDS)
                    .untilAsserted(() -> assertThat(capturedMdcValue.get()).isEqualTo(mdcValue));
        }

        @Test
        void should_handle_empty_mdc_context_when_running_async_with_executor() {
            // Given
            MDC.clear();
            final AtomicInteger counter = new AtomicInteger(0);
            final Executor executor = Runnable::run;
            final Runnable runnable = counter::incrementAndGet;

            // When
            ThreadUtils.runAsyncWithMdc(runnable, executor);

            // Then
            await().atMost(1, TimeUnit.SECONDS)
                    .untilAsserted(() -> assertThat(counter.get()).isEqualTo(1));
        }

        @Test
        void should_handle_empty_mdc_context_when_running_async_without_executor() {
            // Given
            MDC.clear();
            final AtomicInteger counter = new AtomicInteger(0);
            final Runnable runnable = counter::incrementAndGet;

            // When
            ThreadUtils.runAsyncWithMdc(runnable);

            // Then
            await().atMost(1, TimeUnit.SECONDS)
                    .untilAsserted(() -> assertThat(counter.get()).isEqualTo(1));
        }

        @Test
        void should_clear_mdc_after_runnable_execution_with_executor() {
            // Given
            final String mdcKey = "requestId";
            final String mdcValue = "12345";
            MDC.put(mdcKey, mdcValue);

            final AtomicReference<Map<String, String>> mdcAfterExecution = new AtomicReference<>();
            final Executor executor = Runnable::run;

            final Runnable runnable = () -> {
                // Simulate some work
            };

            // When
            final Executor trackingExecutor = task -> executor.execute(() -> {
                task.run();
                mdcAfterExecution.set(MDC.getCopyOfContextMap());
            });

            ThreadUtils.runAsyncWithMdc(runnable, trackingExecutor);

            // Then
            await().atMost(1, TimeUnit.SECONDS)
                    .untilAsserted(() -> assertThat(mdcAfterExecution.get()).isNullOrEmpty());
        }

        @Test
        void should_use_provided_executor_when_running_async() {
            // Given
            final Executor executor = mock(Executor.class);
            doAnswer(invocation -> {
                        final Runnable task = invocation.getArgument(0);
                        task.run();
                        return null;
                    })
                    .when(executor)
                    .execute(any(Runnable.class));

            final Runnable runnable = () -> {};

            // When
            ThreadUtils.runAsyncWithMdc(runnable, executor);

            // Then
            verify(executor).execute(any(Runnable.class));
        }

        @Test
        void should_preserve_multiple_mdc_values_when_running_async() {
            // Given
            final Map<String, String> mdcValues = new HashMap<>();
            mdcValues.put("requestId", "12345");
            mdcValues.put("userId", "user-abc");
            mdcValues.put("traceId", "trace-xyz");
            mdcValues.forEach(MDC::put);

            final AtomicReference<Map<String, String>> capturedMdc = new AtomicReference<>();
            final Executor executor = Runnable::run;
            final Runnable runnable = () -> capturedMdc.set(MDC.getCopyOfContextMap());

            // When
            ThreadUtils.runAsyncWithMdc(runnable, executor);

            // Then
            await().atMost(1, TimeUnit.SECONDS)
                    .untilAsserted(() -> assertThat(capturedMdc.get()).containsAllEntriesOf(mdcValues));
        }
    }

    @Nested
    class SupplyAsyncWithMdcTests {

        @Test
        void should_throw_null_pointer_exception_when_supplier_is_null_with_executor() {
            // Given
            final Executor executor = mock(Executor.class);

            // When
            final var throwableAssert = assertThatThrownBy(() -> ThreadUtils.supplyAsyncWithMdc(null, executor));

            // Then
            throwableAssert.isInstanceOf(NullPointerException.class);
        }

        @Test
        void should_throw_null_pointer_exception_when_executor_is_null() {
            // Given
            final Executor executor = null;

            // When
            final var throwableAssert =
                    assertThatThrownBy(() -> ThreadUtils.supplyAsyncWithMdc(() -> "result", executor));

            // Then
            throwableAssert.isInstanceOf(NullPointerException.class);
        }

        @Test
        void should_throw_null_pointer_exception_when_supplier_is_null_without_executor() {
            // Given & When
            final var throwableAssert = assertThatThrownBy(() -> ThreadUtils.supplyAsyncWithMdc(null));

            // Then
            throwableAssert.isInstanceOf(NullPointerException.class);
        }

        @Test
        void should_execute_supplier_with_executor_and_preserve_mdc_context() throws Exception {
            // Given
            final String mdcKey = "requestId";
            final String mdcValue = "12345";
            MDC.put(mdcKey, mdcValue);

            final Executor executor = Runnable::run;

            // When
            final CompletableFuture<String> future = ThreadUtils.supplyAsyncWithMdc(() -> MDC.get(mdcKey), executor);

            // Then
            assertThat(future.get()).isEqualTo(mdcValue);
        }

        @Test
        void should_execute_supplier_without_executor_and_preserve_mdc_context() throws Exception {
            // Given
            final String mdcKey = "requestId";
            final String mdcValue = "12345";
            MDC.put(mdcKey, mdcValue);

            // When
            final CompletableFuture<String> future = ThreadUtils.supplyAsyncWithMdc(() -> MDC.get(mdcKey));

            // Then
            assertThat(future.get()).isEqualTo(mdcValue);
        }

        @Test
        void should_handle_empty_mdc_context_when_supplying_async_with_executor() throws Exception {
            // Given
            MDC.clear();
            final Executor executor = Runnable::run;

            // When
            final CompletableFuture<String> future = ThreadUtils.supplyAsyncWithMdc(() -> "result", executor);

            // Then
            assertThat(future.get()).isEqualTo("result");
        }

        @Test
        void should_handle_empty_mdc_context_when_supplying_async_without_executor() throws Exception {
            // Given
            MDC.clear();

            // When
            final CompletableFuture<String> future = ThreadUtils.supplyAsyncWithMdc(() -> "result");

            // Then
            assertThat(future.get()).isEqualTo("result");
        }

        @Test
        void should_return_completable_future_with_supplier_result() throws Exception {
            // Given
            final String expectedResult = "computed value";
            final Executor executor = Runnable::run;

            // When
            final CompletableFuture<String> future = ThreadUtils.supplyAsyncWithMdc(() -> expectedResult, executor);

            // Then
            assertThat(future).isNotNull().isCompleted();
            assertThat(future.get()).isEqualTo(expectedResult);
        }
    }
}
