package br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose;

import lombok.Builder;

@Builder
public record ProjectInfoResponseDto(
   Version version,
   Timezone timezone,
   String runtimeMode
) {

    @Builder
    public record Version(
        String current,
        String latest,
        Boolean isLatest
    ) {
    }

    @Builder
    public record Timezone(
        String application,
        String database
    ) {
    }
}
