package com.tsu.mapper;

import com.github.pagehelper.PageInfo;
import com.tsu.entity.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zzz
 */
@Mapper
public interface DeviceMapper {
    /**
     * 根据大棚id查询设备
     *
     * @param greenHouseId
     * @return
     */
    List<Device> getAllCollectionByGreenHouseId(Integer greenHouseId);

    /**
     * 直接添加设备
     *
     * @param device
     */
    void save(Device device);

    /**
     * 根据设备Id来获取大棚Id
     *
     * @return
     */
    Integer getGreenHouseIdByDeviceId(Integer deviceId);

    /**
     * 通过deviceId来获取主键
     *
     * @param deviceId
     * @return
     */
    Integer getIdByDeviceId(String deviceId);

    /**
     * 获取全部的设备和对应的大棚
     *
     * @return
     */
    List<Device> getAllAndGreenHouse();

    /**
     * 通过大棚id来获取设备信息
     *
     * @param greenHouseIds
     * @return
     */
    List<Device> getAllByGreenHouseIds(@Param("greenHouseIds") List<Integer> greenHouseIds);

    /**
     * 更新
     *
     * @param device
     */
    void update(Device device);

    /**
     * 删除
     *
     * @param deviceId
     */
    void delete(Integer deviceId);

    /**
     * 获取所有的设备id
     *
     * @return
     */
    List<Integer> getAllId();

    /**
     * 通过大棚id来获取id
     *
     * @param greenHouseIds
     * @return
     */
    List<Integer> getIdByGreenHouseIds(@Param("greenHouseIds") List<Integer> greenHouseIds);

    List<Device> getByGreenHouseIds(@Param("greenHouseIds") List<Integer> greenHouseIds);

    Integer getCount();

    List<Device> getByGreenHouseId(Integer greenHouseId);


    List<Integer> getIdByGreenHouseId(Integer greenHouseId);
}
