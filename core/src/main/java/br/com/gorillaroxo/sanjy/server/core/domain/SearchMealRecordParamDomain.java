package br.com.gorillaroxo.sanjy.server.core.domain;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Getter
@ToString
public class SearchMealRecordParamDomain extends PageRequestDomain {

    private final LocalDateTime consumedAtAfter;
    private final LocalDateTime consumedAtBefore;
    private final Boolean isFreeMeal;

    public SearchMealRecordParamDomain(final Integer pageNumber,
        final Integer pageSize,
        final LocalDateTime consumedAtAfter,
        final LocalDateTime consumedAtBefore,
        final Boolean isFreeMeal) {

        super(pageNumber, pageSize);

        this.consumedAtAfter = consumedAtAfter;
        this.consumedAtBefore = consumedAtBefore;
        this.isFreeMeal = isFreeMeal;
    }

}
