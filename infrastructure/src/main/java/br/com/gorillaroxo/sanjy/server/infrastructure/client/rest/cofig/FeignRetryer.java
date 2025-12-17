package br.com.gorillaroxo.sanjy.server.infrastructure.client.rest.cofig;

import br.com.gorillaroxo.sanjy.server.core.domain.LogField;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.SanjyServerProps;
import feign.RetryableException;
import feign.Retryer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;

@Slf4j
@RequiredArgsConstructor
public class FeignRetryer implements Retryer {

    private int attempt = 1;
    private final SanjyServerProps.ExternalHttpClientsRetryConfigProp retryConfigProp;

    @Override
    public void continueOrPropagate(final RetryableException exception) {
        log.info(
                LogField.Placeholders.THREE.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Feign HTTP client retry attempt"),
                StructuredArguments.kv(LogField.FEIGN_RETRY_ENDPOINT.label(), exception.getMessage()),
                StructuredArguments.kv(LogField.FEIGN_RETRY_COUNT.label(), attempt));

        if (attempt == retryConfigProp.maxAttempt()) {
            throw exception;
        }

        attempt++;

        try {
            final long interval = calculateRetryInterval();
            log.info(
                    LogField.Placeholders.TWO.getPlaceholder(),
                    StructuredArguments.kv(LogField.MSG.label(), "Feign HTTP client waiting next retry attempt"),
                    StructuredArguments.kv(LogField.FEIGN_RETRY_INTERVAL.label(), interval));
            Thread.sleep(interval);
        } catch (final InterruptedException interruptedException) {
            log.warn(
                    LogField.Placeholders.ONE.getPlaceholder(),
                    StructuredArguments.kv(LogField.MSG.label(), "Fail to wait interval"),
                    interruptedException);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    @SuppressWarnings({"checkstyle:NoClone", "checkstyle:SuperClone"})
    public Retryer clone() {
        return new FeignRetryer(retryConfigProp);
    }

    private long calculateRetryInterval() {
        return (long) (retryConfigProp.interval() * Math.pow(retryConfigProp.backoffMultiplier(), attempt));
    }
}
