package br.com.gorillaroxo.sanjy.server.core.domain;

import lombok.Builder;

@Builder
public record StandardOptionDomain(
        Long id, Long optionNumber, String description, Long mealTypeId, MetadataDomain metadata) {

    public StandardOptionDomain patch(final PatchableStandardOptionDomain patchable) {
        return StandardOptionDomain.builder()
                .id(this.id)
                .optionNumber(this.optionNumber)
                .description(patchable.getDescription().orElse(this.description))
                .mealTypeId(this.mealTypeId)
                .metadata(this.metadata)
                .build();
    }

    public String toPatchableFieldsString() {
        return "( description=" + description + " )";
    }
}
