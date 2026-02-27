package com.counterproject.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MetricsDTO {
    private LocalDate dtRif;
    private BigDecimal currentValue;
    private BigDecimal speseAllaDtRifEOD;
    private BigDecimal spesaExpectedAllaDtRifEOD;
    private BigDecimal expectedValue;
    private BigDecimal budgetCurrent;
    private LocalDate dtMaxExpiry;
    private boolean hasDtMaxExpiry;

    public MetricsDTO() {}

    public LocalDate getDtRif() {
        return dtRif;
    }

    public void setDtRif(LocalDate dtRif) {
        this.dtRif = dtRif;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }

    public BigDecimal getSpeseAllaDtRifEOD() {
        return speseAllaDtRifEOD;
    }

    public void setSpeseAllaDtRifEOD(BigDecimal speseAllaDtRifEOD) {
        this.speseAllaDtRifEOD = speseAllaDtRifEOD;
    }

    public BigDecimal getSpesaExpectedAllaDtRifEOD() {
        return spesaExpectedAllaDtRifEOD;
    }

    public void setSpesaExpectedAllaDtRifEOD(BigDecimal spesaExpectedAllaDtRifEOD) {
        this.spesaExpectedAllaDtRifEOD = spesaExpectedAllaDtRifEOD;
    }

    public BigDecimal getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(BigDecimal expectedValue) {
        this.expectedValue = expectedValue;
    }

    public BigDecimal getBudgetCurrent() {
        return budgetCurrent;
    }

    public void setBudgetCurrent(BigDecimal budgetCurrent) {
        this.budgetCurrent = budgetCurrent;
    }

    public LocalDate getDtMaxExpiry() {
        return dtMaxExpiry;
    }

    public void setDtMaxExpiry(LocalDate dtMaxExpiry) {
        this.dtMaxExpiry = dtMaxExpiry;
    }

    public boolean isHasDtMaxExpiry() {
        return hasDtMaxExpiry;
    }

    public void setHasDtMaxExpiry(boolean hasDtMaxExpiry) {
        this.hasDtMaxExpiry = hasDtMaxExpiry;
    }
}
