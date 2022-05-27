package com.grootan.assetManagement.Controller.RestController;

import com.grootan.assetManagement.Exception.GeneralException;
import com.grootan.assetManagement.Model.Employee;
import com.grootan.assetManagement.Model.EmployeeDepartment;
import com.grootan.assetManagement.Model.EmployeeDevices;
import com.grootan.assetManagement.Repository.EmployeeDao;
import com.grootan.assetManagement.Repository.EmployeeDepartmentDao;
import com.grootan.assetManagement.Service.EmployeeService;
import com.grootan.assetManagement.request.EmployeeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
public class EmployeeRestController {
    @Autowired
    EmployeeDepartmentDao employeeDepartmentDao;
    @Autowired
    EmployeeDao employeeDao;
    @Autowired
    EmployeeService employeeService;

    @GetMapping("/employee/department/emp")
    public ResponseEntity getEmployeeDepartment()
    {
        return employeeService.getAllEmpDepartments();
    }

    @DeleteMapping("/employee/department/delete/{id}")
    public ResponseEntity deleteEmployeeDepartment(String empDep)
    {

        return  employeeService.deleteEmployeeDepartment(empDep);
    }

    @PostMapping("/employee/add")
    public ResponseEntity employeeRegistration(@RequestBody EmployeeRequest employeeRequest)
    {
            return employeeService.saveEmployee(employeeRequest);
    }

    @GetMapping("/employee/all/list")
    public ResponseEntity getAllEmployeeList()
    {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/employee/user/devices/all")
    public ResponseEntity getAllEmployeeDevice()
    {

        return employeeService.getUserDevices();
    }

    @DeleteMapping("/employee/device/delete/{id}")
    public ResponseEntity deleteEmployeeDevice(@PathVariable(name="id") int id)
    {
        return employeeService.deleteEmpDevices(id);
    }

    @PutMapping("/employee/update")
    public ResponseEntity updateEmployee(@RequestBody EmployeeRequest employee)
    {
        return employeeService.updateEmployee(employee);
    }

    @PostMapping("/employee/department/save")
    public ResponseEntity addEmployeeDepartment(@RequestBody EmployeeDepartment dep)
    {

            return  employeeService.saveEmpDepartment(dep);

    }

    @DeleteMapping("/delete/employee/{id}")
    public  ResponseEntity deleteEmployeeById(@PathVariable(name="id") String empId)
    {

        return employeeService.deleteEmpDetails(empId);
    }

}
