package com.grootan.assetManagement.Model;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Component
@Table(name = "employee",uniqueConstraints = @UniqueConstraint(columnNames = {"email", "id","empId"}))
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String email;
    private String empId;
    private String empName;
    private String empPassword;
    private String assignRole;
    private String empDepartment;


    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinTable(name = "department_emp",
            joinColumns = @JoinColumn(name = "emp_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id"))
    private EmployeeDepartment department;


    @Transient
    private String empDevices;
    @ManyToMany
    @JoinTable(name = "user_role",
            joinColumns = {
                    @JoinColumn(name = "emp_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "role_id")
            }
    )
    private Collection<Role> role;
    //@org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
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
}