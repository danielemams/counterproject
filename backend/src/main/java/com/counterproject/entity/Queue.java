package com.counterproject.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "queues")
public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime dtInitValue;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal initValue;

    @Column
    private LocalDate dtExpiryInitValue;

    @Column(nullable = false)
    private boolean isWithLinearConsumption;

    @OneToMany(mappedBy = "queue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Diff> diffs = new ArrayList<>();

    @OneToMany(mappedBy = "queue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QueueTemplate> queueTemplates = new ArrayList<>();

    // Constructors
    public Queue() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDtInitValue() {
        return dtInitValue;
    }

    public void setDtInitValue(LocalDateTime dtInitValue) {
        this.dtInitValue = dtInitValue;
    }

    public BigDecimal getInitValue() {
        return initValue;
    }

    public void setInitValue(BigDecimal initValue) {
        this.initValue = initValue;
    }

    public LocalDate getDtExpiryInitValue() {
        return dtExpiryInitValue;
    }

    public void setDtExpiryInitValue(LocalDate dtExpiryInitValue) {
        this.dtExpiryInitValue = dtExpiryInitValue;
    }

    public boolean isWithLinearConsumption() {
        return isWithLinearConsumption;
    }

    public void setWithLinearConsumption(boolean withLinearConsumption) {
        isWithLinearConsumption = withLinearConsumption;
    }

    public List<Diff> getDiffs() {
        return diffs;
    }

    public void setDiffs(List<Diff> diffs) {
        this.diffs = diffs;
    }

    public List<QueueTemplate> getQueueTemplates() {
        return queueTemplates;
    }

    public void setQueueTemplates(List<QueueTemplate> queueTemplates) {
        this.queueTemplates = queueTemplates;
    }
}
