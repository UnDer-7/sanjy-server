package br.com.gorillaroxo.sanjy.server.core;

import lombok.Builder;

@Builder
public record ProjectInfoDomain(
    String runtimeMode,
    Version version,
    Timezone timezone
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
