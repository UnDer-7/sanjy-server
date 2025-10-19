package br.com.gorillaroxo.sanjy.server.core.domain;

import java.util.List;

public record PageResultDomain<T>(
    Long totalPages,
    Long currentPage,
    Long pageSize,
    Long totalItems,
    List<T> content
) {

}
