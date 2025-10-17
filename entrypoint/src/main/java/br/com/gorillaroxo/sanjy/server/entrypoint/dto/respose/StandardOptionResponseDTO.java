package br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose;

import lombok.Builder;

@Builder
public record StandardOptionResponseDTO(
    Long id,
    Long optionNumber,
    String description,
    Long mealTypeId
) {

}
