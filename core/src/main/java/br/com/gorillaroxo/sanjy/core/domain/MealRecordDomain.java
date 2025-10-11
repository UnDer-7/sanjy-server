package br.com.gorillaroxo.sanjy.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class MealRecordDomain {

    private Long id;

    @Setter
    private LocalDateTime consumedAt;

    private MealTypeDomain mealType;
    private Boolean isFreeMeal;
    private StandardOptionDomain standardOption;
    private String freeMealDescription;
    private BigDecimal quantity;
    private String unit;
    private String notes;
    private LocalDateTime createdAt;

}
