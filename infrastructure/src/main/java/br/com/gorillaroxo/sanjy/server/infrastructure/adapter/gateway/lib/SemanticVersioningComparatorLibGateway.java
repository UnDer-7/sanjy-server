package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.gateway.lib;

import br.com.gorillaroxo.sanjy.server.core.ports.driven.SemanticVersioningComparatorGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SemanticVersioningComparatorLibGateway implements SemanticVersioningComparatorGateway {

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(final String currentVersion, final String targetVersion) {
        final var comparableCurrent = new ComparableVersion(currentVersion);
        final var comparableTarget = new ComparableVersion(targetVersion);

        return comparableCurrent.compareTo(comparableTarget);
    }

}
