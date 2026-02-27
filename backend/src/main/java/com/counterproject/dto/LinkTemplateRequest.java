package com.counterproject.dto;

import java.time.LocalDate;

public class LinkTemplateRequest {
    private Long templateId;
    private LocalDate dtStartLink;
    private LocalDate dtEndLink;

    public LinkTemplateRequest() {}

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public LocalDate getDtStartLink() {
        return dtStartLink;
    }

    public void setDtStartLink(LocalDate dtStartLink) {
        this.dtStartLink = dtStartLink;
    }

    public LocalDate getDtEndLink() {
        return dtEndLink;
    }

    public void setDtEndLink(LocalDate dtEndLink) {
        this.dtEndLink = dtEndLink;
    }
}
