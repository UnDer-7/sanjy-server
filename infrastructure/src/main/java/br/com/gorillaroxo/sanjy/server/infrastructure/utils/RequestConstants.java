package br.com.gorillaroxo.sanjy.server.infrastructure.utils;

public final class RequestConstants {

    private RequestConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final class Headers {

        public static final String X_CORRELATION_ID = "X-Correlation-ID";
        public static final String X_CHANNEL = "X-Channel";

        private Headers() {
            throw new IllegalStateException("Utility class");
        }

    }

}
