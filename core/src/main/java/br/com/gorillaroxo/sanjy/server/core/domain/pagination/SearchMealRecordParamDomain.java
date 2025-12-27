package br.com.gorillaroxo.sanjy.server.core.domain.pagination;

import java.time.Instant;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SearchMealRecordParamDomain extends PageRequestDomain {

    private final Instant consumedAtAfter;
    private final Instant consumedAtBefore;
    private final Boolean isFreeMeal;

    public SearchMealRecordParamDomain(
            final Integer pageNumber,
            final Integer pageSize,
            final Instant consumedAtAfter,
            final Instant consumedAtBefore,
            final Boolean isFreeMeal) {

        super(pageNumber, pageSize);

        this.consumedAtAfter = consumedAtAfter;
        this.consumedAtBefore = consumedAtBefore;
        this.isFreeMeal = isFreeMeal;
    }
}
