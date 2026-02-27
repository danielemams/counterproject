package com.counterproject.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class QueueDTO {
    private Long id;
    private String name;
    private LocalDateTime dtInitValue;
    private BigDecimal initValue;
    private LocalDate dtExpiryInitValue;
    private boolean isWithLinearConsumption;

    public QueueDTO() {}

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
}
