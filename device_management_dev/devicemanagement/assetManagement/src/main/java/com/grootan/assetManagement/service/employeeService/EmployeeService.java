package com.grootan.assetManagement.service.employeeService;

import com.google.gson.Gson;
import com.grootan.assetManagement.exception.AlreadyExistsException;
import com.grootan.assetManagement.exception.FieldEmptyException;
import com.grootan.assetManagement.exception.GeneralException;
import com.grootan.assetManagement.exception.ResourceNotFoundException;
import com.grootan.assetManagement.model.*;
import com.grootan.assetManagement.repository.*;
import com.grootan.assetManagement.response.Response;
import com.grootan.assetManagement.request.EmployeeDepartmentRequest;
import com.grootan.assetManagement.request.EmployeeRequest;
import com.grootan.assetManagement.response.EmployeeResponse;
import com.grootan.assetManagement.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static com.grootan.assetManagement.model.Constants.*;

@Service
public class EmployeeService
{

    @Autowired
    DeviceDao deviceDao;
    @Autowired
    private EmployeeDao employeeDao;
    @Autowired
    EmployeeDepartmentDao employeeDepartmentDao;
    @Autowired
    HistoryDao historyDao;
    @Autowired
    private CommonService service;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleDao roleDao;

    Logger logger = Logger.getLogger("com.grootan.assetManagement.Service");

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

        return  new ResponseEntity<>(
                new Response<>(String.valueOf(HttpStatus.OK.value()),
                        HttpStatus.OK, DEPARTMENT_FOUND, employeeDepartments),
                new HttpHeaders(), HttpStatus.OK);
    }

    /**
     * to get all the employee details
     * @throws ResourceNotFoundException
     */
    public ResponseEntity<Object> getAllEmployees() throws ResourceNotFoundException
    {
        List<Employee> employeeList = (List<Employee>) employeeDao.findAll();
        List<EmployeeResponse> employeeDetails=new ArrayList<>();

        for(Employee employee:employeeList)
        {
            employeeDetails.add(new EmployeeResponse(employee.getEmpId(),employee.getEmail(),employee.getEmpName(),employee.getEmpPassword(),employee.getDepartment(),employee.getRole(),employee.getDevices()));
        }
        if (employeeList.isEmpty())
        {
            throw new ResourceNotFoundException(NO_RECORDS);
        }

        return   new ResponseEntity<>(
                new Response<>(String.valueOf(HttpStatus.OK.value()), HttpStatus.OK, EMPLOYEE_FOUND, employeeDetails),
                new HttpHeaders(), HttpStatus.OK);
    }

    /**
     * update device  assign status after it assign to the employee
     * to fill the details to the employee object to save in the database
     * @param employeeDetails
     * @throws ResourceNotFoundException
     */
    public Employee constructEmployeeObject(String employeeId, EmployeeRequest employeeDetails) {

        String devices= employeeDetails.getEmpDevices();

        List<Device> device=new ArrayList<>();
        List<Integer> id=getDeviceID(devices);

        if(id!=null)
        {
            //device status update
            for(int i=0;i<id.size();i++)
            {
                deviceDao.updateAssignStatus(id.get(i));
            }

            //new device
            for(Integer deviceId : id)
            {
                Device newDevice=deviceDao.getByDeviceId(deviceId);
                device.add(new Device(newDevice.getDeviceId(),newDevice.getManufacturedId(),newDevice.getCategory(),newDevice.getDeviceName(),
                        newDevice.getDevicePurchaseDate(),newDevice.getAssignStatus(),newDevice.getDeviceStatus()));
                logger.info("new employee device added");
            }
        }

        EmployeeDepartment employeeDepartment=new EmployeeDepartment(employeeDetails.getEmpDepartment());
        return new Employee(employeeId,
                employeeDetails.getEmpName(), employeeDetails.getEmail(),
                passwordEncoder.encode(employeeDetails.getEmpPassword()),
                employeeDepartment,
                employeeDetails.getAssignRole(),
                List.of(new Role(employeeDetails.getAssignRole())),
                device,
                employeeDetails.getEmpDepartment());
    }



    /**save employee details
     * before save  check all employee details are  correct
     * check all the mandatory fields
     * */
    public ResponseEntity<Object> saveEmployee(String employeeId,EmployeeRequest employeeDetails) throws FieldEmptyException, ResourceNotFoundException
    {

        employeeIdExistsValidation(employeeId);
        emptyFieldAndIncorrectEmailAndPasswordLengthCheck(employeeId,employeeDetails);
        emailExistsValidation(employeeDetails.getEmail());
        availableDeviceCheck(employeeDetails);
        departmentAndRoleAvailabilityCheck(employeeDetails);
        logger.info("current object passed all the validation check");

        Employee employee= constructEmployeeObject(employeeId,employeeDetails);
        employeeDao.save(employee);
        logger.info("new employee registered success");
        return new ResponseEntity<>(
                new Response<>(String.valueOf(HttpStatus.CREATED.value()), HttpStatus.CREATED, SAVED_SUCCESSFUL,employeeDetails),
                new HttpHeaders(), HttpStatus.CREATED);
    }


    public ResponseEntity<Object> updateEmployee(String employeeId,EmployeeRequest employeeDetails) throws FieldEmptyException, ResourceNotFoundException
    {
        Employee existingEmployee=employeeDao.findByEmpId(employeeId);
        if(existingEmployee==null)
        {
            throw new ResourceNotFoundException("employee didn't exists");
        }

        emptyFieldAndIncorrectEmailAndPasswordLengthCheck(employeeId,employeeDetails);
        departmentAndRoleAvailabilityCheck(employeeDetails);
        availableDeviceCheck(employeeDetails);
        logger.info("current employee object passed all the validations");

        List<Integer> updatedDeviceList=new ArrayList<>();

        List<Device> device=new ArrayList<>();

        List<Integer> existingDevice=deviceDao.deviceId(employeeId);

        List<Integer> newDevice=getDeviceID(employeeDetails.getEmpDevices());
        if(!existingDevice.isEmpty())
        {
            updatedDeviceList.addAll(existingDevice);
        }

        if(!Objects.equals(employeeDetails.getEmpDevices(), ""))
        {
            updatedDeviceList.addAll(newDevice);
            for(Integer Id : updatedDeviceList)
            {
                Device addDevices=deviceDao.getByDeviceId(Id);
                device.add(addDevices);
                logger.info("new device added success");
            }
              updateAssignStatus(updatedDeviceList);
        }

        EmployeeDepartment employeeDepartment=new EmployeeDepartment(employeeDetails.getEmpDepartment());
        
        existingEmployee.setEmpId(employeeId);
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
     * to get all the employee device
     * @return
     * @throws ResourceNotFoundException
     */
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
     * to find employee by employee id
     * @param id
     * @return
     * @throws ResourceNotFoundException
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


    /**
     * current loggedIn user Validation
     * @param id
     */
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

    /**
     * to update device assign status after it assigned to employee
     * @param updatedDevice
     */
    //updated device size
    public void updateAssignStatus(List<Integer> updatedDevice)
    {
        for(int i=0;i<updatedDevice.size();i++)
        {
            deviceDao.updateAssignStatus(updatedDevice.get(i));
        }
    }


    /**
     * to delete employee device
     * and updating device assign status working to new
     * @param id
     * @return
     * @throws ResourceNotFoundException
     */
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

    /**
     * this method is  to  delete employee department
     * @param department
     * @return
     * @throws ResourceNotFoundException
     */
    public ResponseEntity<Object> deleteEmployeeDepartment(String department) throws ResourceNotFoundException
    {
        EmployeeDepartment employeeDepartment = null;
        List<EmployeeDepartment> departmentList=employeeDepartmentDao.findAll();
        for(EmployeeDepartment Department:departmentList)
        {
            if(Department.getDepartment().equalsIgnoreCase(department))
            {
                employeeDepartment=Department;
            }
        }

        if(employeeDepartment==null)
        {
            throw new ResourceNotFoundException(NO_RECORDS);
        }
        employeeDepartmentDao.deleteDepartment(department);
        logger.info("department deleted success");
        return   new ResponseEntity<>(
                new Response<>(String.valueOf(HttpStatus.OK.value()), HttpStatus.OK, "department deleted successful"),
                new HttpHeaders(),
                HttpStatus.OK);
    }

    /**
     * method to delete employee from the database
     * @param empID
     * @return
     * @throws ResourceNotFoundException
     */
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

    /**
     * method to save employee department
     *
     * @param employeeDepartment
     * @return
     * @throws FieldEmptyException
     * @throws AlreadyExistsException
     */
    public ResponseEntity<Object> saveEmpDepartment(EmployeeDepartmentRequest employeeDepartment) throws FieldEmptyException, AlreadyExistsException {

        String empDepartment = employeeDepartment.getDepartment().toLowerCase();
        if(employeeDepartment.getDepartment().isEmpty())
        {
            throw new FieldEmptyException("field should not empty");
        }

        if(departmentExists(empDepartment))
        {
            throw new AlreadyExistsException("Department Name  Already Exists: "+employeeDepartment.getDepartment());
        }

        saveHistory(employeeDepartment,EMP_DEPARTMENT_ADD);
        employeeDepartment.setDepartment(empDepartment);

        logger.info("department saved success");
        EmployeeDepartment department= new EmployeeDepartment(employeeDepartment.getDepartment());
        employeeDepartmentDao.save(department);
        return new ResponseEntity<>(
                new Response<>(String.valueOf(HttpStatus.CREATED.value()), HttpStatus.CREATED, "successfully saved",employeeDepartment),
                new HttpHeaders(),
                HttpStatus.CREATED);
    }


    /**
     * ->validation part
     * email validation
     * emptyField validation
     * email exists validation
     * employee id exists validation
     * department and role availability check
     * device availability check
     */



    /**
     * To check if the given email valid or not
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
     * validation on employee mandatory filed
     * employee email validation
     * employee password
     * employee email exists
     * employee id exists or not
     * @param employeeDetails
     * @throws FieldEmptyException
     */

    public void emptyFieldAndIncorrectEmailAndPasswordLengthCheck(String employeeId, EmployeeRequest employeeDetails) throws FieldEmptyException {

        if (employeeDetails.getEmpPassword().isEmpty() || employeeDetails.getEmpDepartment().isEmpty() ||
                employeeDetails.getAssignRole().isEmpty() || employeeId.isEmpty() || employeeDetails.getEmpName().isEmpty()) {
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



    /**
     * to check  email already exits or not
     **/
    private boolean emailExists(final String email)
    {
        return employeeDao.findByEmail(email)!=null;
    }

    public void employeeIdExistsValidation(String employeeID)
    {
        if (employeeIdExists(employeeID))
        {
            throw new GeneralException(EMP_ID_EXISTS + employeeID);
        }
    }
    /**
     * to check employee id  already exits or not
     * */

    private boolean employeeIdExists(final String empId)
    {
        List<Employee> employees= (List<Employee>) employeeDao.findAll();
        for(Employee employee:employees)
        {
            if(employee.getEmpId().equals(empId))
            {
                return true;
            }
        }
        return false;
    }



    public boolean departmentExists(final String department)
    {
        return employeeDepartmentDao.findByDepartmentName(department)!=null;
    }

    public  boolean roleExistsCheck(String role)
    {
        return  roleDao.findByName(role)!=null;
    }


    /**
     * to check given department  is available
     * to check given role  is available
     */
    public  void departmentAndRoleAvailabilityCheck(EmployeeRequest employeeDetails) throws ResourceNotFoundException {
        List<EmployeeDepartment> employeeList=  employeeDepartmentDao.findAll();

        int flag=0;
        for(EmployeeDepartment department:employeeList)
        {

            if(department!=null||department.getDepartment()!=null)
            {
                if((department.getDepartment().equals(employeeDetails.getEmpDepartment())))
                {
                    flag=1;
                    break;
                }
            }

        }
        if(flag==0)
        {
            throw new ResourceNotFoundException("department unavailable");
        }


        List<Role> roles= (List<Role>) roleDao.findAll();
        for(Role role:roles)
        {
            flag=0;
            if(role.getRoleName()!=null&&role.getRoleName()!=null)
            {
                if(role.getRoleName().equals(employeeDetails.getAssignRole()))
                {
                    flag=1;
                    break;
                }
            }

        }
        if(flag==0)
        {
            throw new ResourceNotFoundException("role unavailable");
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
     * to check given device is available or not
     * @param employeeDetails
     * @throws ResourceNotFoundException
     */
    public void availableDeviceCheck(EmployeeRequest employeeDetails) throws ResourceNotFoundException {
        List<Device> availableDevices=deviceDao.availableDevice();
        List<Integer> newDevice = new ArrayList<>();
        if(!Objects.equals(employeeDetails.getEmpDevices(), ""))
        {
            newDevice=getDeviceID(employeeDetails.getEmpDevices());
            if(availableDevices.isEmpty())
            {
                throw new ResourceNotFoundException("device unavailable ");
            }

        }
        if(!newDevice.isEmpty())
        {
            for(Integer id:newDevice)
            {
                for(Device device:availableDevices)
                {
                    if(!device.getDeviceId().equals(id))
                    {
                        throw new ResourceNotFoundException("device unavailable: "+id);
                    }
                }
            }

            for (Integer id : newDevice) {
                for (Device device : availableDevices) {
                    if (!device.getDeviceId().equals(id)) {
                        throw new ResourceNotFoundException("device unavailable: " + id);
                    }
                }
            }
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
                if (!"".equals(temp))
                {
                    deviceId.add(Integer.parseInt(temp));
                }
            }
        }
        return deviceId;
    }
}