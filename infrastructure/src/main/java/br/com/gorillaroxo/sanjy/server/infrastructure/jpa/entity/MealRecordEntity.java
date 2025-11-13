package br.com.gorillaroxo.sanjy.server.infrastructure.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
@Table(name = "meal_record")
public class MealRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "consumed_at", nullable = false)
    private LocalDateTime consumedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_record_meal_type"))
    private MealTypeEntity mealType;

    @Column(name = "is_free_meal", nullable = false)
    @Builder.Default
    private Boolean isFreeMeal = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "standard_option_id", foreignKey = @ForeignKey(name = "fk_record_standard_option"))
    private StandardOptionEntity standardOption;

    @Column(name = "free_meal_description", columnDefinition = "TEXT")
    private String freeMealDescription;

    @Column(name = "quantity", columnDefinition = "DECIMAL(10,2)", nullable = false)
    private BigDecimal quantity;

    @Column(name = "unit", columnDefinition = "VARCHAR(50)", length = 50, nullable = false)
    private String unit;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
