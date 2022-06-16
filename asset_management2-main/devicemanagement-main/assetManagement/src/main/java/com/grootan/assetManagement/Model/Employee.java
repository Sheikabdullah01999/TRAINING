package com.grootan.assetManagement.Model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Component
@Table(name = "employee",uniqueConstraints = @UniqueConstraint(columnNames = {"email","empId"}))
public class Employee
{

    @Id
    private String empId;
    private String email;
    private String empName;
    private String empPassword;
    private String assignRole;
    private String empDepartment;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(name = "mapped_department",
            joinColumns = @JoinColumn(name = "emp_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id"))
    private EmployeeDepartment department;

    @Transient
    private String empDevices;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(name = "user_role",
            joinColumns = {
                    @JoinColumn(name = "emp_id",nullable = false)
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "role_id",nullable = false)
            }
    )
    private Collection<Role> role;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<Device> devices;

    public Employee()
    {
    }

    public Employee(String empId, String empName, String email, String empPassword, EmployeeDepartment empDepartment, String assignRole, Collection<Role> role, List<Device> devices,String department)
    {
        this.empId = empId;
        this.empName = empName;
        this.email = email;
        this.empPassword = empPassword;
        this.department = empDepartment;
        this.assignRole = assignRole;
        this.role = role;
        this.devices = devices;
        this.empDepartment=department;
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

    public EmployeeDepartment getDepartment() {
        return department;
    }

    public void setDepartment(EmployeeDepartment department) {
        this.department = department;
    }

    public String getEmpDevices() {
        return empDevices;
    }

    public void setEmpDevices(String empDevices) {
        this.empDevices = empDevices;
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