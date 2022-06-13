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
import com.grootan.assetManagement.Service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    public EmployeeService getEmployeeService() {
        return employeeService;
    }

    @Autowired
    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Autowired
    EmployeeService employeeService;

    @GetMapping("/employee/department/emp")
    public List<EmployeeDepartment> getEmployeeDepartment()
    {
        return (List<EmployeeDepartment>) employeeDepartmentDao.findAll();
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
    public ResponseEntity<Object> getAllEmployeeDevice()
    {
        List<EmployeeDevices>  list=employeeDao.getUserDevice();
        System.out.println(list);
        if(list.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }


    @PostMapping("/employee/department/save")
    public ResponseEntity<Object> addEmployeeDepartment(@RequestBody EmployeeDepartment dep) throws FieldEmptyException, AlreadyExistsException
    {
        return  employeeService.saveEmpDepartment(dep);
    }

    @PostMapping("/employee/add")
    public ResponseEntity<Object> employeeRegistration(@RequestBody Employee employeeRequest) throws FieldEmptyException
    {
        return employeeService.saveEmployee(employeeRequest);
    }

    @PutMapping("/employee/update")
    public ResponseEntity<Object> updateEmployee(@RequestBody Employee employee) throws FieldEmptyException, ResourceNotFoundException
    {
        return employeeService.updateEmployee(employee);
    }

    @DeleteMapping("/delete/employee/{id}")
    public  ResponseEntity<Object> deleteEmployeeById(@PathVariable(name="id") String empId) throws ResourceNotFoundException
    {
        return employeeService.deleteEmpDetails(empId);
    }

    @DeleteMapping("/employee/department/delete/{id}")
    public ResponseEntity<Object> deleteEmployeeDepartment(@PathVariable(name="id") String empDep) throws ResourceNotFoundException {

        return  employeeService.deleteEmployeeDepartment(empDep);
    }

    @DeleteMapping("/employee/device/delete/{id}")
    public ResponseEntity<Object> deleteEmployeeDevice(@PathVariable(name="id") int id) throws ResourceNotFoundException {
        return employeeService.deleteEmpDevices(id);
    }

}