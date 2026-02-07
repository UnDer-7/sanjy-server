package br.com.gorillaroxo.sanjy.server.core.ports.driven;

public interface SemanticVersioningComparatorGateway {

    /**
     * Compares two semantic versioning strings to determine their ordering.
     *
     * @param currentVersion the current version to compare
     * @param targetVersion the target version to compare against
     * @return a negative integer if {@code currentVersion} is lower than {@code targetVersion}, zero if they are equal,
     *     or a positive integer if {@code currentVersion} is greater than {@code targetVersion}
     */
    int compare(String currentVersion, String targetVersion);
}
