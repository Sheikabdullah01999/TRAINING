package com.grootan.assetManagement.Service;

import com.google.gson.Gson;
import com.grootan.assetManagement.Model.*;
import com.grootan.assetManagement.Repository.*;
import com.grootan.assetManagement.Response;
import com.grootan.assetManagement.request.DeviceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
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
    public ResponseEntity addDeviceDetails(DeviceRequest deviceRequest) {
        if(deviceRequest.getCategory().isEmpty()||deviceRequest.getDeviceName().isEmpty()||
                deviceRequest.getManufacturedId().isEmpty()||deviceRequest.getAssignStatus().isEmpty()||
                deviceRequest.getDevicePurchaseDate()==null||deviceRequest.getDeviceStatus().isEmpty())

        {
            return new ResponseEntity<>(new Response<>(String.valueOf(HttpStatus.NOT_ACCEPTABLE),
                    HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),"field should not empty"),
                    new HttpHeaders(),HttpStatus.NOT_ACCEPTABLE);
        }
        if(deviceIdExists(deviceRequest.getManufacturedId()))
        {
            return new ResponseEntity<>(new Response<>(String.valueOf(HttpStatus.NOT_ACCEPTABLE),
                    HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),"manufactured id already exits"),
                    new HttpHeaders(),HttpStatus.NOT_ACCEPTABLE);
        }



//        String deviceHistory=NEW_DEVICE_NAME+device.getDeviceName()+","
//                +NEW_DEVICE_MANUFACTURE_ID+device.getManufacturedId();
        saveHistory(deviceRequest,DEVICE_ADD);
        Device device=new Device(deviceRequest.getManufacturedId(), deviceRequest.getCategory(),
                deviceRequest.getDeviceName(),deviceRequest.getDevicePurchaseDate(),
                deviceRequest.getAssignStatus(),deviceRequest.getDeviceStatus());
        deviceDao.save(device);
        return new ResponseEntity(
                new Response<>(String.valueOf(HttpStatus.CREATED.value()), HttpStatus.CREATED.getReasonPhrase(), "successfully saved",deviceRequest),
                new HttpHeaders(),
                HttpStatus.CREATED);
    }

    //update device details
    public ResponseEntity updateDeviceDetails(DeviceRequest deviceRequest) {
//        String deviceHistory=NEW_DEVICE_NAME+device.getDeviceName()+","
//                +NEW_DEVICE_MANUFACTURE_ID+device.getManufacturedId();
        if(deviceRequest.getCategory()==""||deviceRequest.getDeviceName()==""||
                deviceRequest.getManufacturedId()==""||deviceRequest.getAssignStatus()==""||
                deviceRequest.getDevicePurchaseDate()==null||deviceRequest.getDeviceStatus()==""||
                deviceIdExists(deviceRequest.getManufacturedId()))
        {
            return new ResponseEntity<>(new Response<>(String.valueOf(HttpStatus.NOT_ACCEPTABLE),
                    HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),"field should not empty"),
                    new HttpHeaders(),HttpStatus.NOT_ACCEPTABLE);
        }
        saveHistory(deviceRequest,DEVICE_UPDATED);
        Device device=new Device(deviceRequest.getManufacturedId(), deviceRequest.getCategory(),
                deviceRequest.getDeviceName(),deviceRequest.getDevicePurchaseDate(),
                deviceRequest.getAssignStatus(),deviceRequest.getDeviceStatus());

        deviceDao.save(device);
        return new ResponseEntity(
                new Response<>(String.valueOf(HttpStatus.OK.value()), HttpStatus.OK.getReasonPhrase(), "Updated saved",deviceRequest),
                new HttpHeaders(),
                HttpStatus.OK);
    }


    //get all devices
    public ResponseEntity getAllDevices()
    {
        List<Device> deviceList=deviceDao.findAll();
        if (deviceList.isEmpty())
        {
            return new ResponseEntity(
                    new Response<>(String.valueOf(HttpStatus.NOT_FOUND.value()),
                            HttpStatus.NOT_FOUND.getReasonPhrase(), "device not found"),
                    new HttpHeaders(),
                    HttpStatus.NOT_FOUND
            );
        }

        return   new ResponseEntity(
                new Response<>(String.valueOf(HttpStatus.FOUND.value()), HttpStatus.FOUND.getReasonPhrase(), "device found", deviceList),
                new HttpHeaders(),
                HttpStatus.FOUND);
    }


    //get device by device id
    public ResponseEntity findDeviceById(int id)
    {
        Optional<Device> device = deviceDao.findById(id);
        return device.map(value -> new ResponseEntity(
                new Response<>(String.valueOf(HttpStatus.OK.value()), HttpStatus.OK.getReasonPhrase(), "device found", value),
                new HttpHeaders(),
                HttpStatus.OK)).orElseGet(() -> new ResponseEntity(
                new Response<>(String.valueOf(HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND.getReasonPhrase(), "device not found"),
                new HttpHeaders(),
                HttpStatus.NOT_FOUND
        ));
    }

    //search by keyword using i like
    public List<Device> getByKeywordDevice(String keyword)
    {
        return deviceDao.findByKeyword(keyword);
    }

    //save device category
    public ResponseEntity saveDeviceCategory(DeviceCategory category) {
        String lowerCase = category.getCategory().toLowerCase();
        if(category.getCategory().isEmpty())
        {
            return new ResponseEntity<>(new Response<>(String.valueOf(HttpStatus.NOT_ACCEPTABLE),
                    HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),"field should not empty"),
                    new HttpHeaders(),HttpStatus.NOT_ACCEPTABLE);
        }

        if(deviceCategoryExists(lowerCase))
        {
            return new ResponseEntity<>(new Response<>(String.valueOf(HttpStatus.CONFLICT),
                    HttpStatus.CONFLICT.getReasonPhrase(),"This category is already exists: "+category.getCategory()),
                    new HttpHeaders(),HttpStatus.CONFLICT);
        }

        History history=new History(service.currentUser(),DEVICE_CATEGORY_ADD,new Gson().toJson(category),service.DateAndTime());
        historyDao.save(history);
        category.setCategory(lowerCase);
        deviceCategoryDao.save(category);
        return new ResponseEntity(
                new Response<>(String.valueOf(HttpStatus.CREATED.value()), HttpStatus.CREATED.getReasonPhrase(), "successfully saved",category),
                new HttpHeaders(),
                HttpStatus.CREATED);
    }

    //get device category
    public ResponseEntity getCategory() {

        List<DeviceCategory> deviceCategoryList=deviceCategoryDao.findAll();
        if(deviceCategoryList.isEmpty())
        {
            return new ResponseEntity(
                    new Response<>(String.valueOf(HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND.getReasonPhrase(), "no device found"),
                    new HttpHeaders(),
                    HttpStatus.NOT_FOUND
            );

        }
        return   new ResponseEntity(
                new Response<>(String.valueOf(HttpStatus.OK.value()), HttpStatus.OK.getReasonPhrase(), "device found", deviceCategoryList),
                new HttpHeaders(),
                HttpStatus.OK);
    }

    //save device name
    public ResponseEntity saveDeviceName(DeviceName deviceName)
    {
        DeviceCategory deviceCategory = deviceName.getDeviceCategory();
        String lowerCase = deviceName.getName().toLowerCase();
        if(deviceName.getName()==""||deviceCategory.getCategory()=="")
        {
            return new ResponseEntity<>(new Response<>(String.valueOf(HttpStatus.NOT_ACCEPTABLE),
                    HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),"field should not empty"),
                    new HttpHeaders(),HttpStatus.NOT_ACCEPTABLE);
        }
        if(deviceNameExists(lowerCase))
        {
            return new ResponseEntity<>(new Response<>(String.valueOf(HttpStatus.CONFLICT),
                    HttpStatus.CONFLICT.getReasonPhrase(),"This device Name is Already Exists: "+deviceName.getName()),
                    new HttpHeaders(),HttpStatus.CONFLICT);
        }


        saveHistory(deviceName,DEVICE_NAME_ADD);
        deviceName.setName(lowerCase);
        deviceNameDao.save(deviceName);
        return new ResponseEntity(
                new Response<>(String.valueOf(HttpStatus.CREATED.value()), HttpStatus.CREATED.getReasonPhrase(), "successfully saved",deviceName),
                new HttpHeaders(),
                HttpStatus.CREATED);
    }

    public void saveHistory(Object o,String constant)
    {
        String userName=service.currentUser();
        History saveHistory=new History(userName,constant,new Gson().toJson(o),service.DateAndTime());
        historyDao.save(saveHistory);

    }

    //get device name
    public ResponseEntity getDeviceName()
    {
        List<DeviceName> list=deviceNameDao.findAll();
        if(list.isEmpty())
        {
            new ResponseEntity(
                    new Response<>(String.valueOf(HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND.getReasonPhrase(), "no device name found"),
                    new HttpHeaders(),
                    HttpStatus.NOT_FOUND
            );

        }
        return   new ResponseEntity(
                new Response<>(String.valueOf(HttpStatus.FOUND.value()), HttpStatus.FOUND.getReasonPhrase(), "device name found", list),
                new HttpHeaders(),
                HttpStatus.OK);
    }

    public ResponseEntity getHistory(){
        List<History> list=historyDao.findAll();
        if(list.isEmpty())
        {
            new ResponseEntity(
                    new Response<>(String.valueOf(HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND.getReasonPhrase(), "history found"),
                    new HttpHeaders(),
                    HttpStatus.NOT_FOUND
            );

        }
        return   new ResponseEntity(
                new Response<>(String.valueOf(HttpStatus.FOUND.value()), HttpStatus.FOUND.getReasonPhrase(), "history  found", list),
                new HttpHeaders(),
                HttpStatus.FOUND);

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

    public ResponseEntity deleteDeviceCategory(String id) {
        DeviceCategory deviceCategory=deviceCategoryDao.findByDeviceCategory(id);
        if(deviceCategory==null)
        {
            return new ResponseEntity(
                    new Response<>(String.valueOf(HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND.getReasonPhrase(), "no device category found"),
                    new HttpHeaders(),
                    HttpStatus.NOT_FOUND
            );
        }
        deviceCategoryDao.deleteById(id);
        return   new ResponseEntity(
                new Response<>(String.valueOf(HttpStatus.OK.value()), HttpStatus.OK.getReasonPhrase(), "deleted successful"),
                new HttpHeaders(),
                HttpStatus.OK);
    }

    public ResponseEntity deleteDeviceDetails(Integer id) {
        Device device=deviceDao.getByDeviceId(id);
        if(device==null)
        {
            return new ResponseEntity(
                    new Response<>(String.valueOf(HttpStatus.NOT_FOUND.value()),
                            HttpStatus.NOT_FOUND.getReasonPhrase(), "device not found"),
                    new HttpHeaders(), HttpStatus.NOT_FOUND
            );

        }
        //    saveHistory(device,DEVICE_DELETE);
        deviceDao.deleteForiegnKey(id);
        deviceDao.deleteById(id);

        return new ResponseEntity(
                new Response<>(String.valueOf(HttpStatus.OK.value()),
                        HttpStatus.OK.getReasonPhrase(), "deleted successful"),
                new HttpHeaders(), HttpStatus.OK);
    }

    public List<String> findByCategory(long id) {
        return deviceNameDao.getDeviceNames(id);
    }

    public int findByCategoryId(String name){
        return deviceNameDao.getDeviceCategoryId(name);
    }

    public Object findAll() {
        return deviceCategoryDao.findAll();
    }
}