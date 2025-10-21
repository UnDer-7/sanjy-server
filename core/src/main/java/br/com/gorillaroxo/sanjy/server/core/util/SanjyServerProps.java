package br.com.gorillaroxo.sanjy.server.core.util;

public interface SanjyServerProps {
    LoggingProp logging();

    interface LoggingProp {
        String level();
        String filePath();
        String appender();
    }
}
