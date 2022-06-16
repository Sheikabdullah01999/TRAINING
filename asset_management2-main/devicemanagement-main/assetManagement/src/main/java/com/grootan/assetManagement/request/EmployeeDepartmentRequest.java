package com.grootan.assetManagement.request;

public class EmployeeDepartmentRequest {
    String department;

    public EmployeeDepartmentRequest()
    {

    }
    public EmployeeDepartmentRequest(String department) {
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
