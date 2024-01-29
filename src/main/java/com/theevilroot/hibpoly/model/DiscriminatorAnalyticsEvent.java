package com.theevilroot.hibpoly.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "discriminator_events_analytics")
public class DiscriminatorAnalyticsEvent extends DiscriminatorEvent {

    @Column(name = "uptime")
    private Long uptime;

    @Column(name = "ram_available")
    private Long ramAvailable;

    @Column(name = "cpu_load")
    private Long cpuLoad;

    public DiscriminatorAnalyticsEvent() {
    }

    public DiscriminatorAnalyticsEvent(EventType eventType, Instant createDate, String instanceId, Long uptime, Long ramAvailable, Long cpuLoad) {
        super(eventType, createDate, instanceId);
        this.uptime = uptime;
        this.ramAvailable = ramAvailable;
        this.cpuLoad = cpuLoad;
    }

    public Long getUptime() {
        return uptime;
    }

    public void setUptime(Long uptime) {
        this.uptime = uptime;
    }

    public Long getRamAvailable() {
        return ramAvailable;
    }

    public void setRamAvailable(Long ramAvailable) {
        this.ramAvailable = ramAvailable;
    }

    public Long getCpuLoad() {
        return cpuLoad;
    }

    public void setCpuLoad(Long cpuLoad) {
        this.cpuLoad = cpuLoad;
    }
}
