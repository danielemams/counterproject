package com.counterproject.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DiffDTO {
    private Long id;
    private Long queueId;
    private BigDecimal value;
    private LocalDateTime dtDiff;
    private LocalDate dtExpiry;
    private Boolean isManual;
    private String description;
    private Long templateId;
    private boolean isModifiedManually;

    public DiffDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQueueId() {
        return queueId;
    }

    public void setQueueId(Long queueId) {
        this.queueId = queueId;
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
