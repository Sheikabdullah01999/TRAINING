package com.grootan.assetManagement.Service;

import com.google.gson.Gson;
import com.grootan.assetManagement.Exception.AlreadyExistsException;
import com.grootan.assetManagement.Exception.FieldEmptyException;
import com.grootan.assetManagement.Exception.GeneralException;
import com.grootan.assetManagement.Exception.ResourceNotFoundException;
import com.grootan.assetManagement.Model.*;
import com.grootan.assetManagement.Repository.*;
import com.grootan.assetManagement.Response;
import com.grootan.assetManagement.request.EmployeeDepartmentRequest;
import com.grootan.assetManagement.request.EmployeeRequest;
import com.grootan.assetManagement.response.EmployeeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import static com.grootan.assetManagement.Model.Constants.*;

@Service
public class EmployeeService {
    @Autowired
    RoleDao roleDao;
    @Autowired
    DeviceDao deviceDao;
    @Autowired
    private EmployeeDao employeeDao;

    public EmployeeDao getEmployeeDao() {
        return employeeDao;
    }

    @Autowired
    public void setEmployeeDao(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    @Autowired
    EmployeeDepartmentDao employeeDepartmentDao;
    @Autowired
    HistoryDao historyDao;
    @Autowired
    private CommonService service;
    @Autowired
    private PasswordEncoder passwordEncoder;


    Logger logger = Logger.getLogger("com.grootan.assetManagement.Service");

    /**
     * To check if the given email is valid or not
     * @param email
     * @return
     */
    public static boolean isValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }


    /**
     * save history
     * @param o
     * @param constant
     */
    public void saveHistory(Object o,String constant)
    {
        String userName=service.currentUser();
        History saveHistory=new History(userName,constant,new Gson().toJson(o),service.DateAndTime());
          historyDao.save(saveHistory);

    }
    //get all departments from the table

    /**
     * this method will give all the employee department from the table
     * @return
     * @throws ResourceNotFoundException
     */
    public ResponseEntity<Object> getAllEmpDepartments() throws ResourceNotFoundException {
        List<EmployeeDepartment> employeeDepartments=employeeDepartmentDao.findAll();
        if (employeeDepartments.isEmpty())
        {
            throw new ResourceNotFoundException(NO_RECORDS);
        }

        return   new ResponseEntity<>(
                new Response<>(String.valueOf(HttpStatus.OK.value()),
                        HttpStatus.OK, DEPARTMENT_FOUND, employeeDepartments),
                new HttpHeaders(), HttpStatus.OK);
    }

    /**
     * to get all the employee details
     *
     * @return
     * @throws ResourceNotFoundException
     */
    public ResponseEntity<Object> getAllEmployees() throws ResourceNotFoundException {
        List<Employee> employeeList = (List<Employee>) employeeDao.findAll();
        List<EmployeeResponse> employeeInformation=new ArrayList<>();
        for(Employee employee:employeeList)
        {
            employeeInformation.add(new EmployeeResponse(employee.getEmpId(),employee.getEmail(),employee.getEmpName(),employee.getEmpPassword(),employee.getDepartment(),employee.getRole(),employee.getDevices()));
        }
        if (employeeList.isEmpty())
        {
            throw new ResourceNotFoundException(NO_RECORDS);
        }

        return   new ResponseEntity<>(
                new Response<>(String.valueOf(HttpStatus.OK.value()), HttpStatus.OK, EMPLOYEE_FOUND, employeeInformation),
                new HttpHeaders(), HttpStatus.OK);
    }

    /**
     *update device  assign status after it assign to the employee
     * to fill the details to the employee object to save in the data base
     * @param employeeDetails
     * @return
     */
    public Employee saveEmpDetails(EmployeeRequest employeeDetails) throws ResourceNotFoundException {

        String devices= employeeDetails.getEmpDevices();

        List<Device> device=new ArrayList<>();

        if(devices!=null)
        {
            List<Integer> id=getDeviceID(devices);
            for(int i=0;i<id.size();i++)
            {
                deviceDao.updateAssignStatus(id.get(i));
            }

            for(Integer deviceId : id)
            {
                Device device1=deviceDao.getByDeviceId(deviceId);
                device.add(new Device(device1.getDeviceId(),device1.getManufacturedId(),device1.getCategory(),device1.getDeviceName(),
                        device1.getDevicePurchaseDate(),device1.getAssignStatus(),device1.getDeviceStatus()));
            }
        }

        EmployeeDepartment employeeDepartment=new EmployeeDepartment(employeeDetails.getEmpDepartment());
        List<EmployeeDepartment> employeeList=  employeeDepartmentDao.findAll();

        int flag=0;
        for(EmployeeDepartment department:employeeList)
        {

            if((department.getDepartment().equals(employeeDetails.getEmpDepartment())))
            {
                flag=1;
                break;
            }
        }
        if(flag==0)
        {
            throw new ResourceNotFoundException("department unavailable");
        }

//        List<Role> roles= (List<Role>) roleDao.findAll();
//        for(Role role:roles)
//        {
//            flag=0;
//            if(role.getRoleName().equals(employeeDetails.getAssignRole()))
//            {
//                flag=1;
//            }
//        }
//        if(flag==0)
//        {
//            throw new ResourceNotFoundException("role unavailable");
//        }


        return new Employee(employeeDetails.getEmpId(),
                employeeDetails.getEmpName(), employeeDetails.getEmail(),
                passwordEncoder.encode(employeeDetails.getEmpPassword()),
                employeeDepartment,
                employeeDetails.getAssignRole(),
                List.of(new Role(employeeDetails.getAssignRole())),
                device,
                employeeDetails.getEmpDepartment());
    }

    /**
     * validation on employee mandatory filed
     * employee email validation
     * employee password
     * employee email exists
     * employee id exists or not
     * @param employeeDetails
     * @throws FieldEmptyException
     */

    public void validate(EmployeeRequest employeeDetails) throws FieldEmptyException {

        if (employeeDetails.getEmpPassword().isEmpty() || employeeDetails.getEmpDepartment().isEmpty() ||
                employeeDetails.getAssignRole().isEmpty() || employeeDetails.getEmpId().isEmpty() || employeeDetails.getEmpName().isEmpty()) {
            throw new FieldEmptyException(FIELD_EMPTY);
        }

        Boolean validEmail = isValid(employeeDetails.getEmail());
        if (Boolean.FALSE.equals(validEmail)) {
            throw new GeneralException(INCORRECT_EMAIL + employeeDetails.getEmail());
        }

        if (employeeDetails.getEmpPassword().length() < 9) {
            throw new GeneralException(PASSWORD_LENGTH);
        }
    }

    public void employeeIdExistsValidation(String employeeID)
    {
        if (employeeIdExists(employeeID))
        {
            throw new GeneralException(EMP_ID_EXISTS + employeeID);
        }
    }

    /**
     * email already exists validation
     * @param email
     */
    public void emailExistsValidation(String email)
    {
        if (emailExists(email))
        {
            throw new GeneralException(EMP_EMAIL_EXISTS + email);
        }
    }


    /**
     * get device by device id
     * to get device id from the device list
     * */

    public List<Integer> getDeviceID(String device)
    {
        List<String> list = null;
        if (device != null)
        {
            list = Arrays.asList(device.split(","));
        }

        List<Integer> deviceId = new ArrayList<>();
        assert list != null;
        if(!list.isEmpty())
        {
            for (String empId : list)
            {

                String temp = "";
                for (int i = 0; i < empId.length(); i++)
                {
                    if (Character.isDigit(empId.charAt(i)))
                    {
                        temp = temp + String.valueOf(empId.charAt(i));
                    }
                }
                if (temp != "")
                {
                    deviceId.add(Integer.parseInt(temp));
                }
            }
        }
        return deviceId;
    }

    /**save employee details
     * before save  check all employee details are  correct
     * check all the mandatory fields
     * */
    public ResponseEntity<Object> saveEmployee(EmployeeRequest employeeDetails) throws FieldEmptyException, ResourceNotFoundException {
        emailExistsValidation(employeeDetails.getEmail());
        validate(employeeDetails);
        Employee employee=saveEmpDetails(employeeDetails);
        employeeDao.save(employee);
        logger.info("new employee registered success");
        return new ResponseEntity<>(
                new Response<>(String.valueOf(HttpStatus.CREATED.value()), HttpStatus.CREATED, SAVED_SUCCESSFUL,employeeDetails),
                new HttpHeaders(), HttpStatus.CREATED);
    }


    public ResponseEntity<Object> updateEmployee(EmployeeRequest employeeDetails) throws FieldEmptyException, ResourceNotFoundException, AlreadyExistsException {
        Employee existingEmployee=employeeDao.findByEmpId(employeeDetails.getEmpId());
        if(existingEmployee==null)
        {
            throw new ResourceNotFoundException("employee didn't exists");
        }

        validate(employeeDetails);


        List<Integer> updatedDeviceList=new ArrayList<>();

        List<Device> device=new ArrayList<>();

        List<Integer> existingDevice=deviceDao.deviceId(employeeDetails.getEmpId());
        List<Integer> newDevice=getDeviceID(employeeDetails.getEmpDevices());
        for(Integer existingId:existingDevice)
        {
            for(Integer newID:newDevice)
            {
                if(existingId==newID)
                {
                    throw new AlreadyExistsException("device unavailable: "+newID);
                }
            }
        }

        if(!existingDevice.isEmpty())
        {
            updatedDeviceList.addAll(existingDevice);
        }

        if(employeeDetails.getEmpDevices()!="")
        {

            for(Integer id:newDevice)
            {
                updatedDeviceList.add(id);
            }
            for(Integer Id : updatedDeviceList)
            {
                Device addDevices=deviceDao.getByDeviceId(Id);
                device.add(addDevices);
                //device.add(new Device(addDevices.getDeviceId(),addDevices.getManufacturedId(),addDevices.getCategory(),addDevices.getDeviceName(),addDevices.getDevicePurchaseDate(),addDevices.getAssignStatus(),addDevices.getDeviceStatus()));
            }
            updateAssignStatus(updatedDeviceList);
        }

        EmployeeDepartment employeeDepartment=new EmployeeDepartment(employeeDetails.getEmpDepartment());
        List<EmployeeDepartment> employeeList=  employeeDepartmentDao.findAll();

//        int flag=0;
//        for(EmployeeDepartment department:employeeList)
//        {
//
//            if((department.getDepartment().equals(employeeDetails.getEmpDepartment())))
//            {
//                flag=1;
//                break;
//            }
//        }
//        if(flag==0)
//        {
//            throw new ResourceNotFoundException("department unavailable");
//        }
//
//        List<Role> roles= (List<Role>) roleDao.findAll();
//        for(Role role:roles)
//        {
//            flag=0;
//            if(role.getRoleName().equals(employeeDetails.getAssignRole()))
//            {
//                flag=1;
//            }
//        }
//        if(flag==0)
//        {
//            throw new ResourceNotFoundException("role unavailable");
//        }
        existingEmployee.setEmpId(employeeDetails.getEmpId());
        existingEmployee.setEmpName(employeeDetails.getEmpName());
        existingEmployee.setEmail(employeeDetails.getEmail());
        existingEmployee.setAssignRole(employeeDetails.getAssignRole());
        existingEmployee.setDepartment(employeeDepartment);
        existingEmployee.setRole(new LinkedList<>(List.of(new Role(employeeDetails.getAssignRole()))));
        existingEmployee.setDevices(device);
        existingEmployee.setEmpDepartment(employeeDetails.getEmpDepartment());

        employeeDao.save(existingEmployee);
        logger.info("employee updated  success");
        return new ResponseEntity<>(
                new Response<>(String.valueOf(HttpStatus.CREATED.value()), HttpStatus.CREATED, "updated successfully"),
                new HttpHeaders(),
                HttpStatus.CREATED);
    }

    /**
     * to check  email already exits or not
     **/
    private boolean emailExists(final String email)
    {
        return employeeDao.findByEmail(email)!=null;
    }

    /**
     * to check employee id  already exits or not
     * */

    private boolean employeeIdExists(final String empId)
    {
        return employeeDao.findByEmpId(empId)!=null;
    }


    private boolean departmentExists(final String department)
    {
        return employeeDepartmentDao.findByDepartmentName(department)!=null;
    }

    // list all user with devices
    public ResponseEntity<Object> getUserDevices() throws ResourceNotFoundException
    {
        List<EmployeeDevices> userDevices = employeeDao.getUserDevice();
        if(userDevices.isEmpty())
        {
            throw new ResourceNotFoundException(NO_RECORDS);
        }
        return   new ResponseEntity<>(
                new Response<>(String.valueOf(HttpStatus.OK.value()), HttpStatus.OK, "device found",userDevices),
                new HttpHeaders(),
                HttpStatus.OK);
    }

    /**
     * find employee by employee id
     */

    public ResponseEntity<Object> findEmployeeById(String id) throws ResourceNotFoundException {
        Employee employee = employeeDao.findByEmpId(id);
        if(employee==null)
        {
            throw new ResourceNotFoundException(NO_RECORDS);
        }
        EmployeeResponse employeeResponse= new EmployeeResponse(employee.getEmpId(),employee.getEmail(),
                employee.getEmpName(),employee.getEmpPassword(),employee.getDepartment(),
                        employee.getRole(),employee.getDevices());


        return   new ResponseEntity<>(
                new Response<>(String.valueOf(HttpStatus.OK.value()), HttpStatus.OK, "employee found",employeeResponse),
                new HttpHeaders(),
                HttpStatus.OK);
    }


    //search by keyword using like
    public List<Employee> getByKeyword(String keyword){
        return employeeDao.findByKeyword(keyword);
    }

    //current log in user detail validation
    public void  currentLoggedInUserValidation(String  id)
    {
        String currentUser=employeeDao.getEmployeeMail(id);
        if(currentUser!=null)
        {
            if(currentUser.equals(service.currentUser()))
            {
                throw  new GeneralException("cannot delete current user");
            }
            else if(id.equals("G001"))
            {
                throw  new GeneralException("cannot delete default admin account");
            }
        }

    }

    //updated device size
    public void updateAssignStatus(List<Integer> updatedDevice)
    {
        for(int i=0;i<updatedDevice.size();i++)
        {
            deviceDao.updateAssignStatus(updatedDevice.get(i));
        }
    }


    //delete employee devices by employeeId
    public ResponseEntity<Object> deleteEmpDevices(int id) throws ResourceNotFoundException {
        EmployeeDevices device=employeeDao.getUserDevices(id);
        if (device==null)
        {
            throw new ResourceNotFoundException(NO_RECORDS);
        }

        deviceDao.updateAssignStatusAndDeviceStatus(id);
        employeeDao.deleteEmployeeByEmpDevice(id);
        logger.info("employee device deleted success");
        return   new ResponseEntity<>(
                new Response<>(String.valueOf(HttpStatus.OK.value()), HttpStatus.OK, "deleted successful"),
                new HttpHeaders(),
                HttpStatus.OK);
    }

    public ResponseEntity<Object> deleteEmployeeDepartment(String department) throws ResourceNotFoundException {
        EmployeeDepartment empDep= employeeDepartmentDao.findByDepartmentName(department);
        if(empDep==null)
        {
            throw new ResourceNotFoundException(NO_RECORDS);
        }
        employeeDepartmentDao.deleteDepartment(department);
        return   new ResponseEntity<>(
                new Response<>(String.valueOf(HttpStatus.OK.value()), HttpStatus.OK, "deleted successful"),
                new HttpHeaders(),
                HttpStatus.OK);
    }

    public ResponseEntity<Object> deleteEmpDetails(String empID) throws ResourceNotFoundException {
        if(employeeDao.findByEmpId(empID)==null)
        {
            throw new ResourceNotFoundException(NO_RECORDS);
        }
        currentLoggedInUserValidation(empID);

        List<Integer> empDevice=deviceDao.deviceId(empID);
        employeeDao.deleteByEmpId(empID);
        for(Integer dId:empDevice)
        {
            deviceDao.updateAssignStatusAndDeviceStatus(dId);
        }
        logger.info("employee deleted  successful");
        saveHistory(empID,EMP_DELETE);

        return   new ResponseEntity<>(
                new Response<>(String.valueOf(HttpStatus.OK.value()), HttpStatus.OK, "employee deleted success"),
                new HttpHeaders(),
                HttpStatus.OK);
    }

    public ResponseEntity<Object> saveEmpDepartment(EmployeeDepartmentRequest employeeDepartment) throws FieldEmptyException, AlreadyExistsException {

        String lower = employeeDepartment.getDepartment().toLowerCase();
        if(employeeDepartment.getDepartment().isEmpty())
        {
            throw new FieldEmptyException("field should not empty");
        }

        if(departmentExists(lower))
        {
            throw new AlreadyExistsException("Department Name  Already Exists: "+employeeDepartment.getDepartment());
        }

        saveHistory(employeeDepartment,EMP_DEPARTMENT_ADD);
        employeeDepartment.setDepartment(lower);

        logger.info("saved success");
        EmployeeDepartment department= new EmployeeDepartment(employeeDepartment.getDepartment());
        employeeDepartmentDao.save(department);
        return new ResponseEntity<>(
                new Response<>(String.valueOf(HttpStatus.CREATED.value()), HttpStatus.CREATED, "successfully saved",employeeDepartment),
                new HttpHeaders(),
                HttpStatus.CREATED);
    }

    public EmployeeDevices employeeDevices(int id)
    {
        EmployeeDevices empDevice=employeeDao.getUserDevices(id);
        return empDevice;
    }

}