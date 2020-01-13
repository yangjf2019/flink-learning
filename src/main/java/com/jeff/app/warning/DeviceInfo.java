package com.jeff.app.warning;

import java.io.Serializable;

/**
 * @author Created by Jeff Yang on 2020-01-13 15:10.
 * Update date:
 * Project: flink-learning
 * Package: com.jeff.app.warning
 * Describe :解析传感器采集的 json 数据
 * Dependency :
 * Frequency: Calculate once a day.
 * Result of Test: test ok
 * Command:
 * <p>
 * Email:  highfei2011@126.com
 * Status：Using online
 * <p>
 * Please note:
 * Must be checked once every time you submit a configuration file is correct!
 * Data is priceless! Accidentally deleted the consequences!
 */
public class DeviceInfo implements Serializable {

    private static final long serialVersionUID = -8981968540607338358L;
    private Long timestamp;
    private Integer deviceId;
    private String deviceName;
    private String deviceIp;
    private Double cpu;
    private Double memory;
    private Double space;

    public DeviceInfo(){}
    public DeviceInfo(Long timestamp, Integer deviceId, String deviceName, String deviceIp, Double cpu, Double memory, Double space) {
        this.timestamp = timestamp;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceIp = deviceIp;
        this.cpu = cpu;
        this.memory = memory;
        this.space = space;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }

    public Double getCpu() {
        return cpu;
    }

    public void setCpu(Double cpu) {
        this.cpu = cpu;
    }

    public Double getMemory() {
        return memory;
    }

    public void setMemory(Double memory) {
        this.memory = memory;
    }

    public Double getSpace() {
        return space;
    }

    public void setSpace(Double space) {
        this.space = space;
    }
}
