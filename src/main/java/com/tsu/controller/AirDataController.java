package com.tsu.controller;

import com.tsu.constant.HttpStatusConstant;
import com.tsu.entity.AirData;
import com.tsu.service.AirDataService;
import com.tsu.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Action;

/**
 * @author zzz
 */
@RestController
@RequestMapping("/airData")
@CrossOrigin
public class AirDataController {

    @Autowired
    AirDataService airDataService;

    @GetMapping("/getLatest/{deviceId}")
    public Result getLatest(@PathVariable String deviceId) {
        AirData airData = airDataService.getLatest(deviceId);
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS)
                .add("airData", airData);
    }
}
