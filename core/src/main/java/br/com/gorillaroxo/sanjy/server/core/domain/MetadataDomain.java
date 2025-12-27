package br.com.gorillaroxo.sanjy.server.core.domain;

import java.time.Instant;
import lombok.Builder;

@Builder(toBuilder = true)
public record MetadataDomain(Instant createdAt, Instant updatedAt) {}
