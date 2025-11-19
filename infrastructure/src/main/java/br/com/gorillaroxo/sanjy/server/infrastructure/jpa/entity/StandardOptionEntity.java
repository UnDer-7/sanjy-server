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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "standard_options",
        uniqueConstraints =
                @UniqueConstraint(
                        name = "uk_type_option",
                        columnNames = {"meal_type_id", "option_number"}))
public class StandardOptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_options_meal_type"))
    private MealTypeEntity mealType;

    @Column(name = "option_number", nullable = false)
    private Integer optionNumber;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    //    @Builder.Default
    //    @OneToMany(mappedBy = "standardOption")
    //    private List<MealRecordEntity> mealRecords = new ArrayList<>();

}
