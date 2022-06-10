package com.grootan.assetManagement.Controller.RestController;

import com.grootan.assetManagement.Exception.AlreadyExistsException;
import com.grootan.assetManagement.Exception.FieldEmptyException;
import com.grootan.assetManagement.Exception.GeneralException;
import com.grootan.assetManagement.Exception.ResourceNotFoundException;
import com.grootan.assetManagement.Model.Employee;
import com.grootan.assetManagement.Model.EmployeeDepartment;
import com.grootan.assetManagement.Model.EmployeeDevices;
import com.grootan.assetManagement.Repository.EmployeeDao;
import com.grootan.assetManagement.Repository.EmployeeDepartmentDao;
import com.grootan.assetManagement.Response;
import com.grootan.assetManagement.Service.EmployeeService;
import com.grootan.assetManagement.request.EmployeeRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
public class EmployeeRestController {
    @Autowired
    EmployeeDepartmentDao employeeDepartmentDao;
    @Autowired
    EmployeeDao employeeDao;
    @Autowired
    EmployeeService employeeService;

    @GetMapping("/employee/department/emp")
    public List<EmployeeDepartment> getEmployeeDepartment()
    {
        return (List<EmployeeDepartment>) employeeDepartmentDao.findAll();
    }

    @DeleteMapping("/employee/department/delete/{id}")
    public ResponseEntity deleteEmployeeDepartment(@PathVariable(name="id") String empDep) throws ResourceNotFoundException {

        return  employeeService.deleteEmployeeDepartment(empDep);
    }

    @PostMapping("/employee/add")
    public ResponseEntity employeeRegistration(@RequestBody Employee employeeRequest) throws FieldEmptyException {

        return employeeService.saveEmployee(employeeRequest);
    }

    @GetMapping("/employee/all/list")
    public ResponseEntity<List<Employee>> getAllEmployeeList()
    {
        List<Employee> employeeList=employeeDao.findAll();
        if(employeeList.isEmpty())
        {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return  ResponseEntity.status(HttpStatus.OK).body(employeeList);
    }

    @GetMapping("/employee/user/devices/all")
    public ResponseEntity getAllEmployeeDevice()
    {
        List<EmployeeDevices>  list=employeeDao.getUserDevice();
        if(list.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @DeleteMapping("/employee/device/delete/{id}")
    public ResponseEntity deleteEmployeeDevice(@PathVariable(name="id") int id) throws ResourceNotFoundException {
        return employeeService.deleteEmpDevices(id);
    }

    @PutMapping("/employee/update")
    public ResponseEntity updateEmployee(@RequestBody Employee employee) throws FieldEmptyException, ResourceNotFoundException {
        return employeeService.updateEmployee(employee);
    }

    @PostMapping("/employee/department/save")
    public ResponseEntity addEmployeeDepartment(@RequestBody EmployeeDepartment dep) throws FieldEmptyException, AlreadyExistsException {

        return  employeeService.saveEmpDepartment(dep);

    }

    @DeleteMapping("/delete/employee/{id}")
    public  ResponseEntity deleteEmployeeById(@PathVariable(name="id") String empId) throws ResourceNotFoundException
    {
        return employeeService.deleteEmpDetails(empId);
    }

}