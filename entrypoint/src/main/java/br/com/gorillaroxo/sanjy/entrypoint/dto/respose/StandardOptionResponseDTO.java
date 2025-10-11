package br.com.gorillaroxo.sanjy.entrypoint.dto.respose;

import lombok.Builder;

@Builder
public record StandardOptionResponseDTO(
    Long id,
    Long optionNumber,
    String description,
    Long mealTypeId
) {

}
