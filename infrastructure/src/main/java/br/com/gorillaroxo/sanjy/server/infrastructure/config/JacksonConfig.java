package br.com.gorillaroxo.sanjy.server.infrastructure.config;

import br.com.gorillaroxo.sanjy.server.entrypoint.util.RequestConstants;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return JacksonConfig::dateTimeFormat;
    }

    private static void dateTimeFormat(final Jackson2ObjectMapperBuilder builder) {
        final DateTimeFormatter dateFormatter =
                DateTimeFormatter.ofPattern(RequestConstants.DateTimeFormats.DATE_FORMAT);
        builder.serializers(new LocalDateSerializer(dateFormatter));
        builder.deserializers(new LocalDateDeserializer(dateFormatter));

        final DateTimeFormatter timeFormatter =
                DateTimeFormatter.ofPattern(RequestConstants.DateTimeFormats.TIME_FORMAT);
        builder.serializers(new LocalTimeSerializer(timeFormatter));
        builder.deserializers(new LocalTimeDeserializer(timeFormatter));
    }
}
