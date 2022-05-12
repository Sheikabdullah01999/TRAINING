package com.grootan.assetManagement.Service;

import com.grootan.assetManagement.Exception.GeneralException;
import com.grootan.assetManagement.Model.*;
import com.grootan.assetManagement.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

import static com.grootan.assetManagement.Model.Constants.*;

@Service
public class AdminService {
    String UserName;
    @Autowired
    private AdminDao adminDao;

    @Autowired
    private Employee employee;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private DeviceCategoryDao deviceCategoryDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    HistoryDao historyDao;

    @Autowired
    private DeviceNameDao deviceNameDao;

    @Autowired
    private EmployeeDepartmentDao employeeDepartmentDao;

    public Authentication getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        return authentication;
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

        Employee adminUser = new Employee();
        adminUser.setEmpId("G001");
        adminUser.setEmpName("grootan");
        adminUser.setEmail("grootan@gmail.com");
        adminUser.setEmpPassword(getEncodedPassword("gr00tan"));
        adminUser.setEmpDepartment("admin");
        adminUser.setEmpDevices("");
        adminUser.setAssignRole(adminRole.getRoleName());
        Collection<Role> adminRoles = new ArrayList<>();
        adminRoles.add(adminRole);
        adminUser.setRole(adminRoles);
        adminDao.save(adminUser);
    }

    //password encoder
    public String getEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }

    //to check  email already exits or not
    private boolean emailExists(final String email)
    {
        return employeeDao.findByEmail(email)!=null;
    }

    //to check role name exists or not
    private boolean roleExists(final String roleName){
        return roleDao.findByName(roleName)!=null;
    }

    //to check employee id  already exits or not
    private boolean employeeIdExists(final String empId)
    {
        return employeeDao.findByEmpId(empId)!=null;
    }

    //to check device Id  already exits or not
    private boolean deviceIdExists(final String id)
    {
        return deviceDao.findByDeviceId(id)!=null;
    }

    //to check device name exits or not
    private boolean deviceNameExists(final String name)
    {
        return deviceNameDao.findByDeviceName(name)!=null;
    }

    //to check employee devices exists or not
    private boolean employeeDevicesExists(final String empDevices)
    {
        return employeeDao.findByEmpDevices(empDevices)!=null;
    }

    //to check device category already exits or  not
    private boolean deviceCategoryExists(final String category)
    {
        return deviceCategoryDao.findByDeviceCategory(category)!=null;
    }

    //to check department name already exits or not
    private boolean departmentExists(final String department)
    {
        return employeeDepartmentDao.findByDepartmentName(department)!=null;
    }

    // check given email id is valid or not
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
    public String currentUser()
    {
        Authentication authentication=getCurrentUser();
       return  authentication.getName();
    }

    public Boolean validate(Employee employeeDetails)
    {
        Boolean validEmail=isValid(employeeDetails.getEmail());
        if(!validEmail)
        {
            throw new GeneralException(INCORRECT_EMAIL+employeeDetails.getEmail());
        }

        if(emailExists(employeeDetails.getEmail()))
        {
            throw new GeneralException(EMP_EMAIL_EXISTS+employeeDetails.getEmail());
        }

        if(employeeIdExists(employeeDetails.getEmpId()))
        {
            throw new GeneralException(EMP_ID_EXISTS+employeeDetails.getEmpId());
        }

        if(employeeDevicesExists(employeeDetails.getEmpDevices()))
        {
            throw new GeneralException(DEVICE_ASSIGNED+employeeDetails.getEmpDevices());
        }
        return true;
    }

    public Employee saveEmpDetails(Employee employeeDetails)
    {
        String employeeDevices="";
        String devices= employeeDetails.getEmpDevices();

        List<Device> device=new ArrayList<>();

        if(employeeDetails.getEmpDevices()==null)
        {
            employeeDevices="";
            device=new ArrayList<>();
        }
        else
        {
            List<Integer> id=getDeviceID(devices);
            String deviceList=empDevice(employeeDetails);
            employeeDevices=deviceList;
            for(int i=0;i<id.size();i++)
            {
                deviceDao.updateAssignStatus(id.get(i));
            }

            for(Integer deviceId : id)
            {
                device.add(new Device(deviceId));
            }

        }

        Employee employee = new Employee(employeeDetails.getEmpId(),
                employeeDetails.getEmpName(), employeeDetails.getEmail(),
                passwordEncoder.encode(employeeDetails.getEmpPassword()),
                employeeDetails.getEmpDepartment(),employeeDevices,
                employeeDetails.getAssignRole(),
                Arrays.asList(new Role(employeeDetails.getAssignRole())),device);

        return employee;
    }


    public String empDevice(Employee employee)
    {
        String devices=employee.getEmpDevices();
        List<Integer> list=getDeviceID(devices);
        List<String> deviceList =new ArrayList<>();
        for(int i=0;i<list.size();i++)
        {
            deviceList.add(deviceDao.getDeviceById(list.get(i)));
        }
        String updatedDevice="";
        for (String device:deviceList) {
            if (updatedDevice == "") {
                updatedDevice = updatedDevice + device;
                updatedDevice = device;
            } else {
                updatedDevice = updatedDevice + ";" + device;
            }

        }
        return updatedDevice;
    }

    //save employee details after check all details are  correct
    public Employee saveEmployee(Employee employeeDetails) {
        Employee employee=saveEmpDetails(employeeDetails);
        if(employeeDetails.getAssignRole().equals("ADMIN"))
        {
            String history = NEW_EMP_ID + employeeDetails.getEmpId() + ","
                    + EMP_NAME + employeeDetails.getEmpName();
            History newHistory = new History(currentUser(), ADMIN_ADD, history, DateAndTime());
            historyDao.save(newHistory);
            return employeeDao.save(employee);
        }
        else {
            String history = NEW_EMP_ID + employeeDetails.getEmpId() + ","
                    + EMP_NAME+ employeeDetails.getEmpName();
            History newHistory = new History(currentUser(), USER_ADD, history, DateAndTime());
            historyDao.save(newHistory);
            return employeeDao.save(employee);
        }
    }

    public Role saveRoles(Role role) {
        if(roleExists(role.getRoleName()))
        {
            throw new GeneralException(R0LE_EXISTS+role.getRoleName());
        }
        String roleHistory=NEW_ROLE+role.getRoleName();
        History history=new History(currentUser(),ROLE_ADD,roleHistory,DateAndTime());
        historyDao.save(history);
        return roleDao.save(role);
    }

    //list all employees
    public List<Employee> getAllEmployees()
    {
        List<Employee> employeeList = employeeDao.findAll();
        if(employeeList.isEmpty())
        {
            throw new GeneralException("RECORD_NOT_FOUND");
        }
        return employeeList;
    }

    //get all roles
    public List<Role> getAllRoles()
    {
        List<Role> roles = (List<Role>) roleDao.findAll();
        if(roles.isEmpty())
        {
            throw new GeneralException("NO_RECORDS_FOUND");
        }
        return roles;
    }

    //get device from database
    public List<Device> getDevice(String device)
    {
        List<Device> list=new ArrayList<>();
        list= deviceDao.getDeviceByCategory(device);
        return list;

    }

    //Add device details
    public void addDeviceDetails(Device device) {

      if(deviceIdExists(device.getManufacturedId()))
      {
          throw new GeneralException(PRODUCT_ID_EXISTS+device.getManufacturedId());
      }
        String deviceHistory=NEW_DEVICE_NAME+device.getDeviceName()+","
                +NEW_DEVICE_MANUFACTURE_ID+device.getManufacturedId();
        History history=new History(currentUser(),DEVICE_ADD,deviceHistory,DateAndTime());
        historyDao.save(history);
        deviceDao.save(device);
    }

    //update device details
    public void updateDeviceDetails(Device device) {
        String deviceHistory=NEW_DEVICE_NAME+device.getDeviceName()+","
                +NEW_DEVICE_MANUFACTURE_ID+device.getManufacturedId();
        History history=new History(currentUser(),DEVICE_UPDATED,deviceHistory,DateAndTime());
        historyDao.save(history);
        deviceDao.save(device);
    }

    //update device category
    public void updateDeviceCategory(DeviceCategory deviceCategory) {
        System.out.println(deviceCategory.getCategory());
        String deviceHistory=DEVICE_CATEGORY+deviceCategory.getCategory();
        History history=new History(currentUser(),DEVICE_UPDATED,deviceHistory,DateAndTime());
        historyDao.save(history);
        deviceCategoryDao.save(deviceCategory);
    }

    //get all devices
    public List<Device> getAllDevices()
    {
        return deviceDao.findAll();
    }
    //get all category
    public List<String> getAllCategory()
    {
        return (List<String>) deviceDao.getDevice();
    }

    //get All device by device name
    public List<String> getAllDevicesByName()
    {
        return (List<String>) deviceDao.getDeviceByName();
    }

    //get all device by employeeId
    public String getAllDevicesById(String EmpId)
    {
        return employeeDao.getEmpDevices(EmpId);
    }

    // list all user with devices
    public List<EmployeeDevices> getUserDevices()
    {
        List<EmployeeDevices> userDevices = employeeDao.getUserDevice();
        return userDevices;
    }

    //get all damaged device
    public List<Device> getDamagedDevice(String device, String status) {
        List<Device> list=new ArrayList<>();
        list= deviceDao.getDamagedDevice(device,status);
        return list;
    }

    //delete device details
    public void deleteDeviceDetails(Integer id) {
        Device device=deviceDao.findById(id).orElseThrow(()-> new GeneralException("no record found"));
        String empDevices = device.getId()+","+device.getDeviceName()+","+device.getCategory();
        String empId = deviceDao.getEmpId(id);
        String empDevicesById = employeeDao.getEmpDevices(empId);

        if(empDevicesById!=null)
        {
            String [] empDevicesByIds = empDevicesById.split(";");

            String newDevice="";
            for(int i=0;i<empDevicesByIds.length;i++)
            {
                if(empDevices.equals(empDevicesByIds[i]))
                {
                    newDevice=newDevice+"";
                }
                else
                {
                    newDevice=newDevice+empDevicesByIds[i];
                }
            }
            employeeDao.updateEmployeeByEmpDevice(empId,newDevice);
            String deviceDeleteHistory=DEVICE_ID+device.getId()+","
                    +"device name: "+device.getDeviceName();
            History history=new History(currentUser(),DEVICE_DELETE,deviceDeleteHistory,DateAndTime());


            deviceDao.deleteForiegnKey(id);
            deviceDao.deleteById(id);
            historyDao.save(history);
        }
        else
        {
            String deviceDeleteHistory="device id:"+device.getId()+","
                    +"device name: "+device.getDeviceName();

            String userName=currentUser();
            History history=new History(userName,DEVICE_DELETE,deviceDeleteHistory,DateAndTime());
            historyDao.save(history);
            deviceDao.deleteForiegnKey(id);
            deviceDao.deleteById(id);
        }
    }

    //delete empdevices by employeeId
    public void deleteEmpDevices(String empId)
    {
        String empDevices = employeeDao.getEmpDevices(empId);
        int employeeDevices = employeeDao.deleteByEmpDevicesId(empId);
        String devices = deviceDao.getDeviceById(employeeDevices);
        String [] empDevicesById = empDevices.split(";");
        String newDevice="";
        for(int i=0;i<empDevicesById.length;i++)
        {
            if(devices.equals(empDevicesById[i]))
            {
                newDevice=newDevice+"";
            }
            else
            {
                newDevice=newDevice+empDevicesById[i];
            }
        }
        employeeDao.updateEmployeeByEmpDevice(empId,newDevice);
        deviceDao.updateAssignStatusAndDeviceStatus(employeeDevices);
        employeeDao.deleteEmployeeByEmpDevice(empId);
    }

    //get device by device id
    public Optional<Device> findDeviceById(int id)
    {
        Optional<Device> device = deviceDao.findById(id);
        if(device.isEmpty())
        {
            throw new GeneralException("No Records Found");
        }
        return device;
    }

    //find employee by employee id
    public Employee findEmployeeById(String id)
    {
        Employee employee = employeeDao.findByEmpId(id);
        if(employee.getEmpId()==null)
        {
            throw new GeneralException("No Records Found");
        }
        return employee;
    }

    //updated device
    public String updateEmployeeDevice(Employee employeeDetails)
    {
        String newDevice=employeeDetails.getEmpDevices();
        String updatedDevice = "";
        String deviceList=deviceDao.getDevice(employeeDetails.getEmpId());

        if(employeeDetails.getEmpDevices()!=null)
        {
            updatedDevice=newDevice+";"+deviceList;
        }
        else
        {
            updatedDevice=updatedDevice+deviceList;
        }

        return updatedDevice;

    }
    //updated device size
    public void updateAssignStatus(String updatedDevice)
    {
        List<Integer> deviceId=getDeviceID(updatedDevice);
        for(int i=0;i<deviceId.size();i++)
        {
            deviceDao.updateAssignStatus(deviceId.get(i));
        }
    }

    //update employee details
    public Employee updateEmployee(Employee employeeDetails)
    {
        List<Integer> newDeviceSize = null;
        String newDevice=employeeDetails.getEmpDevices();
        if(newDevice!=null)
        {
            newDeviceSize=getDeviceID(newDevice);
        }

        String updatedDevice = updateEmployeeDevice(employeeDetails);

        List<Integer> deviceId=getDeviceID(updatedDevice);
        List<Device> device=new ArrayList<>();

        for(Integer Id : deviceId)
        {
            device.add(new Device(Id));
        }


        Employee employee = new Employee(employeeDetails.getEmpId(),
                employeeDetails.getEmpName(), employeeDetails.getEmail(),
                employeeDetails.getEmpPassword(), employeeDetails.getEmpDepartment(), updatedDevice,
                employeeDetails.getAssignRole(),
                Arrays.asList(new Role(employeeDetails.getAssignRole())),device);

        updateAssignStatus(updatedDevice);

        String updatedEmployeeHistory=updatedEmployeeHistory(employeeDetails,newDeviceSize);
        String userName=currentUser();
        History history=new History(userName,USER_UPDATED,updatedEmployeeHistory,DateAndTime());

        historyDao.save(history);
        return employeeDao.save(employee);
    }

    //update employee history
    public String updatedEmployeeHistory(Employee employeeDetails,List<Integer> newDeviceSize)
    {
        String employeeId=employeeDetails.getEmpId();
        Employee emp=employeeDao.findByEmpId(employeeId);
        String updatedEmployeeHistory="EMP ID: "+employeeDetails.getEmpId();

        if(employeeDetails.getEmpDevices()!=null&&emp.getEmpDevices()!=null)
        {
            if(!emp.getEmpDevices().equals(employeeDetails.getEmpDevices())&&(newDeviceSize!=null))
            {
                int size=newDeviceSize.size();
                updatedEmployeeHistory=updatedEmployeeHistory+size+",new device added ";
            }
        }
        if(!emp.getEmpName().equalsIgnoreCase(employeeDetails.getEmpName()))
        {
            updatedEmployeeHistory=updatedEmployeeHistory+" ,Employee name changed from "
                    +emp.getEmpName()+" to "
                    +employeeDetails.getEmpName();
        }
        if(!emp.getEmpDepartment().equalsIgnoreCase(employeeDetails.getEmpDepartment()))
        {
            updatedEmployeeHistory=updatedEmployeeHistory+",Employee department changed from "
                    + emp.getEmpDepartment()+" to"+employeeDetails.getEmpDepartment();
        }
        if(!emp.getAssignRole().equalsIgnoreCase(employeeDetails.getAssignRole()))
        {
            updatedEmployeeHistory=updatedEmployeeHistory+",Employee Role changed from "
                    +  emp.getAssignRole()+" to "+employeeDetails.getAssignRole();
        }
        if(!emp.getEmail().equalsIgnoreCase(employeeDetails.getEmail()))
        {
            updatedEmployeeHistory=updatedEmployeeHistory+",Employee Email changed from "
                    +emp.getEmail()+" to "+employeeDetails.getEmail();
        }

        return updatedEmployeeHistory;

    }

    //current log in user detail validation
    public void currentLoggedInUserValidation(String  id)
    {
        String currentUser=employeeDao.getEmployeeMail(id);
        if(currentUser.equals(currentUser()))
        {
            throw new GeneralException("cannot delete current logged in user");
        }
        else if(id.equals("G001"))
        {
            throw new GeneralException("cannot delete default admin account ");
        }
    }

    //delete employee details
    public void deleteEmpDetails(String id)
    {
        currentLoggedInUserValidation(id);

        Employee employee = employeeDao.findByEmpId(id);
        String s=employee.getEmpDevices();

        List<Integer> deviceId=getDeviceID(s);

        if(employee.getEmpId()==null)
        {
            throw new GeneralException("No Records Found");
        }

        employeeDao.deleteByEmpId(id);

        for(Integer dId:deviceId)
        {
            deviceDao.updateAssignStatusAndDeviceStatus(dId);
        }
        String newDeletedDeviceHistory="employee Id "+id+"Records deleted ";
        String userName=currentUser();
        History history=new History(userName,USER_DELETE,newDeletedDeviceHistory,DateAndTime());
        historyDao.save(history);

    }


    //get employee by employe id
    public Employee getEmployeeById()
    {
       Employee employee = employeeDao.findByEmail(UserName);
       return employee;
    }

    //search by keyword using like
    public List<Employee> getByKeyword(String keyword){
        return employeeDao.findByKeyword(keyword);
    }

    //search by keyword using ilike
    public List<Device> getByKeywordDevice(String keyword)
    {
        return deviceDao.findByKeyword(keyword);
    }

    //save device category
    public void saveDeviceCategory(DeviceCategory category) {
        String lower = category.getCategory();
        String i = lower.toLowerCase();
      if(deviceCategoryExists(i))
      {
          throw new GeneralException("This category is already exists: "+category.getCategory());
      }
        String newDeviceHistory="New Device category: "+category.getCategory();
        LocalDateTime now=LocalDateTime.now();

        String userName=currentUser();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
        String dateString = dateFormat.format(new Date()).toString();
        History history=new History(userName,DEVICE_CATEGORY_ADD,newDeviceHistory,dateString);
        historyDao.save(history);
        category.setCategory(i);
       deviceCategoryDao.save(category);
    }

    //get device category
    public List<DeviceCategory> getCategory() {

      List<DeviceCategory> list=deviceCategoryDao.findAll();
      return list;
    }

    //save device name
    public void saveDeviceName(DeviceName deviceName)
    {
        DeviceCategory deviceCategory = deviceName.getDeviceCategory();
        String lower = deviceName.getName();
        String i = lower.toLowerCase();
        if(deviceNameExists(i))
        {
            throw new GeneralException("This Product Name is Already Exists: "+deviceName.getName());
        }
        String newDeviceHistory="New Device  Name: "+deviceName.getName();
        String userName=currentUser();
        History history=new History(userName,DEVICE_NAME_ADD,newDeviceHistory,DateAndTime());
        historyDao.save(history);
        deviceName.setName(i);
        deviceNameDao.save(deviceName);
    }

    //get device name
    public List<DeviceName> getName()
    {
        List<DeviceName> list=deviceNameDao.findAll();
        return list;
    }

    //get device by device id
    public List<Integer> getDeviceID(String device) {
        List<String> list = null;
        if (device != null) {

            list = Arrays.asList(device.split(","));
        }

        List<Integer> deviceId = new ArrayList<>();
        if(!list.isEmpty())
        {
            for (String empId : list) {

                String temp = "";
                for (int i = 0; i < empId.length(); i++) {
                    if (Character.isDigit(empId.charAt(i))) {
                        temp = temp + String.valueOf(empId.charAt(i));
                    }
                }
                if (temp != "") {
                    deviceId.add(Integer.parseInt(temp));
                }
            }
        }

        return deviceId;
    }


    public List<History> getHistory(){
        List<History> historyList=historyDao.findAll();
        if(historyList.isEmpty())
        {
            throw new GeneralException("RECORD_NOT_FOUND");
        }
        return historyList;
    }

    //save employee department
    public void saveEmpDepartment(EmployeeDepartment employeeDepartment) {
        String lower = employeeDepartment.getDepartment();
        String i = lower.toLowerCase();
        if(departmentExists(i))
        {
            throw new GeneralException("Department Name  Already Exists: "+employeeDepartment.getDepartment());
        }
        String newDeviceHistory="New Department Name: "+employeeDepartment.getDepartment();
        String userName=currentUser();
        History history=new History(userName,EMP_DEPARTMENT_ADD,newDeviceHistory,DateAndTime());
        historyDao.save(history);
        employeeDepartment.setDepartment(i);
        employeeDepartmentDao.save(employeeDepartment);
    }

    //get all departments from the table
    public List<EmployeeDepartment> getAllEmpDepartments() {
        List<EmployeeDepartment> list=employeeDepartmentDao.findAll();
        return list;
    }

    public Iterable<DeviceCategory> findAll() {
        return deviceCategoryDao.findAll();
    }

    public List<String> findByCategory(String name) {
        return deviceNameDao.getDeviceNames(name);
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

    //pagination by employee table
    public Page<History> findPaginated(int pageNo, int pageSize)
    {
        Pageable pageable = PageRequest.of(pageNo-1,pageSize);
        return this.historyDao.findAll(pageable);
    }

    public List<DeviceCategory> getAllDeviceCategory()
    {
        return deviceCategoryDao.findAll();
    }

    public void deleteDeviceCategory(String id) {
        deviceCategoryDao.deleteById(id);
    }
}

