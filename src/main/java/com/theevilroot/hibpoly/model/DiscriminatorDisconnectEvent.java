package com.theevilroot.hibpoly.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@DiscriminatorValue("1")
public class DiscriminatorDisconnectEvent extends DiscriminatorEvent {

    @Column(name = "reason", length = 1024)
    private String reason;

    public DiscriminatorDisconnectEvent() {
    }

    public DiscriminatorDisconnectEvent(EventType eventType, Instant createDate, String instanceId, String reason) {
        super(eventType, createDate, instanceId);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
