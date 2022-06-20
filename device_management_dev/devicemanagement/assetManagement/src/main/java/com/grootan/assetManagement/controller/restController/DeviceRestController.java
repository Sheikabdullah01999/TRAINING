package com.grootan.assetManagement.controller.restController;


import com.grootan.assetManagement.exception.AlreadyExistsException;
import com.grootan.assetManagement.exception.FieldEmptyException;
import com.grootan.assetManagement.exception.ResourceNotFoundException;
import com.grootan.assetManagement.model.Device;
import com.grootan.assetManagement.repository.DeviceCategoryDao;
import com.grootan.assetManagement.repository.DeviceDao;
import com.grootan.assetManagement.repository.HistoryDao;
import com.grootan.assetManagement.response.Response;
import com.grootan.assetManagement.service.DeviceService;
import com.grootan.assetManagement.request.DeviceCategoryRequest;
import com.grootan.assetManagement.request.DeviceNameRequest;
import com.grootan.assetManagement.request.DeviceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.websocket.server.PathParam;
import java.util.List;

@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "201", description = "CREATED",content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "302", description = "FOUND",content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "404", description = "NO_RECORDS",content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "406", description = "NOT_ACCEPTABLE",content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "409", description = "CONFLICT",content = @Content(schema = @Schema(implementation = Response.class)))
})
@RestController
public class DeviceRestController {
    @Autowired
    DeviceService deviceService;

    @Autowired
    HistoryDao historyDao;

    @Autowired
    DeviceCategoryDao deviceCategoryDao;

    @Autowired
    DeviceDao deviceDao;

    @GetMapping("/v1/history")
    @Operation(summary = "GetHistories", description = "Returns a List Of Histories")
    public ResponseEntity<Object> history() throws ResourceNotFoundException {
        return deviceService.getHistory();
    }

    /**
     * rest api to get all the device details
     * @return
     * @throws ResourceNotFoundException
     */
    @GetMapping("/v1/devices")
    @Operation(summary = "GetDevices", description = "Returns a List Of Devices")
    public ResponseEntity<Object> getAllDeviceList() throws ResourceNotFoundException {
        List<Device> devices = deviceDao.findAll();
        if (devices.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(devices);
    }

    /**
     * rest api to get device by id
     * @param deviceId
     * @return
     */
    @GetMapping("/v1/devices/{deviceId}")
    @Operation(summary = "GetDevice", description = "Returns a Device By Id")
    public ResponseEntity<Object> getDeviceById(@PathParam("deviceId") Integer deviceId) {
        return deviceService.findDeviceById(deviceId);
    }

    /**
     * rest api to get categories
     * @return
     * @throws ResourceNotFoundException
     */
    @GetMapping("/v1/devices/categories")
    @Operation(summary = "GetDeviceCategories", description = "Returns a List Of DeviceCategories")
    public ResponseEntity<Object> getDeviceCategory() throws ResourceNotFoundException {
        return deviceService.getCategory();
    }

    /**
     * rest api to create new device
     * @param device
     * @return
     * @throws FieldEmptyException
     * @throws ResourceNotFoundException
     */
    @PostMapping("/v1/device")
    @Operation(summary = "CreateDevice", description = "Create a Device")
    public ResponseEntity<Object> addDeviceDetails(@RequestBody DeviceRequest device) throws FieldEmptyException, ResourceNotFoundException {
        return deviceService.addDeviceDetails(device);
    }

    /**
     * rest api to add device category
     * @param deviceCategory
     * @return
     * @throws FieldEmptyException
     * @throws AlreadyExistsException
     */
    @PostMapping("/v1/devices/category")
    @Operation(summary = "CreateDeviceCategory", description = "Create a DeviceCategory")
    public ResponseEntity<Object> addDeviceCategory(@RequestBody DeviceCategoryRequest deviceCategory) throws FieldEmptyException, AlreadyExistsException {

        return deviceService.saveDeviceCategory(deviceCategory);
    }

    /**
     * to create device along  with category
     * @param deviceName
     * @return
     * @throws FieldEmptyException
     * @throws AlreadyExistsException
     */
    @PostMapping("/v1/devices/deviceName")
    @Operation(summary = "CreateDeviceName", description = "Create a DeviceName")
    public ResponseEntity addDeviceName(@RequestBody DeviceNameRequest deviceName) throws FieldEmptyException, AlreadyExistsException
    {
        return deviceService.saveDeviceName(deviceName);
    }


    @PutMapping("/v1/device/{id}")
    @Operation(summary = "EditDevice", description = "update the device")
    public ResponseEntity updateDevice(@PathVariable(name = "id") Integer id, @RequestBody DeviceRequest device) throws FieldEmptyException {
        return deviceService.updateDeviceDetails(id,device);
    }

    @DeleteMapping("/v1/devices/{id}")
    @Operation(summary = "DeleteDevice", description = "delete the device")
    public ResponseEntity deleteDevice(@PathVariable(name = "id") Integer id) throws ResourceNotFoundException {
        return deviceService.deleteDeviceDetails(id);
    }



}