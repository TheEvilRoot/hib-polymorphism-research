package com.theevilroot.hibpoly.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@DiscriminatorValue(value = "0")
public class DiscriminatorConnectEvent extends DiscriminatorEvent {

    @Column(name = "ip_address", length = 100)
    private String ipAddress;

    @Column(name = "is_success")
    private boolean success;

    public DiscriminatorConnectEvent() {
    }

    public DiscriminatorConnectEvent(EventType eventType, Instant createDate, String instanceId, String ipAddress, boolean success) {
        super(eventType, createDate, instanceId);
        this.ipAddress = ipAddress;
        this.success = success;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
