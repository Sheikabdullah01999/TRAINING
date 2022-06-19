package com.grootan.assetManagement.Service;

import com.grootan.assetManagement.Model.Employee;
import com.grootan.assetManagement.Model.EmployeeDepartment;
import com.grootan.assetManagement.Model.Role;
import com.grootan.assetManagement.Repository.AdminDao;
import com.grootan.assetManagement.Repository.EmployeeDao;
import com.grootan.assetManagement.Repository.EmployeeDepartmentDao;
import com.grootan.assetManagement.Repository.RoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CommonService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmployeeDao employeeDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private AdminDao adminDao;

    @Autowired
    private EmployeeDepartmentDao employeeDepartmentDao;

    //password encoder
    public String getEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public String currentUser()
    {
        Authentication authentication=getCurrentUser();
        return  authentication.getName();
    }

    public Authentication getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        return authentication;
    }

    //current date and time in AM PM format
    public String DateAndTime()
    {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
        return dateFormat.format(new Date()).toString();
    }

    public Employee loginEmployeeDetails(String name) {
        Employee employee=employeeDao.findByEmail(name);
        return  employee;
    }

    public void initRoleAndUser()
    {
        EmployeeDepartment employeeDepartment=new EmployeeDepartment();
        employeeDepartment.setId(1);
        employeeDepartment.setDepartment("FrontEnd");
        employeeDepartmentDao.save(employeeDepartment);
        employeeDepartment.setId(2);
        employeeDepartment.setDepartment("BackEnd");
        employeeDepartmentDao.save(employeeDepartment);


        Role adminRole = new Role();
        adminRole.setId(2);
        adminRole.setRoleName("ADMIN");
        adminRole.setRoleDescription("Admin role");


        Role userRole = new Role();
        userRole.setId(1);
        userRole.setRoleName("USER");
        userRole.setRoleDescription("Default role for newly created record");
        roleDao.save(userRole);

        EmployeeDepartment adminEmployeeDepartment=new EmployeeDepartment();
        adminEmployeeDepartment.setDepartment("admin");

        EmployeeDepartment userEmployeeDepartment=new EmployeeDepartment();
        userEmployeeDepartment.setDepartment("user");

        Employee adminUser = new Employee();

        adminUser.setEmpId("G001");
        adminUser.setEmpName("grootan");
        adminUser.setEmail("grootan@gmail.com");
        adminUser.setEmpPassword(getEncodedPassword("gr00tan"));
        adminUser.setEmpDepartment(adminEmployeeDepartment.getDepartment());
        adminUser.setAssignRole(adminRole.getRoleName());
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        adminUser.setRole(adminRoles);

        adminDao.save(adminUser);
    }
}