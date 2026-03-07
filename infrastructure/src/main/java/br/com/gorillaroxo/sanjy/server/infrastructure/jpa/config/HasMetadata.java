package br.com.gorillaroxo.sanjy.server.infrastructure.jpa.config;

import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity.embedded.MetadataEntityEmbedded;

public interface HasMetadata {

    MetadataEntityEmbedded getMetadata();

    void setMetadata(MetadataEntityEmbedded metadata);
}
