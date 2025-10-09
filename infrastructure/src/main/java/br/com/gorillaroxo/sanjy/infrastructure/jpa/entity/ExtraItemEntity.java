package br.com.gorillaroxo.sanjy.infrastructure.jpa.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "extra_items")
public class ExtraItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_record_id", nullable = false, foreignKey = @ForeignKey(name = "fk_extras_meal_record"))
    private MealRecordEntity mealRecord;

    @Column(name = "item_name", nullable = false, length = 200)
    private String itemName;

    @Builder.Default
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal quantity = BigDecimal.ONE;

    @Builder.Default
    @Column(nullable = false, length = 50)
    private String unit = "unit";

    @Column(name = "consumed_at")
    private LocalDateTime consumedAt;

}