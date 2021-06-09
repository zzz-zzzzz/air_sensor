package com.tsu.controller;

import com.tsu.constant.HttpStatusConstant;
import com.tsu.entity.AirDataAverage;
import com.tsu.service.AirDataAverageService;
import com.tsu.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zzz
 */
@RestController
@RequestMapping("/airDataAverage")
@CrossOrigin
public class AirDataAverageController {

    @Autowired
    AirDataAverageService airDataAverageService;

    @PostMapping("/getByRecodeTimeAndDeviceId/{deviceId}")
    public Result getByRecodeTimeAndDeviceId(@PathVariable String deviceId, @RequestBody List<String> recordTimeList) {
        List<AirDataAverage> airDataAverageList =airDataAverageService.getByRecodeTimeAndDeviceId(recordTimeList,deviceId);
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS)
                .add("airDataAverageList", airDataAverageList);
    }
}
