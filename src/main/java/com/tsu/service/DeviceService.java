package com.tsu.service;

import com.github.pagehelper.PageInfo;
import com.tsu.entity.Device;
import com.tsu.vo.DeviceStatusVo;
import org.apache.shiro.authz.AuthorizationException;

import java.util.List;
import java.util.Map;

/**
 * @author zzz
 */
public interface DeviceService {
    /**
     * 管理员直接进行添加
     *
     * @param device
     */
    void add(Device device);

    /**
     * 用户查看是否具有设备所在仓库的权限，在进行是否添加
     *
     * @param device
     * @param userId
     * @throws AuthorizationException
     */
    void checkAdd(Device device, Integer userId) throws AuthorizationException;


    /**
     * 根据设备Id获取大棚id
     *
     * @param deviceId
     * @return
     */
    Integer getLocationByDeviceId(Integer deviceId);

    /**
     * 通过deviceId来获取主键
     *
     * @param deviceId
     * @return
     */
    Integer getIdByDeviceId(String deviceId);

    /**
     * 管理元直接获取所有的设备
     *
     * @param page
     * @param size
     * @return
     */
    PageInfo<Device> getAllHasGreenHouseByPage(Integer page, Integer size);

    /**
     * 用户获取对应基地的设备
     *
     * @param page
     * @param size
     * @param userId
     * @return
     */
    PageInfo<Device> getAllHasGreenHouseByPageAndUserId(Integer page, Integer size, Integer userId);

    /**
     * 管理员直接添加
     *
     * @param device
     */
    void update(Device device);

    /**
     * 用户先检查权限在添加
     *
     * @param device
     * @param userId
     * @throws AuthorizationException
     */
    void checkUpdate(Device device, Integer userId) throws AuthorizationException;

    /**
     * 管理员直接删除设备
     *
     * @param deviceId
     */
    void delete(Integer deviceId);

    /**
     * 用户需要先检查是否具有权限在进行删除
     *
     * @param deviceId
     * @param userId
     * @throws AuthorizationException
     */
    void checkDelete(Integer deviceId, Integer userId) throws AuthorizationException;

    /**
     * 用户获取在线的设备总数
     *
     * @param userId
     * @return
     */
    Long getOnlineDeviceCountByUserId(Integer userId);

    /**
     * 管理员获取在线的设备总数
     *
     * @return
     */
    Long getOnlineDeviceCount(List<Integer> deviceIds);


    /**
     * 设置设备为报警
     *
     * @param id
     */
    void setDeviceIsAlarm(Integer id);

    void setDeviceIsNotAlarm(Integer id);

    /**
     * 获取所有的设备信息
     *
     * @return
     */
    Map<String, Object> getOverviewData(List<String> dateList);

    /**
     * 获取用户所有的设备信息
     *
     * @return
     */
    Map<String, Object> getOverviewDataByUserId(List<String> dateList, Integer userId);


    Integer getCount();

    Long getAlarmDeviceCount(List<Integer> deviceIds);


    List<Device> getByGreenHouseId(Integer greenHouseId);

    /**
     * 通过id获取设备的状态
     *
     *
     * @param id
     * @return
     */
    Integer getStatus(Integer id);

    DeviceStatusVo getStatusByGreenHouseId(Integer greenHouseId);
}
