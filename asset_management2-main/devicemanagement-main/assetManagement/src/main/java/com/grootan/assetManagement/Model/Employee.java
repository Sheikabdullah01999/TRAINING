package com.grootan.assetManagement.Model;

import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity

@Component
@Table(name = "employee",uniqueConstraints = @UniqueConstraint(columnNames = {"email", "empId"}))
public class Employee {
    private String email;
    @Id
    private String empId;
    private String empName;
    private String empPassword;
    private String empDepartment;
    private String empDevices;
    private String assignRole;


    public Employee()
    {

    }


    public Employee(String empId, String empName, String email, String empPassword, String empDepartment, String empDevices, String assignRole, Collection<Role> role, List<Device> devices) {
        this.empId = empId;
        this.empName = empName;
        this.email = email;
        this.empPassword = empPassword;
        this.empDepartment = empDepartment;
        this.empDevices = empDevices;
        this.assignRole = assignRole;
        this.role = role;
        this.devices = devices;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
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


    public String getAssignRole() {
        return assignRole;
    }

    public void setAssignRole(String assignRole) {
        this.assignRole = assignRole;
    }
    @ManyToMany
    @JoinTable(name = "auth_user_role",
            joinColumns = {
                    @JoinColumn(name = "email")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "roleName")
            }
    )

    private Collection<Role> role;


    @OneToMany()
    private List<Device> devices;

    public List<Device> getDevice() {
        return devices;
    }

    public void setDevice(List<Device> devices) {
        this.devices = devices;
    }

    public Collection<Role> getRole() {
        return role;
    }

    public void setRole(Collection<Role> role) {
        this.role = role;
    }


}
