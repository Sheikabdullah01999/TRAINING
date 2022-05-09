package com.grootan.assetManagement.Model;

import lombok.*;

import java.util.Date;


@NoArgsConstructor
public class EmployeeDevices {
    private String empId;
    private Integer id;
    private String deviceName;
    private Date devicePurchaseDate;
    private String category;


    public EmployeeDevices(String empId, String deviceName, Integer id, Date devicePurchaseDate, String category) {
        this.empId = empId;
        this.deviceName = deviceName;
        this.id = id;
        this.devicePurchaseDate = devicePurchaseDate;
        this.category = category;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


}
