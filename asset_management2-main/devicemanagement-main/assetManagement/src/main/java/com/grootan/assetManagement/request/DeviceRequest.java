package com.grootan.assetManagement.request;

import lombok.NoArgsConstructor;
import java.sql.Date;


@NoArgsConstructor
public class DeviceRequest {

    private Integer deviceId;

    private String manufacturedId;

    private String category;

    private String deviceName;

    private Date devicePurchaseDate;

    private String assignStatus;

    private String deviceStatus;

    public DeviceRequest(String manufacturedId, String category, String deviceName, Date devicePurchaseDate, String assignStatus, String deviceStatus) {
        this.manufacturedId = manufacturedId;
        this.category = category;
        this.deviceName = deviceName;
        this.devicePurchaseDate = devicePurchaseDate;
        this.assignStatus = assignStatus;
        this.deviceStatus = deviceStatus;
    }

    public String getManufacturedId() {
        return manufacturedId;
    }

    public void setManufacturedId(String manufacturedId) {
        this.manufacturedId = manufacturedId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Date getDevicePurchaseDate() {
        return devicePurchaseDate;
    }

    public void setDevicePurchaseDate(Date devicePurchaseDate) {
        this.devicePurchaseDate = devicePurchaseDate;
    }

    public String getAssignStatus() {
        return assignStatus;
    }

    public void setAssignStatus(String assignStatus) {
        this.assignStatus = assignStatus;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }
}
