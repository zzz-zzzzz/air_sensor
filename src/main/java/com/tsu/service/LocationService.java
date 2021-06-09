package com.tsu.service;

import com.github.pagehelper.PageInfo;
import com.tsu.entity.GreenHouse;
import com.tsu.entity.Location;

import java.util.List;

/**
 * @author zzz
 */
public interface LocationService {
    /**
     * 根据用户的id获取基地（分页）
     *
     * @param page   当前页
     * @param size   页大小
     * @param userId 用户id
     * @return
     */
    PageInfo<Location> getAllByUserIdAndPage(Integer page, Integer size, Integer userId);

    /**
     * 根据用户查询基地和设备和大棚
     *
     * @param page
     * @param size
     * @param userId
     * @return
     */
    List<Location> getAllAndCollectionByPageAndUserId(Integer page, Integer size, Integer userId);

    /**
     * 添加基地
     *
     * @param location
     */
    void add(Location location);

    /**
     * 判断用户是否具有这个基地
     *
     * @param userId
     * @param locationId
     * @return
     */
    boolean hasLocation(Integer userId, Integer locationId);

    /**
     * 未用户分配基地
     *
     * @param userId
     * @param locationId
     */
    void distributeLocation(Integer userId, Integer locationId);

    /**
     * 管理员获取所有的基地
     *
     * @param page
     * @param size
     * @return
     */
    PageInfo<Location> getAll(Integer page, Integer size);

    /**
     * 管理员获取所有的基地的大棚，和设备
     *
     * @param page
     * @param size
     * @return
     */
    List<Location> getAllAndCollection(Integer page, Integer size);

    /**
     * 获取所有基地
     *
     * @return
     */
    List<Location> getAll();

    /**
     * 根据用户名获取基地
     *
     * @param userId
     * @return
     */
    List<Location> getAllByUserId(Integer userId);

    /**
     * 根据用户名基地地址删除关系
     *
     * @param userId
     * @param locationId
     */
    void deleteLocationUserRelation(Integer userId, Integer locationId);

    /**
     * 管理员获取所有的基地的大棚，和设备
     *
     * @return
     */
    List<Location> getAllAndCollection();

    /**
     * 用户获取持有所有的基地的大棚，和设备
     *
     * @param userId
     * @return
     */
    List<Location> getAllAndCollectionByUserId(Integer userId);

    /**
     * 通过userId来获取关联的locationId
     *
     * @param userId
     * @return
     */
    List<Integer> getIdsByUserId(Integer userId);

    /**
     * 更新基地
     *
     * @param location
     */
    void update(Location location);

    /**
     * 删除基地
     *
     * @param locationId
     */
    void delete(Integer locationId);

    /**
     * 获取设备总数
     * @return
     */
    Integer getCount();

    Location getByGreenHouseId(Integer greenHouseId);
}

