package com.grootan.assetManagement.Controller.RestController;

import com.grootan.assetManagement.Model.Employee;
import com.grootan.assetManagement.Model.EmployeeDepartment;
import com.grootan.assetManagement.Model.EmployeeDevices;
import com.grootan.assetManagement.Repository.EmployeeDao;
import com.grootan.assetManagement.Repository.EmployeeDepartmentDao;
import com.grootan.assetManagement.Service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@org.springframework.web.bind.annotation.RestController

public class RestController {
    @Autowired
    EmployeeDepartmentDao employeeDepartmentDao;
    @Autowired
    EmployeeDao employeeDao;
    @Autowired
    EmployeeService employeeService;

    @GetMapping("/GET/EMP")
    public ResponseEntity<List<EmployeeDepartment>> getEmployeeDepartment()
    {
        List<EmployeeDepartment> list=employeeDepartmentDao.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @DeleteMapping("/employee/department/delete/{id}")
    public ResponseEntity<List<EmployeeDepartment>> deleteEmployeeDepartment(String empDep)
    {
        employeeService.deleteEmployeeDepartment(empDep);
     List<EmployeeDepartment> list=employeeDepartmentDao.findAll();
     if(list.isEmpty())
     {
         return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
     }
     return  ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @PostMapping("/employee/registration")
    public ResponseEntity<Object> employeeRegistration(@RequestBody Employee employee)
    {
        employeeService.saveEmployee(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body("success");
    }

    @GetMapping("/employee/all/list")
    public ResponseEntity<List<Employee>> getAllEmployeeList()
    {
        List<Employee> employeeList=employeeService.getAllEmployees();
        if(employeeList.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return  ResponseEntity.status(HttpStatus.OK).body(employeeList);
    }

    @GetMapping("/employee/user/devices/all")
    public ResponseEntity<List<EmployeeDevices>> getAllEmployeeDevice()
    {
        List<EmployeeDevices>  list=employeeService.getUserDevices();
        if(list.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @DeleteMapping("/employee/device/delete/{id}")
    public ResponseEntity<Object> deleteEmployeeDevice(int id)
    {
       EmployeeDevices devices=employeeService.employeeDevices(id);
       if(devices==null)
       {
           return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("no records found");
       }
       employeeService.deleteEmpDevices(id);
       return ResponseEntity.status(HttpStatus.OK).body("deleted Successfully");
    }

    @PutMapping("/employee/update/{id}")
    public ResponseEntity<List<Employee>> updateEmployee(@PathVariable(name="id") String  empId,@RequestBody Employee employee)
    {
        Employee employee1=employeeService.findEmployeeById(empId);
        if(employee1==null)
        {
           return  ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        employeeService.updateEmployee(employee1);
        List<Employee> list=employeeService.getAllEmployees();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }



}
