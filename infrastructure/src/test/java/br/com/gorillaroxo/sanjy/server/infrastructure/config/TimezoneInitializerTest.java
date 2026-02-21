package br.com.gorillaroxo.sanjy.server.infrastructure.config;

import java.time.ZoneOffset;
import java.util.TimeZone;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ConfigurableApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@ExtendWith(MockitoExtension.class)
class TimezoneInitializerTest {

    @Mock
    ConfigurableApplicationContext applicationContext;

    @InjectMocks
    TimezoneInitializer timezoneInitializer;

    TimeZone originalTimeZone;

    @BeforeEach
    void setUp() {
        // Store original timezone to restore after test
        originalTimeZone = TimeZone.getDefault();
    }

    @AfterEach
    void tearDown() {
        // Restore original timezone after each test
        TimeZone.setDefault(originalTimeZone);
    }

    @Test
    @DisplayName("Should set JVM default timezone to UTC when initialize is called")
    void should_set_jvm_default_timezone_to_utc() {
        // Given
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York")); // Set non-UTC timezone

        // When
        timezoneInitializer.initialize(applicationContext);

        // Then
        TimeZone defaultTimeZone = TimeZone.getDefault();
        assertThat(defaultTimeZone.getID()).isEqualTo("UTC");
        assertThat(defaultTimeZone.getRawOffset()).isZero();
    }

    @Test
    @DisplayName("Should set timezone to UTC matching ZoneOffset.UTC")
    void should_set_timezone_to_utc_matching_zone_offset() {
        // Given
        TimeZone expectedUtcTimeZone = TimeZone.getTimeZone(ZoneOffset.UTC);

        // When
        timezoneInitializer.initialize(applicationContext);

        // Then
        TimeZone defaultTimeZone = TimeZone.getDefault();
        assertThat(defaultTimeZone.getID()).isEqualTo(expectedUtcTimeZone.getID());
        assertThat(defaultTimeZone.getRawOffset()).isEqualTo(expectedUtcTimeZone.getRawOffset());
    }

    @Test
    @DisplayName("Should not throw any exception when initialize is called")
    void should_not_throw_exception_when_initialize_is_called() {
        // When & Then
        assertThatCode(() -> timezoneInitializer.initialize(applicationContext)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should successfully initialize even when timezone is already UTC")
    void should_initialize_successfully_when_timezone_already_utc() {
        // Given
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC)); // Already UTC

        // When
        timezoneInitializer.initialize(applicationContext);

        // Then
        TimeZone defaultTimeZone = TimeZone.getDefault();
        assertThat(defaultTimeZone.getID()).isEqualTo("UTC");
    }

    @Test
    @DisplayName("Should override any previously set timezone to UTC")
    void should_override_previously_set_timezone_to_utc() {
        // Given
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"));

        // When
        timezoneInitializer.initialize(applicationContext);

        // Then
        TimeZone defaultTimeZone = TimeZone.getDefault();
        assertThat(defaultTimeZone.getID()).isEqualTo("UTC");
        assertThat(defaultTimeZone.getID()).isNotEqualTo("Asia/Tokyo");
    }

    @Test
    @DisplayName("Should set UTC timezone with zero offset")
    void should_set_utc_timezone_with_zero_offset() {
        // When
        timezoneInitializer.initialize(applicationContext);

        // Then
        TimeZone defaultTimeZone = TimeZone.getDefault();
        assertThat(defaultTimeZone.getRawOffset())
                .as("UTC timezone should have zero offset from GMT")
                .isZero();
        assertThat(defaultTimeZone.useDaylightTime())
                .as("UTC timezone should not use daylight saving time")
                .isFalse();
    }

    @Test
    @DisplayName("Should handle null application context gracefully")
    void should_handle_null_application_context_gracefully() {
        // When & Then
        assertThatCode(() -> timezoneInitializer.initialize(null)).doesNotThrowAnyException();

        // Verify timezone is still set to UTC
        TimeZone defaultTimeZone = TimeZone.getDefault();
        assertThat(defaultTimeZone.getID()).isEqualTo("UTC");
    }
}
