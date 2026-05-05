package br.com.gorillaroxo.sanjy.server.core.domain;

import java.time.LocalTime;
import java.util.List;
import lombok.Builder;

@Builder
public record MealTypeDomain(
        Long id,
        String name,
        LocalTime scheduledTime,
        Long dietPlanId,
        String observation,
        List<StandardOptionDomain> standardOptions,
        MetadataDomain metadata) {

    public MealTypeDomain patch(final PatchableMealTypeDomain patchable) {
        return MealTypeDomain.builder()
                .id(this.id)
                .name(patchable.getName().orElse(this.name))
                .scheduledTime(patchable.getScheduledTime().orElse(this.scheduledTime))
                .observation(patchable.getObservation().orElse(this.observation))
                .dietPlanId(this.dietPlanId)
                .standardOptions(this.standardOptions)
                .metadata(this.metadata)
                .build();
    }

    public String toPatchableFieldsString() {
        return "( name=" + name + ", scheduledTime=" + scheduledTime + ", observation=" + observation + " )";
    }
}
