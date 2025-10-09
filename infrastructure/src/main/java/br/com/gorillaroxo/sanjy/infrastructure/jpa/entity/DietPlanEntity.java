package br.com.gorillaroxo.sanjy.infrastructure.jpa.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
@Table(name = "diet_plan")
public class DietPlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    @Column(name = "daily_calories")
    private Integer dailyCalories;

    @Column(name = "daily_protein_g")
    private Integer dailyProteinInG;

    @Column(name = "daily_carbs_g")
    private Integer dailyCarbsInG;

    @Column(name = "daily_fat_g")
    private Integer dailyFatInG;

    @Column(columnDefinition = "TEXT")
    private String goal;

    @Column(name = "nutritionist_notes", columnDefinition = "TEXT")
    private String nutritionistNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @OneToMany(mappedBy = "dietPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MealTypeEntity> mealTypes = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}