package br.com.gorillaroxo.sanjy.server.core.util.function;

@FunctionalInterface
public interface RunnableWrapper<E extends Exception> {

    static Runnable wrap(final RunnableWrapper<Exception> runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (final Exception e) {
                throw new ExceptionWrapper(e);
            }
        };
    }

    void run() throws E;
}
