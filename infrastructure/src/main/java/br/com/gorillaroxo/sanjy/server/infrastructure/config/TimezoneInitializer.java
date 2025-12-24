package br.com.gorillaroxo.sanjy.server.infrastructure.config;

import br.com.gorillaroxo.sanjy.server.core.domain.LogField;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.TimeZone;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Initializes the application timezone before the ApplicationContext is fully created.
 *
 * <p>This initializer sets the JVM default timezone based on the following priority:
 *
 * <ol>
 *   <li>SANJY_SERVER_TIMEZONE environment variable
 *   <li>TZ environment variable (Linux default timezone)
 *   <li>UTC as fallback (if none of the above are set)
 * </ol>
 *
 * <p><strong>Why this approach:</strong>
 *
 * <ul>
 *   <li>Executes BEFORE the ApplicationContext is created, ensuring all beans see the correct timezone
 *   <li>Works with both JVM and GraalVM Native Image without modifications
 *   <li>Reads environment variables at runtime (not build time)
 *   <li>More reliable than -Duser.timezone JVM argument for Docker/Native Image deployments
 * </ul>
 *
 * @see TimeZone#setDefault(TimeZone)
 */
@Slf4j
public class TimezoneInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String ENV_SANJY_TIMEZONE = "SANJY_SERVER_TIMEZONE";
    private static final String ENV_SYSTEM_TIMEZONE = "TZ";
    private static final String FALLBACK_TIMEZONE = "UTC";

    @Override
    public void initialize(final ConfigurableApplicationContext applicationContext) {
        final String timezoneId = resolveTimezoneId();
        final TimeZone timezone = TimeZone.getTimeZone(ZoneId.of(timezoneId));

        TimeZone.setDefault(timezone);

        log.info(
                LogField.Placeholders.TWO.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Application timezone initialized"),
                StructuredArguments.kv(LogField.TIMEZONE_ID.label(), timezoneId));
    }

    /**
     * Resolves the timezone ID based on environment variables with fallback logic.
     *
     * @return the resolved timezone ID
     */
    private static String resolveTimezoneId() {
        // Priority 1: SANJY_SERVER_TIMEZONE
        final String sanjyTimezone = System.getenv(ENV_SANJY_TIMEZONE);
        if (isValidTimezoneId(sanjyTimezone)) {
            log.debug("Using timezone from {}: {}", ENV_SANJY_TIMEZONE, sanjyTimezone);
            return sanjyTimezone.trim();
        }

        // Priority 2: TZ (Linux system timezone)
        final String systemTimezone = System.getenv(ENV_SYSTEM_TIMEZONE);
        if (isValidTimezoneId(systemTimezone)) {
            log.debug("Using timezone from {}: {}", ENV_SYSTEM_TIMEZONE, systemTimezone);
            return systemTimezone.trim();
        }

        // Priority 3: Fallback to UTC
        log.warn(
                LogField.Placeholders.TWO.getPlaceholder(),
                StructuredArguments.kv(
                        LogField.MSG.label(),
                        "No valid timezone found in environment variables: %s or %s. Using fallback"
                                .formatted(ENV_SANJY_TIMEZONE, ENV_SYSTEM_TIMEZONE)),
                StructuredArguments.kv(LogField.FALLBACK_TIMEZONE_ID.label(), FALLBACK_TIMEZONE));

        return FALLBACK_TIMEZONE;
    }

    /**
     * Validates if the given timezone ID is valid and not blank.
     *
     * @param timezoneId the timezone ID to validate
     * @return true if valid, false otherwise
     */
    private static boolean isValidTimezoneId(final String timezoneId) {
        if (timezoneId == null || timezoneId.isBlank()) {
            return false;
        }

        try {
            ZoneId.of(timezoneId.trim());
            return true;
        } catch (final ZoneRulesException e) {
            log.warn(
                    LogField.Placeholders.TWO.getPlaceholder(),
                    StructuredArguments.kv(LogField.MSG.label(), "Invalid timezone ID"),
                    StructuredArguments.kv(LogField.TIMEZONE_ID.label(), timezoneId),
                    e);
            return false;
        }
    }
}
