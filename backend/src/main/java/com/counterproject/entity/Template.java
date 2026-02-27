package com.counterproject.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "templates")
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal value;

    @Column(nullable = false)
    private int dayOfPeriod;

    @Column(nullable = false)
    private int frequencyNum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FrequencyUnit frequencyUnit;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QueueTemplate> queueTemplates = new ArrayList<>();

    // Constructors
    public Template() {}

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

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public int getDayOfPeriod() {
        return dayOfPeriod;
    }

    public void setDayOfPeriod(int dayOfPeriod) {
        this.dayOfPeriod = dayOfPeriod;
    }

    public int getFrequencyNum() {
        return frequencyNum;
    }

    public void setFrequencyNum(int frequencyNum) {
        this.frequencyNum = frequencyNum;
    }

    public FrequencyUnit getFrequencyUnit() {
        return frequencyUnit;
    }

    public void setFrequencyUnit(FrequencyUnit frequencyUnit) {
        this.frequencyUnit = frequencyUnit;
    }

    public List<QueueTemplate> getQueueTemplates() {
        return queueTemplates;
    }

    public void setQueueTemplates(List<QueueTemplate> queueTemplates) {
        this.queueTemplates = queueTemplates;
    }
}
