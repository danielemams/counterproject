package com.counterproject.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "queue_templates")
public class QueueTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "queue_id", nullable = false)
    private Queue queue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;

    @Column(nullable = false)
    private LocalDate dtStartLink;

    @Column(nullable = false)
    private LocalDate dtEndLink;

    // Constructors
    public QueueTemplate() {}

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

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
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
