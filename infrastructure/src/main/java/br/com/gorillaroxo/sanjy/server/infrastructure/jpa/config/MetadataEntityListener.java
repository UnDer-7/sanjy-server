package br.com.gorillaroxo.sanjy.server.infrastructure.jpa.config;

import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.embedded.MetadataEntityEmbedded;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.Instant;

public class MetadataEntityListener {

    @PrePersist
    public void onPrePersist(HasMetadata entity) {
        Instant now = Instant.now();
        entity.setMetadata(MetadataEntityEmbedded.builder().createdAt(now).updatedAt(now).build());
    }

    @PreUpdate
    public void onPreUpdate(HasMetadata entity) {
        MetadataEntityEmbedded metadata = entity.getMetadata();
        if (metadata != null) {
            metadata.setUpdatedAt(Instant.now());
        }
    }
}
