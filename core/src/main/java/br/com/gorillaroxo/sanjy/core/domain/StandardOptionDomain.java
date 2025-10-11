package br.com.gorillaroxo.sanjy.core.domain;

import lombok.Builder;

@Builder
public record StandardOptionDomain(
    Long id,
    Long optionNumber,
    String description,
    Long mealTypeId
) {

}
