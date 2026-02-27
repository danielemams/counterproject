package com.counterproject.service;

import com.counterproject.dto.DiffDTO;
import com.counterproject.entity.Diff;
import com.counterproject.entity.Queue;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class DiffService {

    @PersistenceContext(unitName = "counterproject-pu")
    private EntityManager em;

    public List<DiffDTO> findByQueueId(Long queueId) {
        TypedQuery<Diff> query = em.createQuery(
                "SELECT d FROM Diff d WHERE d.queue.id = :queueId ORDER BY d.dtDiff DESC", Diff.class);
        query.setParameter("queueId", queueId);
        return query.getResultList().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public DiffDTO findById(Long id) {
        Diff diff = em.find(Diff.class, id);
        return diff != null ? toDTO(diff) : null;
    }

    public DiffDTO create(DiffDTO dto) {
        validateDiff(dto);
        Queue queue = em.find(Queue.class, dto.getQueueId());
        if (queue == null) {
            throw new IllegalArgumentException("Queue not found");
        }

        Diff diff = new Diff();
        diff.setQueue(queue);
        fromDTO(dto, diff);
        diff.setModifiedManually(true);

        em.persist(diff);
        em.flush();
        return toDTO(diff);
    }

    public DiffDTO update(Long id, DiffDTO dto) {
        Diff diff = em.find(Diff.class, id);
        if (diff == null) {
            throw new IllegalArgumentException("Diff not found");
        }
        validateDiff(dto);
        fromDTO(dto, diff);
        diff.setModifiedManually(true);

        em.merge(diff);
        em.flush();
        return toDTO(diff);
    }

    public void delete(Long id) {
        Diff diff = em.find(Diff.class, id);
        if (diff != null) {
            em.remove(diff);
        }
    }

    private void validateDiff(DiffDTO dto) {
        if (dto.getValue() == null) {
            throw new IllegalArgumentException("value is required");
        }
        if (dto.getDtDiff() == null) {
            throw new IllegalArgumentException("dtDiff is required");
        }

        // For diff < 0, isManual is required
        if (dto.getValue().compareTo(BigDecimal.ZERO) < 0) {
            if (dto.getIsManual() == null) {
                throw new IllegalArgumentException("isManual is required for negative values");
            }
            if (dto.getDtExpiry() != null) {
                throw new IllegalArgumentException("dtExpiry must be null for negative values");
            }
        }

        // For diff > 0, isManual must be null
        if (dto.getValue().compareTo(BigDecimal.ZERO) > 0) {
            if (dto.getIsManual() != null) {
                throw new IllegalArgumentException("isManual must be null for positive values");
            }
        }
    }

    private DiffDTO toDTO(Diff diff) {
        DiffDTO dto = new DiffDTO();
        dto.setId(diff.getId());
        dto.setQueueId(diff.getQueue().getId());
        dto.setValue(diff.getValue());
        dto.setDtDiff(diff.getDtDiff());
        dto.setDtExpiry(diff.getDtExpiry());
        dto.setIsManual(diff.getIsManual());
        dto.setDescription(diff.getDescription());
        dto.setTemplateId(diff.getTemplateId());
        dto.setModifiedManually(diff.isModifiedManually());
        return dto;
    }

    private void fromDTO(DiffDTO dto, Diff diff) {
        diff.setValue(dto.getValue());
        diff.setDtDiff(dto.getDtDiff());
        diff.setDtExpiry(dto.getDtExpiry());
        diff.setIsManual(dto.getIsManual());
        diff.setDescription(dto.getDescription());
    }
}
