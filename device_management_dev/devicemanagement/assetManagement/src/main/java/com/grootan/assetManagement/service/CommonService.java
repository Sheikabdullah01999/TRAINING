package com.grootan.assetManagement.service;

import com.grootan.assetManagement.model.Device;
import com.grootan.assetManagement.model.Employee;
import com.grootan.assetManagement.model.EmployeeDepartment;
import com.grootan.assetManagement.model.Role;
import com.grootan.assetManagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
    DeviceDao deviceDao;
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
        return SecurityContextHolder.getContext().getAuthentication();
    }

    //current date and time in AM PM format
    public String DateAndTime()
    {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
        return dateFormat.format(new Date()).toString();
    }

    public Employee loginEmployeeDetails(String name) {
        return employeeDao.findByEmail(name);
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
        roleDao.save(adminRole);


        Role userRole = new Role();
        userRole.setId(1);
        userRole.setRoleName("USER");
        userRole.setRoleDescription("Default role for newly created record");

        roleDao.save(userRole);


        EmployeeDepartment adminEmployeeDepartment=new EmployeeDepartment();
        adminEmployeeDepartment.setId(1);
        adminEmployeeDepartment.setDepartment("admin");
        employeeDepartmentDao.save(adminEmployeeDepartment);


        Employee adminUser = new Employee();

        adminUser.setEmpId("G001");
        adminUser.setEmpName("grootan");
        adminUser.setEmail("g@g.com");
        adminUser.setEmpPassword(getEncodedPassword("root"));
        adminUser.setEmpDepartment("admin");
        adminUser.setAssignRole(adminRole.getRoleName());
        Collection<Role> adminRoles = new ArrayList<>();
        adminRoles.add(adminRole);
        adminUser.setRole(adminRoles);

        adminDao.save(adminUser);

    }
}