package br.com.gorillaroxo.sanjy.infrastructure.jpa.entity;

import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "meal_type",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_plan_meal_name",
        columnNames = {"diet_plan_id", "name"}
    ))
public class MealTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_plan_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_meal_type_plan"))
    private DietPlanEntity dietPlan;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "scheduled_time")
    private LocalTime scheduledTime;

    @Builder.Default
    @OneToMany(mappedBy = "mealType", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StandardOptionEntity> standardOptions = new HashSet<>();

//    @Builder.Default
//    @OneToMany(mappedBy = "mealType")
//    private List<MealRecordEntity> mealRecords = new ArrayList<>();
}