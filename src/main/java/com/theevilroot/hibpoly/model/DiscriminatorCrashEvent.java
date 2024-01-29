package com.theevilroot.hibpoly.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@DiscriminatorValue("4")
public class DiscriminatorCrashEvent extends DiscriminatorEvent {
    @Column(name = "stacktrace", length = 4096)
    private String stackTrace;

    @Column(name = "module", length = 1024)
    private String module;

    public DiscriminatorCrashEvent() {
    }

    public DiscriminatorCrashEvent(EventType eventType, Instant createDate, String instanceId, String stackTrace, String module) {
        super(eventType, createDate, instanceId);
        this.stackTrace = stackTrace;
        this.module = module;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
}
