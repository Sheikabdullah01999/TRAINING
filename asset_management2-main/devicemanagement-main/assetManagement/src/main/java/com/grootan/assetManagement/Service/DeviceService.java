package com.grootan.assetManagement.Service;

import com.grootan.assetManagement.Exception.GeneralException;
import com.grootan.assetManagement.Model.*;
import com.grootan.assetManagement.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import static com.grootan.assetManagement.Model.Constants.*;

@Service
public class DeviceService {
    String UserName;


    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private DeviceCategoryDao deviceCategoryDao;

    @Autowired
    HistoryDao historyDao;

    @Autowired
    private CommonService service;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DeviceNameDao deviceNameDao;


    //get All device by device name
    public List<String> getAllDevicesByName()
    {
        return (List<String>) deviceDao.getDeviceByName();
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

    //to check device category already exits or  not
    private boolean deviceCategoryExists(final String category)
    {
        return deviceCategoryDao.findByDeviceCategory(category)!=null;
    }


    //Add device details
    public Device addDeviceDetails(Device device) {
      if(device.getCategory()=="")
      {
          throw new GeneralException("Select the device category");
      }

      if(device.getDeviceName()=="")
      {
          throw new GeneralException("Select the deviceName");
      }

      if(device.getManufacturedId()=="")
      {
          throw new GeneralException("Enter the device manufactureId");
      }

      if(device.getAssignStatus()=="")
      {
          throw new GeneralException("Select the device assignStatus");
      }

      if(device.getDevicePurchaseDate()==null)
      {
          throw new GeneralException("Pick the devicePurchaseDate");
      }
      if(device.getDeviceStatus()=="")
      {
          throw new GeneralException("Select the device Status");
      }

      if(deviceIdExists(device.getManufacturedId()))
      {
          throw new GeneralException(PRODUCT_ID_EXISTS+device.getManufacturedId());
      }
        String deviceHistory=NEW_DEVICE_NAME+device.getDeviceName()+","
                +NEW_DEVICE_MANUFACTURE_ID+device.getManufacturedId();
        History history=new History(service.currentUser(),DEVICE_ADD,deviceHistory,service.DateAndTime());
        historyDao.save(history);
        return deviceDao.save(device);
    }

    //update device details
    public void updateDeviceDetails(Device device) {
        String deviceHistory=NEW_DEVICE_NAME+device.getDeviceName()+","
                +NEW_DEVICE_MANUFACTURE_ID+device.getManufacturedId();
        History history=new History(service.currentUser(),DEVICE_UPDATED,deviceHistory,service.DateAndTime());
        historyDao.save(history);
        deviceDao.save(device);
    }

    //update device category
    public void updateDeviceCategory(DeviceCategory deviceCategory) {
        System.out.println(deviceCategory.getCategory());
        String deviceHistory=DEVICE_CATEGORY+deviceCategory.getCategory();
        History history=new History(service.currentUser(),DEVICE_UPDATED,deviceHistory,service.DateAndTime());
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

    //delete device details
    public void deleteDeviceDetails(Integer id) {
        Device device=deviceDao.findById(id).orElseThrow(()-> new GeneralException("no record found"));

        String deviceDeleteHistory=DEVICE_ID+device.getId()+","
                    +"device name: "+device.getDeviceName();
            String userName=service.currentUser();
            History history1=new History(userName,DEVICE_DELETE,deviceDeleteHistory,service.DateAndTime());
            historyDao.save(history1);
            deviceDao.deleteForiegnKey(id);
            deviceDao.deleteById(id);
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

    //search by keyword using ilike
    public List<Device> getByKeywordDevice(String keyword)
    {
        return deviceDao.findByKeyword(keyword);
    }

    //save device category
    public DeviceCategory saveDeviceCategory(DeviceCategory category) {
        String lower = category.getCategory();
        String i = lower.toLowerCase();
      if(category.getCategory()=="")
      {
          throw new GeneralException("Enter the category");
      }

      if(deviceCategoryExists(i))
      {
          throw new GeneralException("This category is already exists: "+category.getCategory());
      }
        String newDeviceHistory="New Device category: "+category.getCategory();
        LocalDateTime now=LocalDateTime.now();

        String userName=service.currentUser();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
        String dateString = dateFormat.format(new Date()).toString();
        History history=new History(userName,DEVICE_CATEGORY_ADD,newDeviceHistory,dateString);
        historyDao.save(history);
        category.setCategory(i);
       return deviceCategoryDao.save(category);
    }

    //get device category
    public List<DeviceCategory> getCategory() {

      List<DeviceCategory> list=deviceCategoryDao.findAll();
      return list;
    }

    //save device name
    public DeviceName saveDeviceName(DeviceName deviceName)
    {
        DeviceCategory deviceCategory = deviceName.getDeviceCategory();
        String lower = deviceName.getName();
        String i = lower.toLowerCase();
        if(deviceName.getName()=="")
        {
            throw new GeneralException("Enter the deviceName");
        }
        if(deviceCategory.getCategory()=="")
        {
            throw new GeneralException("Enter the category");
        }
        if(deviceNameExists(i))
        {
            throw new GeneralException("This device Name is Already Exists: "+deviceName.getName());
        }
        String newDeviceHistory="New Device  Name: "+deviceName.getName();
        String userName=service.currentUser();
        History history=new History(userName,DEVICE_NAME_ADD,newDeviceHistory,service.DateAndTime());
        historyDao.save(history);
        deviceName.setName(i);
        return deviceNameDao.save(deviceName);
    }

    //get device name
    public List<DeviceName> getDeviceName()
    {
        List<DeviceName> list=deviceNameDao.findAll();
        return list;
    }

    public List<History> getHistory(){
        List<History> historyList=historyDao.findAll();
        if(historyList.isEmpty())
        {
            throw new GeneralException("RECORD_NOT_FOUND");
        }
        return historyList;
    }

    public Iterable<DeviceCategory> findAll() {
        return deviceCategoryDao.findAll();
    }

    public List<String> findByCategory(String name) {
        return deviceNameDao.getDeviceNames(name);
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

