package com.tsu.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tsu.constant.DeviceStatusConstant;
import com.tsu.entity.AlarmFrequency;
import com.tsu.entity.Device;
import com.tsu.entity.Location;
import com.tsu.mapper.DeviceMapper;
import com.tsu.service.*;
import com.tsu.vo.DeviceStatusVo;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zzz
 */
@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    DeviceMapper deviceMapper;

    @Autowired
    LocationService locationService;

    @Autowired
    GreenHouseService greenHouseService;

    @Autowired
    AlarmFrequencyService alarmFrequencyService;

    @Autowired
    RelayService relayService;


    @Autowired
    AirDataService airDataService;

    @Value("${system.alarm-timeout}")
    Integer alarmTimeOut;


    @Override
    public void add(Device device) {
        deviceMapper.save(device);
    }

    @Override
    public void checkAdd(Device device, Integer userId) throws AuthorizationException {
        Integer locationId = greenHouseService.getLocationIdByGreenHouseId(device.getGreenHouseId());
        if (locationId != null && locationService.hasLocation(userId, locationId)) {
            add(device);
        } else {
            throw new AuthorizationException("没有设备的添加权限");
        }
    }

    @Override
    public Integer getLocationByDeviceId(Integer deviceId) {
        Integer greenHouseId = deviceMapper.getGreenHouseIdByDeviceId(deviceId);
        return greenHouseService.getLocationIdByGreenHouseId(greenHouseId);
    }

    @Override
    public Integer getIdByDeviceId(String deviceId) {
        return deviceMapper.getIdByDeviceId(deviceId);
    }


    @Override
    public PageInfo<Device> getAllHasGreenHouseByPage(Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<Device> deviceList = deviceMapper.getAllAndGreenHouse();
        return new PageInfo<>(deviceList);
    }

    @Override
    public PageInfo<Device> getAllHasGreenHouseByPageAndUserId(Integer page, Integer size, Integer userId) {
        List<Integer> locationIds = locationService.getIdsByUserId(userId);
        List<Integer> greenHouseIds = greenHouseService.getIdsByLocationIds(locationIds);
        PageHelper.startPage(page, size);
        List<Device> deviceList = deviceMapper.getAllByGreenHouseIds(greenHouseIds);
        return new PageInfo<>(deviceList);
    }

    @Override
    public void update(Device device) {
        deviceMapper.update(device);
    }

    @Override
    public void checkUpdate(Device device, Integer userId) throws AuthorizationException {
        Integer locationId = greenHouseService.getLocationIdByGreenHouseId(device.getGreenHouseId());
        if (locationId != null && locationService.hasLocation(userId, locationId)) {
            update(device);
        } else {
            throw new AuthorizationException("没有设备的添加权限");
        }
    }

    @Override
    public void delete(Integer deviceId) {
        deviceMapper.delete(deviceId);
    }

    @Override
    public void checkDelete(Integer deviceId, Integer userId) throws AuthorizationException {
        Integer greenHouseId = deviceMapper.getGreenHouseIdByDeviceId(deviceId);
        Integer locationId = greenHouseService.getLocationIdByGreenHouseId(greenHouseId);
        if (locationService.hasLocation(userId, locationId)) {
            delete(deviceId);
        } else {
            throw new AuthorizationException("没有设备的操作权限");
        }
    }


    @Override
    public Long getOnlineDeviceCountByUserId(Integer userId) {
        List<Integer> locationIds = locationService.getIdsByUserId(userId);
        List<Integer> greenHouseIds = greenHouseService.getIdsByLocationIds(locationIds);
        List<Integer> deviceIds = deviceMapper.getIdByGreenHouseIds(greenHouseIds);
        List<String> redisStringKey = idToRedisOnlineKey(deviceIds);
        return Objects.requireNonNull(redisTemplate.opsForValue().multiGet(redisStringKey)).stream().filter(Objects::nonNull).count();
    }

    @Override
    public Long getOnlineDeviceCount(List<Integer> deviceIds) {
        List<String> redisStringKey = idToRedisOnlineKey(deviceIds);
        return Objects.requireNonNull(redisTemplate.opsForValue().multiGet(redisStringKey)).stream().filter(Objects::nonNull).count();
    }

    private List<String> idToRedisOnlineKey(List<Integer> deviceIds) {
        return deviceIds.stream().map(id -> "air_sensor:online_device:" + id).collect(Collectors.toList());
    }


    @Override
    public void setDeviceIsAlarm(Integer id) {
        String redisKey = "air_sensor:alarm_device:" + id;
        String s = redisTemplate.opsForValue().get(redisKey);
        if (s == null) {
            redisTemplate.opsForValue().set(redisKey, ".", alarmTimeOut, TimeUnit.MINUTES);
            alarmFrequencyService.addAlarmFrequency(id);
        }
    }


    @Override
    public void setDeviceIsNotAlarm(Integer id) {
        String redisKey = "air_sensor:alarm_device:" + id;
        String s = redisTemplate.opsForValue().get(redisKey);
        if (s != null) {
            redisTemplate.delete(redisKey);
        }
    }

    @Override
    public Map<String, Object> getOverviewData(List<String> dateList) {
        Map<String, Object> data = new HashMap<>(9);
        //获取所有的基地和地理坐标
        List<Location> locationList = locationService.getAll();
        data.put("locationList", locationList);
        //获取所有的基地个数
        Integer locationCount = locationList != null ? locationList.size() : 0;
        data.put("locationCount", locationCount);
        //获取所有的大棚个数
        Integer greenHouseCount = greenHouseService.getCount();
        data.put("greenHouseCount", greenHouseCount);
        //获取所有的设备个数
        List<Integer> deviceIds = deviceMapper.getAllId();
        //获取统计的数据量
        Long airDataCount = airDataService.getCount();
        data.put("airDataCount", airDataCount);
        //获取一周内的数据
        List<AlarmFrequency> dateAlarmCountList = alarmFrequencyService.getCountByDates(dateList);
        data.put("dateAlarmCountList", dateAlarmCountList);
        //统计总报警次数
        Integer alarmFrequencyCount = alarmFrequencyService.getCount();
        data.put("alarmFrequencyCount", alarmFrequencyCount);
        //获取继电器总数
        Integer relayCount = relayService.getCount();
        data.put("relayCount", relayCount);
        fillMapCommonData(data, deviceIds);
        return data;
    }


    @Override
    public Long getAlarmDeviceCount(List<Integer> deviceIds) {
        List<String> redisStringKey = idToRedisAlarmKey(deviceIds);
        return Objects.requireNonNull(redisTemplate.opsForValue().multiGet(redisStringKey)).stream().filter(Objects::nonNull).count();
    }

    private List<String> idToRedisAlarmKey(List<Integer> deviceIds) {
        return deviceIds.stream().map(id -> "air_sensor:alarm_device:" + id).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getOverviewDataByUserId(List<String> dateList, Integer userId) {
        Map<String, Object> data = new HashMap<>(8);
        //获取所有的基地和地理坐标
        List<Location> locationList = locationService.getAllByUserId(userId);
        data.put("locationList", locationList);
        //获取所有的基地个数
        Integer locationCount = locationList != null ? locationList.size() : 0;
        data.put("locationCount", locationCount);
        List<Integer> locationIds = locationList.stream().map(Location::getId).collect(Collectors.toList());
        //获取所有的大棚个数
        List<Integer> greenHouseIds = greenHouseService.getIdsByLocationIds(locationIds);
        data.put("greenHouseCount", greenHouseIds != null ? greenHouseIds.size() : 0);
        List<Device> deviceList = deviceMapper.getByGreenHouseIds(greenHouseIds);
        ArrayList<Integer> deviceIds = new ArrayList<>();
        ArrayList<String> deviceDeviceIds = new ArrayList<>();
        deviceList.forEach(device -> {
            deviceIds.add(device.getId());
            deviceDeviceIds.add(device.getDeviceId());
        });
        //获取统计的数据量
        Long airDataCount = airDataService.getCountByDeviceId(deviceDeviceIds);
        data.put("airDataCount", airDataCount);
        //获取一周内的数据
        List<AlarmFrequency> dateAlarmCountList = alarmFrequencyService.getCountByDatesAndDeviceIds(dateList, deviceIds);
        data.put("dateAlarmCountList", dateAlarmCountList);
        //统计总报警次数
        Integer alarmFrequencyCount = alarmFrequencyService.getCountByDeviceId(deviceIds);
        data.put("alarmFrequencyCount", alarmFrequencyCount);
        //查询继电器总数
        Integer relayCount = relayService.getCountByGreenHouseIds(greenHouseIds);
        data.put("relayCount", relayCount);
        fillMapCommonData(data, deviceIds);
        return data;
    }

    private void fillMapCommonData(Map<String, Object> data, List<Integer> deviceIds) {
        //1.获取所有的设备个数
        Integer deviceCount = deviceIds.size();
        data.put("deviceCount", deviceCount);
        //2.获取所有的在线设备的个数
        Long onlineDeviceCount = getOnlineDeviceCount(deviceIds);
        data.put("onlineDeviceCount", onlineDeviceCount);
        //5.获取报警设备中数
        Long alarmCount = getAlarmDeviceCount(deviceIds);
        data.put("alarmCount", alarmCount);
    }


    @Override
    public Integer getCount() {
        return deviceMapper.getCount();
    }

    @Override
    public List<Device> getByGreenHouseId(Integer greenHouseId) {
        return deviceMapper.getByGreenHouseId(greenHouseId);
    }

    @Override
    public Integer getStatus(Integer id) {
        String str1 = redisTemplate.opsForValue().get("air_sensor:alarm_device:" + id);
        if (str1 != null) {
            return DeviceStatusConstant.WARNING;
        }
        String str2 = redisTemplate.opsForValue().get("air_sensor:online_device:" + id);
        if (str2 != null) {
            return DeviceStatusConstant.NORMAL;
        }
        return DeviceStatusConstant.OFFLINE;
    }

    @Override
    public DeviceStatusVo getStatusByGreenHouseId(Integer greenHouseId) {
        List<Integer> deviceIds = deviceMapper.getIdByGreenHouseId(greenHouseId);
        if (deviceIds == null) {
            return null;
        }
        long count = deviceIds.size();
        //获取在线的设备
        Long onlineDeviceCount = getOnlineDeviceCount(deviceIds);
        //获取在报警的设备
        Long alarmDeviceCount = getAlarmDeviceCount(deviceIds);
        return new DeviceStatusVo()
                .setCount(count)
                .setOnlineCount(onlineDeviceCount)
                .setAlarmCount(alarmDeviceCount);
    }
}
