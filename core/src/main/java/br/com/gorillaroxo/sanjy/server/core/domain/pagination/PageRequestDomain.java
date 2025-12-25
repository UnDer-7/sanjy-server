package br.com.gorillaroxo.sanjy.server.core.domain.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class PageRequestDomain {

    private final Integer pageNumber;
    private final Integer pageSize;
}
