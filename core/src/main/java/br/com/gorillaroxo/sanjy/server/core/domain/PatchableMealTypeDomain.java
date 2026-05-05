package br.com.gorillaroxo.sanjy.server.core.domain;

import java.time.LocalTime;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@AllArgsConstructor
public class PatchableMealTypeDomain {

    @Getter
    private Long id;

    private String name;
    private LocalTime scheduledTime;
    private String observation;

    public Optional<String> getName() {
        return Optional.ofNullable(name).filter(Predicate.not(String::isBlank));
    }

    public Optional<LocalTime> getScheduledTime() {
        return Optional.ofNullable(scheduledTime);
    }

    public Optional<String> getObservation() {
        return Optional.ofNullable(observation).filter(Predicate.not(String::isBlank));
    }
}
