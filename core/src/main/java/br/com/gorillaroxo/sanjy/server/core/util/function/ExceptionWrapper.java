package br.com.gorillaroxo.sanjy.server.core.util.function;

public class ExceptionWrapper extends RuntimeException {

    ExceptionWrapper(final Throwable throwable) {
        super(throwable);
    }
}
