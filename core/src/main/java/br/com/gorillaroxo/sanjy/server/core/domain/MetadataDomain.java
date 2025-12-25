package br.com.gorillaroxo.sanjy.server.core.domain;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record MetadataDomain(
    Instant createdAt,
    Instant updatedAt
) {

}
