package com.grootan.assetManagement.request;

public class EmployeeUpdateRequest {
    private String email;
    private String empName;
    private String empPassword;
    private String assignRole;
    private String empDepartment;
    private String empDevices;

    public EmployeeUpdateRequest(String email, String empName, String empPassword, String assignRole, String empDepartment, String empDevices) {

        this.email = email;
        this.empName = empName;
        this.empPassword = empPassword;
        this.assignRole = assignRole;
        this.empDepartment = empDepartment;
        this.empDevices = empDevices;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmpPassword() {
        return empPassword;
    }

    public void setEmpPassword(String empPassword) {
        this.empPassword = empPassword;
    }

    public String getAssignRole() {
        return assignRole;
    }

    public void setAssignRole(String assignRole) {
        this.assignRole = assignRole;
    }

    public String getEmpDepartment() {
        return empDepartment;
    }

    public void setEmpDepartment(String empDepartment) {
        this.empDepartment = empDepartment;
    }

    public String getEmpDevices() {
        return empDevices;
    }

    public void setEmpDevices(String empDevices) {
        this.empDevices = empDevices;
    }
}
