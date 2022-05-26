package com.grootan.assetManagement.Controller.RestController;

import com.grootan.assetManagement.Exception.GeneralException;
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
@RestController
public class EmployeeRestController {
    @Autowired
    EmployeeDepartmentDao employeeDepartmentDao;
    @Autowired
    EmployeeDao employeeDao;
    @Autowired
    EmployeeService employeeService;

    @GetMapping("/employee/department/emp")
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

    @PostMapping("/employee/add")
    public ResponseEntity<Object> employeeRegistration(@RequestBody Employee employee)
    {
        try
        {
            return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.saveEmployee(employee));
        }
        catch(GeneralException e)
        {
            return ResponseEntity.status(HttpStatus.CREATED).body(e.getMessage());
        }

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
    public ResponseEntity<List<EmployeeDevices>> deleteEmployeeDevice(@PathVariable(name="id") int id)
    {
        employeeService.deleteEmpDevices(id);
        List<EmployeeDevices> userDevices = employeeService.getUserDevices();
        return ResponseEntity.status(HttpStatus.OK).body(userDevices);
    }

    @PutMapping("/employee/update")
    public ResponseEntity<List<Employee>> updateEmployee(@RequestBody Employee employee)
    {

        employeeService.updateEmployee(employee);
        List<Employee> list=employeeService.getAllEmployees();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @PostMapping("/employee/department/save")
    public ResponseEntity<Object> addEmployeeDepartment(@RequestBody EmployeeDepartment dep)
    {
        try
        {
            return  ResponseEntity.status(HttpStatus.CREATED).body(employeeService.saveEmpDepartment(dep));
        }
        catch(GeneralException e)
        {
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }

    }

    @DeleteMapping("/delete/employee/{id}")
    public  ResponseEntity<List<Employee>> deleteEmployeeById(@PathVariable(name="id") String empId)
    {
        employeeService.deleteEmpDetails(empId);
        List<Employee> list=employeeService.getAllEmployees();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

}
