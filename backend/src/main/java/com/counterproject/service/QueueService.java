package com.counterproject.service;

import com.counterproject.dto.LinkTemplateRequest;
import com.counterproject.dto.QueueDTO;
import com.counterproject.entity.Diff;
import com.counterproject.entity.Queue;
import com.counterproject.entity.QueueTemplate;
import com.counterproject.entity.Template;
import com.counterproject.util.TemplateGenerator;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class QueueService {

    @PersistenceContext(unitName = "counterproject-pu")
    private EntityManager em;

    public List<QueueDTO> findAll() {
        TypedQuery<Queue> query = em.createQuery("SELECT q FROM Queue q ORDER BY q.id", Queue.class);
        return query.getResultList().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public QueueDTO findById(Long id) {
        Queue queue = em.find(Queue.class, id);
        return queue != null ? toDTO(queue) : null;
    }

    public QueueDTO create(QueueDTO dto) {
        validateQueue(dto);
        Queue queue = new Queue();
        fromDTO(dto, queue);
        em.persist(queue);
        em.flush();
        return toDTO(queue);
    }

    public QueueDTO update(Long id, QueueDTO dto) {
        Queue queue = em.find(Queue.class, id);
        if (queue == null) {
            throw new IllegalArgumentException("Queue not found");
        }
        validateQueue(dto);
        fromDTO(dto, queue);
        em.merge(queue);
        em.flush();
        return toDTO(queue);
    }

    public void delete(Long id) {
        Queue queue = em.find(Queue.class, id);
        if (queue != null) {
            em.remove(queue);
        }
    }

    public void clearAllDiffs(Long queueId) {
        Queue queue = em.find(Queue.class, queueId);
        if (queue == null) {
            throw new IllegalArgumentException("Queue not found");
        }
        em.createQuery("DELETE FROM Diff d WHERE d.queue.id = :queueId")
                .setParameter("queueId", queueId)
                .executeUpdate();
    }

    public void linkTemplate(Long queueId, LinkTemplateRequest request) {
        Queue queue = em.find(Queue.class, queueId);
        if (queue == null) {
            throw new IllegalArgumentException("Queue not found");
        }

        Template template = em.find(Template.class, request.getTemplateId());
        if (template == null) {
            throw new IllegalArgumentException("Template not found");
        }

        // Create link
        QueueTemplate queueTemplate = new QueueTemplate();
        queueTemplate.setQueue(queue);
        queueTemplate.setTemplate(template);
        queueTemplate.setDtStartLink(request.getDtStartLink());
        queueTemplate.setDtEndLink(request.getDtEndLink());
        em.persist(queueTemplate);

        // Generate diffs
        List<Diff> diffs = TemplateGenerator.generateDiffs(queue, template, request.getDtStartLink(), request.getDtEndLink());
        for (Diff diff : diffs) {
            em.persist(diff);
        }

        em.flush();
    }

    private void validateQueue(QueueDTO dto) {
        if (dto.getInitValue() == null) {
            throw new IllegalArgumentException("initValue is required");
        }
        if (dto.getDtInitValue() == null) {
            throw new IllegalArgumentException("dtInitValue is required");
        }
    }

    private QueueDTO toDTO(Queue queue) {
        QueueDTO dto = new QueueDTO();
        dto.setId(queue.getId());
        dto.setName(queue.getName());
        dto.setDtInitValue(queue.getDtInitValue());
        dto.setInitValue(queue.getInitValue());
        dto.setDtExpiryInitValue(queue.getDtExpiryInitValue());
        dto.setWithLinearConsumption(queue.isWithLinearConsumption());
        return dto;
    }

    private void fromDTO(QueueDTO dto, Queue queue) {
        queue.setName(dto.getName());
        queue.setDtInitValue(dto.getDtInitValue());
        queue.setInitValue(dto.getInitValue());
        queue.setDtExpiryInitValue(dto.getDtExpiryInitValue());
        queue.setWithLinearConsumption(dto.isWithLinearConsumption());
    }
}
