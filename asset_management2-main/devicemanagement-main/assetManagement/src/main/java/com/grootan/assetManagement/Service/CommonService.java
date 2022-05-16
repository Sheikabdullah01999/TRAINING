package com.grootan.assetManagement.Service;

import com.grootan.assetManagement.Model.Device;
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

    public void initRoleAndUser() {

        Role adminRole = new Role();
        adminRole.setRoleName("ADMIN");
        adminRole.setRoleDescription("Admin role");
        roleDao.save(adminRole);

        Role userRole = new Role();
        userRole.setRoleName("USER");
        userRole.setRoleDescription("Default role for newly created record");
        roleDao.save(userRole);

        EmployeeDepartment employeeDepartment=new EmployeeDepartment();
        employeeDepartment.setDepartment("admin");
        employeeDepartmentDao.save(employeeDepartment);


        Employee adminUser = new Employee();

        adminUser.setEmpId("G001");
        adminUser.setEmpName("grootan");
        adminUser.setEmail("grootan@gmail.com");
        adminUser.setEmpPassword(getEncodedPassword("gr00tan"));


//        List<EmployeeDepartment> departments=new ArrayList<>();
//        departments.add(employeeDepartment);
        adminUser.setEmpDepartment("admin");

        adminUser.setAssignRole(adminRole.getRoleName());
        Collection<Role> adminRoles = new ArrayList<>();
        adminRoles.add(adminRole);
        adminUser.setRole(adminRoles);

        adminDao.save(adminUser);
    }
}
