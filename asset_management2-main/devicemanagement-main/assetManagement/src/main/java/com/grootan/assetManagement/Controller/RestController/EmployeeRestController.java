package com.grootan.assetManagement.Controller.RestController;

import com.grootan.assetManagement.Exception.AlreadyExistsException;
import com.grootan.assetManagement.Exception.FieldEmptyException;
import com.grootan.assetManagement.Exception.ResourceNotFoundException;
import com.grootan.assetManagement.Model.Employee;
import com.grootan.assetManagement.Model.EmployeeDepartment;
import com.grootan.assetManagement.Model.EmployeeDevices;
import com.grootan.assetManagement.Repository.EmployeeDao;
import com.grootan.assetManagement.Repository.EmployeeDepartmentDao;
import com.grootan.assetManagement.Service.EmployeeService;
import com.grootan.assetManagement.request.EmployeeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static com.grootan.assetManagement.Model.Constants.NO_RECORDS;

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
    public ResponseEntity<List<Employee>> getAllEmployeeList() throws ResourceNotFoundException {
        List<Employee> employeeList=employeeDao.findAll();
        if(employeeList.isEmpty())
        {

            throw new ResourceNotFoundException(NO_RECORDS);
        }
        return  ResponseEntity.status(HttpStatus.OK).body(employeeList);
    }

    @GetMapping("/employee/user/devices/all")
    public ResponseEntity<Object> getAllEmployeeDevice() throws ResourceNotFoundException {
        List<EmployeeDevices>  list=employeeDao.getUserDevice();
        if(list.isEmpty())
        {
            throw new ResourceNotFoundException(NO_RECORDS);
        }
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }


    @PostMapping("/employee/department/save")
    public ResponseEntity<Object> addEmployeeDepartment(@RequestBody EmployeeDepartment dep) throws FieldEmptyException, AlreadyExistsException
    {
        return  employeeService.saveEmpDepartment(dep);
    }

    @PostMapping("/employee/add")
    public ResponseEntity<Object> employeeRegistration(@RequestBody EmployeeRequest employeeRequest) throws FieldEmptyException
    {
        return employeeService.saveEmployee(employeeRequest);
    }

    @PutMapping("/employee/update")
    public ResponseEntity<Object> updateEmployee(@RequestBody EmployeeRequest employee) throws FieldEmptyException, ResourceNotFoundException
    {
        return employeeService.updateEmployee(employee);
    }

    @DeleteMapping("/delete/employee/{id}")
    public  ResponseEntity<Object> deleteEmployeeById(@PathVariable(name="id") String empId) throws ResourceNotFoundException
    {
        return employeeService.deleteEmpDetails(empId);
    }

    @DeleteMapping("/employee/device/delete/{id}")
    public ResponseEntity<Object> deleteEmployeeDevice(@PathVariable(name="id") int id) throws ResourceNotFoundException {
        return employeeService.deleteEmpDevices(id);
    }

}