package com.theevilroot.hibpoly.model;

import javax.persistence.*;
import java.time.Instant;

@Entity(name = "discriminator_events")
@Table(name = "discriminator_events")
@Inheritance(strategy = InheritanceType.JOINED)
public class DiscriminatorEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_type")
    @Enumerated(value = EnumType.ORDINAL)
    private EventType eventType;

    @Column(name = "create_date")
    private Instant createDate;

    @Column(name = "instance_id", length = 1024)
    private String instanceId;

    public DiscriminatorEvent() {
    }

    public DiscriminatorEvent(EventType eventType, Instant createDate, String instanceId) {
        this.eventType = eventType;
        this.createDate = createDate;
        this.instanceId = instanceId;
    }

    public Long getId() {
        return id;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
