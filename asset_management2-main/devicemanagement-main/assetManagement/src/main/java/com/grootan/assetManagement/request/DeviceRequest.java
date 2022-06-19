package com.grootan.assetManagement.request;

import lombok.NoArgsConstructor;
import java.sql.Date;


@NoArgsConstructor
public class DeviceRequest {

    private String manufacturedId;

    private String category;

    private String deviceName;

    private Date devicePurchaseDate;


    public DeviceRequest(String manufacturedId, String category, String deviceName, Date devicePurchaseDate) {
        this.manufacturedId = manufacturedId;
        this.category = category;
        this.deviceName = deviceName;
        this.devicePurchaseDate = devicePurchaseDate;
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

}
