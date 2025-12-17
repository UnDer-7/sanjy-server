package br.com.gorillaroxo.sanjy.server.core.ports.driven;

public interface SanjyServerProps {
    LoggingProp logging();

    ApplicationProp application();

    ExternalHttpClientsProp externalHttpClients();

    interface LoggingProp {
        String level();

        String filePath();

        String appender();
    }

    interface ApplicationProp {
        String name();

        String version();

        String description();

        ApplicationContactProp contact();

        ApplicationDocumentationProp documentation();
    }

    interface ApplicationContactProp {
        String name();

        String url();

        String email();
    }

    interface ApplicationDocumentationProp {
        String url();

        String description();
    }

    interface ExternalHttpClientsProp {
        ExternalHttpClientsRetryConfigProp retryConfig();

        GenericHttpClientsProp github();
    }

    interface GenericHttpClientsProp {
        String url();
    }

    interface ExternalHttpClientsRetryConfigProp {
        Integer maxAttempt();

        Integer interval();

        Integer backoffMultiplier();
    }
}
