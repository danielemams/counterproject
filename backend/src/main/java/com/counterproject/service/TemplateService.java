package com.counterproject.service;

import com.counterproject.dto.TemplateDTO;
import com.counterproject.entity.Diff;
import com.counterproject.entity.FrequencyUnit;
import com.counterproject.entity.Template;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class TemplateService {

    @PersistenceContext(unitName = "counterproject-pu")
    private EntityManager em;

    public List<TemplateDTO> findAll() {
        TypedQuery<Template> query = em.createQuery("SELECT t FROM Template t ORDER BY t.id", Template.class);
        return query.getResultList().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public TemplateDTO findById(Long id) {
        Template template = em.find(Template.class, id);
        return template != null ? toDTO(template) : null;
    }

    public TemplateDTO create(TemplateDTO dto) {
        validateTemplate(dto);
        Template template = new Template();
        fromDTO(dto, template);
        em.persist(template);
        em.flush();
        return toDTO(template);
    }

    public TemplateDTO update(Long id, TemplateDTO dto) {
        Template template = em.find(Template.class, id);
        if (template == null) {
            throw new IllegalArgumentException("Template not found");
        }
        validateTemplate(dto);
        fromDTO(dto, template);
        em.merge(template);
        em.flush();

        // Regenerate future diffs
        regenerateFutureDiffs(template);

        return toDTO(template);
    }

    public void delete(Long id) {
        Template template = em.find(Template.class, id);
        if (template != null) {
            // Delete links but keep generated diffs
            em.createQuery("DELETE FROM QueueTemplate qt WHERE qt.template.id = :templateId")
                    .setParameter("templateId", id)
                    .executeUpdate();
            em.remove(template);
        }
    }

    private void regenerateFutureDiffs(Template template) {
        LocalDate now = LocalDate.now();

        // Delete future diffs that are not modified manually
        em.createQuery("DELETE FROM Diff d WHERE d.templateId = :templateId AND d.dtDiff > :now AND d.isModifiedManually = false")
                .setParameter("templateId", template.getId())
                .setParameter("now", now.atStartOfDay())
                .executeUpdate();

        // Note: regeneration will happen on next link or manually
    }

    private void validateTemplate(TemplateDTO dto) {
        if (dto.getValue() == null) {
            throw new IllegalArgumentException("value is required");
        }
        if (dto.getFrequencyUnit() == null) {
            throw new IllegalArgumentException("frequencyUnit is required");
        }

        // Validate dayOfPeriod based on frequencyUnit
        switch (dto.getFrequencyUnit()) {
            case DAYS:
                // dayOfPeriod not really used for DAYS, but should be 1
                if (dto.getDayOfPeriod() < 1) {
                    throw new IllegalArgumentException("dayOfPeriod must be >= 1");
                }
                break;
            case WEEKS:
                if (dto.getDayOfPeriod() < 1 || dto.getDayOfPeriod() > 7) {
                    throw new IllegalArgumentException("dayOfPeriod must be between 1 and 7 for WEEKS");
                }
                break;
            case MONTHS:
                if (dto.getDayOfPeriod() < 1 || dto.getDayOfPeriod() > 31) {
                    throw new IllegalArgumentException("dayOfPeriod must be between 1 and 31 for MONTHS");
                }
                break;
            case YEARS:
                if (dto.getDayOfPeriod() < 1 || dto.getDayOfPeriod() > 366) {
                    throw new IllegalArgumentException("dayOfPeriod must be between 1 and 366 for YEARS");
                }
                break;
        }

        // Validate frequencyNum
        if (dto.getFrequencyNum() < 1) {
            throw new IllegalArgumentException("frequencyNum must be >= 1");
        }
    }

    private TemplateDTO toDTO(Template template) {
        TemplateDTO dto = new TemplateDTO();
        dto.setId(template.getId());
        dto.setName(template.getName());
        dto.setValue(template.getValue());
        dto.setDayOfPeriod(template.getDayOfPeriod());
        dto.setFrequencyNum(template.getFrequencyNum());
        dto.setFrequencyUnit(template.getFrequencyUnit());
        return dto;
    }

    private void fromDTO(TemplateDTO dto, Template template) {
        template.setName(dto.getName());
        template.setValue(dto.getValue());
        template.setDayOfPeriod(dto.getDayOfPeriod());
        template.setFrequencyNum(dto.getFrequencyNum());
        template.setFrequencyUnit(dto.getFrequencyUnit());
    }
}
