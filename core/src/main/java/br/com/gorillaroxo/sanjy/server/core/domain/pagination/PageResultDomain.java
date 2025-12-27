package br.com.gorillaroxo.sanjy.server.core.domain.pagination;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record PageResultDomain<T>(Long totalPages, Long currentPage, Long pageSize, Long totalItems, List<T> content) {

    public PageResultDomain {
        content = Objects.requireNonNullElse(content, new ArrayList<>());
    }
}
