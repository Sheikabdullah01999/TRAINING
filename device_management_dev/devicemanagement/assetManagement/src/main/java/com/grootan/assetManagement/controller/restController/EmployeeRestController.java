package com.grootan.assetManagement.controller.restController;

import com.grootan.assetManagement.exception.AlreadyExistsException;
import com.grootan.assetManagement.exception.FieldEmptyException;
import com.grootan.assetManagement.exception.ResourceNotFoundException;
import com.grootan.assetManagement.response.Response;
import com.grootan.assetManagement.service.employeeService.EmployeeService;
import com.grootan.assetManagement.request.EmployeeDepartmentRequest;
import com.grootan.assetManagement.request.EmployeeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@ApiResponses(value =
        {
            @ApiResponse(responseCode = "200", description = "OK",content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "201", description = "CREATED",content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "302", description = "FOUND",content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "NO_RECORDS",content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "406", description = "NOT_ACCEPTABLE",content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "409", description = "CONFLICT",content = @Content(schema = @Schema(implementation = Response.class)))
        })

@RestController
public class EmployeeRestController
{
    @Autowired
    EmployeeService employeeService;

    /**
     * Rest Api to get all the employee department
     * @return
     * @throws ResourceNotFoundException
     */
    @GetMapping("v1/employee/department")
    @Operation(description = "Rest service to get employee department", summary = "employeeDepartment")
    public ResponseEntity<Object> getEmployeeDepartment() throws ResourceNotFoundException {
        return employeeService.getAllEmpDepartments();
    }

    /**
     * Rest api to get employee details
     * @return
     * @throws ResourceNotFoundException
     */
    @GetMapping("v1/employees")
    @Operation(description = "Rest service to get employees details", summary = "employeeDetails")
    public ResponseEntity<Object> getAllEmployeeList() throws ResourceNotFoundException {
        return employeeService.getAllEmployees();
    }

    /**
     * Rest Api to get employee devices
     * @return
     * @throws ResourceNotFoundException
     */
    @GetMapping("v1/employee/devices")
    @Operation(description = "Rest service to get employee Device", summary = "employeeDevice")
    public ResponseEntity<Object> getAllEmployeeDevice() throws ResourceNotFoundException {
        return employeeService.getUserDevices();
    }

    /**
     * Rest Api to get employee details by employee id
     * @param employeeId
     * @return
     * @throws ResourceNotFoundException
     */
    @GetMapping("v1/employee/{employeeId}")
    @Operation(description = "Rest service to get employee by employee id", summary = "employeeById")
    public ResponseEntity<Object> employeeDetailsById(@PathVariable String employeeId) throws ResourceNotFoundException {
        return employeeService.findEmployeeById(employeeId);
    }

    /**
     * Rest Api to create employee department
     * @param dep
     * @return
     * @throws FieldEmptyException
     * @throws AlreadyExistsException
     */
    @PostMapping("v1/employee/department")
    @Operation(description = "Rest service to create employee department", summary = "createDepartment")
    public ResponseEntity<Object> addEmployeeDepartment(@RequestBody EmployeeDepartmentRequest dep) throws FieldEmptyException, AlreadyExistsException {
        return employeeService.saveEmpDepartment(dep);
    }

    /**
     * Rest Api to create new employee
     * @param employeeId
     * @param employeeRequest
     * @return
     * @throws FieldEmptyException
     * @throws ResourceNotFoundException
     * @throws AlreadyExistsException
     */
    @PostMapping("v1/employee/{employeeId}")
    @Operation(description = "Rest service to create employee", summary = "createEmployee")
    public ResponseEntity<Object> employeeRegistration(@PathVariable String employeeId,@RequestBody EmployeeRequest employeeRequest) throws FieldEmptyException, ResourceNotFoundException {
        return employeeService.saveEmployee(employeeId,employeeRequest);
    }

    /**
     * Rest Api to update employee details
     * @param employeeId
     * @param employee
     * @return
     * @throws FieldEmptyException
     * @throws ResourceNotFoundException
     * @throws AlreadyExistsException
     */
    @PutMapping("v1/employee/{employeeId}")
    @Operation(description = "Rest service to update employee", summary = "updateEmployee")
    public ResponseEntity<Object> updateEmployee(@PathVariable String employeeId,@RequestBody EmployeeRequest employee) throws FieldEmptyException, ResourceNotFoundException{
        return employeeService.updateEmployee(employeeId,employee);
    }


    /**
     * delete employeeBY employee id
     * @param empId
     * @return
     * @throws ResourceNotFoundException
     */
    @DeleteMapping("v1/employee/{employeeId}")
    @Operation(description = "Rest service to delete employee", summary = "deleteEmployee")
    public  ResponseEntity<Object> deleteEmployeeById(@PathVariable(name = "employeeId") String empId) throws ResourceNotFoundException
    {
        return employeeService.deleteEmpDetails(empId);
    }


    /**
     * delete employee device employee id
     * @param id
     * @return
     * @throws ResourceNotFoundException
     */
    @DeleteMapping("v1/employee/device/{id}")
    @Operation(description = "Rest service to delete employee device", summary = "deleteEmployeeDevice")
    public ResponseEntity<Object> deleteEmployeeDevice(@PathVariable(name="id") int id) throws ResourceNotFoundException {
        return employeeService.deleteEmpDevices(id);
    }

    /**
     * delete employee department by department name
     * @param id
     * @return
     * @throws ResourceNotFoundException
     */
    @DeleteMapping("v1/employee/department/{departmentName}")
    @Operation(description = "Rest service to delete employee device", summary = "deleteEmployeeDevice")
    public ResponseEntity<Object> deleteEmployeeDepartment(@PathVariable(name = "departmentName") String id) throws ResourceNotFoundException {
        return employeeService.deleteEmployeeDepartment(id);
    }

}