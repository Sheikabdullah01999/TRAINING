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

    public Employee(String empId, String email, String empName, String empPassword, String assignRole, String empDepartment, EmployeeDepartment department, Collection<Role> role, List<Device> devices) {
        this.empId = empId;
        this.email = email;
        this.empName = empName;
        this.empPassword = empPassword;
        this.assignRole = assignRole;
        this.empDepartment = empDepartment;
        this.department = department;
        this.role = role;
        this.devices = devices;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(name = "department_emp",
            joinColumns = @JoinColumn(name = "emp_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id"))
    private EmployeeDepartment department;


    @Transient
    private String empDevices;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "user_role",
            joinColumns = {
                    @JoinColumn(name = "emp_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "role_id")
            }
    )
    private Collection<Role> role;

    @OneToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private List<Device> devices;

    public Employee()
    {

    }

}