package com.grootan.assetManagement.response;

import com.grootan.assetManagement.Model.Device;
import com.grootan.assetManagement.Model.EmployeeDepartment;
import com.grootan.assetManagement.Model.Role;

import java.util.Collection;
import java.util.List;

public class EmployeeResponse
{
    private String empId;
    private String email;
    private String empName;
    private String empPassword;
    private EmployeeDepartment department;
    private Collection<Role> role;
    private List<Device> devices;

    public EmployeeResponse() {
    }

    public EmployeeResponse(String empId, String email, String empName, String empPassword, EmployeeDepartment department, Collection<Role> role, List<Device> devices) {
        this.empId = empId;
        this.email = email;
        this.empName = empName;
        this.empPassword = empPassword;
        this.department = department;
        this.role = role;
        this.devices = devices;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
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

    public EmployeeDepartment getDepartment() {
        return department;
    }

    public void setDepartment(EmployeeDepartment department) {
        this.department = department;
    }

    public Collection<Role> getRole() {
        return role;
    }

    public void setRole(Collection<Role> role) {
        this.role = role;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }
}
