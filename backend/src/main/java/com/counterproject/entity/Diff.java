package com.counterproject.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "diffs")
public class Diff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "queue_id", nullable = false)
    private Queue queue;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal value;

    @Column(nullable = false)
    private LocalDateTime dtDiff;

    @Column
    private LocalDate dtExpiry;

    @Column
    private Boolean isManual;

    @Column(length = 1000)
    private String description;

    @Column
    private Long templateId;

    @Column(nullable = false)
    private boolean isModifiedManually = false;

    // Constructors
    public Diff() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public LocalDateTime getDtDiff() {
        return dtDiff;
    }

    public void setDtDiff(LocalDateTime dtDiff) {
        this.dtDiff = dtDiff;
    }

    public LocalDate getDtExpiry() {
        return dtExpiry;
    }

    public void setDtExpiry(LocalDate dtExpiry) {
        this.dtExpiry = dtExpiry;
    }

    public Boolean getIsManual() {
        return isManual;
    }

    public void setIsManual(Boolean isManual) {
        this.isManual = isManual;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public boolean isModifiedManually() {
        return isModifiedManually;
    }

    public void setModifiedManually(boolean modifiedManually) {
        isModifiedManually = modifiedManually;
    }
}
