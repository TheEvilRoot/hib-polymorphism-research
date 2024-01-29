package com.theevilroot.hibpoly.model;

import javax.persistence.*;
import java.time.Instant;

@Entity
@DiscriminatorValue("2")
public class DiscriminatorDataEvent extends DiscriminatorEvent {

    @Column(name = "data_length")
    private Long dataLength;

    @Column(name = "data_sha1", length = 1024)
    private String dataSha1;

    @Column(name = "data_blob")
    @Basic(fetch=FetchType.LAZY)
    @Lob()
    private byte[] data;

    public DiscriminatorDataEvent() {
    }

    public DiscriminatorDataEvent(EventType eventType, Instant createDate, String instanceId, Long dataLength, String dataSha1, byte[] data) {
        super(eventType, createDate, instanceId);
        this.dataLength = dataLength;
        this.dataSha1 = dataSha1;
        this.data = data;
    }

    public Long getDataLength() {
        return dataLength;
    }

    public void setDataLength(Long dataLength) {
        this.dataLength = dataLength;
    }

    public String getDataSha1() {
        return dataSha1;
    }

    public void setDataSha1(String dataSha1) {
        this.dataSha1 = dataSha1;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
