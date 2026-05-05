package br.com.gorillaroxo.sanjy.server.core.domain;

import java.util.Optional;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@AllArgsConstructor
public class PatchableStandardOptionDomain {

    @Getter
    private Long id;

    private String description;

    public Optional<String> getDescription() {
        return Optional.ofNullable(description).filter(Predicate.not(String::isBlank));
    }
}
