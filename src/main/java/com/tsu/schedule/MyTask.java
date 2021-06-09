package com.tsu.schedule;

import com.tsu.entity.AirDataAverage;
import com.tsu.entity.AirDataValueAverage;
import com.tsu.service.AirDataAverageService;
import com.tsu.service.AirDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zzz
 */
@Component
@Slf4j
public class MyTask {

    @Autowired
    AirDataService airDataService;

    @Autowired
    AirDataAverageService airDataAverageService;


    /**
     * 每天的0:01执行
     */
    @Scheduled(cron = "0 1 0 * * ?")
    public void task01() throws ParseException {
        // TODO: 2021/5/21 存在bug
        log.info("温湿度分析开始运行");
        long oneMinutesTime = 60 * 1000;
        Date now = new Date();
        long nowTime = now.getTime();
        long twentyFourHoursTime = 24 * 60 * oneMinutesTime;

        //1.获取前一天的结束
        Date yesterdayEnd = new Date(nowTime - oneMinutesTime);
        long yesterdayEndTime = yesterdayEnd.getTime();

        //2.获取前一天的开始
        Date yesterdayStart = new Date(yesterdayEndTime - twentyFourHoursTime);


        //3.获取前一天平均的温度
        List<AirDataValueAverage> temperatureAverage = airDataService.getTemperatureAverage(yesterdayStart, yesterdayEnd);
        //4.获取前一天的平均湿度
        List<AirDataValueAverage> humidityAverage = airDataService.getHumidityAverage(yesterdayStart, yesterdayEnd);

        //5.保存进数据库
        HashMap<String, AirDataAverage> saveMap = new HashMap<>();
        temperatureAverage.forEach(airDataValueAverage -> {
            String deviceId = airDataValueAverage.getDeviceId();
            AirDataAverage airDataAverage = new AirDataAverage()
                    .setTemperatureAverage(airDataValueAverage.getAverage())
                    .setDeviceId(deviceId)
                    .setRecordTime(yesterdayStart);
            saveMap.put(deviceId, airDataAverage);
        });

        List<AirDataAverage> airDataAverageList = humidityAverage.stream().map(airDataValueAverage -> {
            String deviceId = airDataValueAverage.getDeviceId();
            if (saveMap.containsKey(deviceId)) {
                AirDataAverage airDataAverage = saveMap.get(deviceId);
                airDataAverage.setHumidityAverage(airDataValueAverage.getAverage());
                return airDataAverage;
            } else {
                return new AirDataAverage()
                        .setDeviceId(deviceId)
                        .setHumidityAverage(airDataValueAverage.getAverage())
                        .setRecordTime(yesterdayStart);
            }
        }).collect(Collectors.toList());
        airDataAverageService.batchSave(airDataAverageList);
    }
}
