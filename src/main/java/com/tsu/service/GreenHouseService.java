package com.tsu.service;

import com.github.pagehelper.PageInfo;
import com.tsu.entity.GreenHouse;
import com.tsu.entity.Location;
import org.apache.shiro.authz.AuthorizationException;

import java.util.List;

/**
 * @author zzz
 */
public interface GreenHouseService {
    /**
     * 添加大棚
     *
     * @param greenHouse
     */
    void add(GreenHouse greenHouse);

    /**
     * 先检测是否有大棚所在基地的权限，在进行添加，没有添加权限会抛出异常
     *
     * @param greenHouse
     * @param userId
     * @throws AuthorizationException
     */
    void checkAdd(GreenHouse greenHouse, Integer userId) throws AuthorizationException;


    /**
     * 通过大棚的id获取基地的id
     *
     * @param greenHouseId
     * @return
     */
    Integer getLocationIdByGreenHouseId(Integer greenHouseId);

    /**
     * 通过location的ids获取greenhouse的ids
     *
     * @param locationIds
     * @return
     */
    List<Integer> getIdsByLocationIds(List<Integer> locationIds);

    /**
     * 获取所有的greenHouse
     *
     * @param page
     * @param size
     * @return
     */
    List<GreenHouse> getAll();

    /**
     * 用户获取所有属于他的GreenHouse
     *
     * @param page
     * @param size
     * @param userId
     * @return
     */
    List<GreenHouse> getAllByUserId(Integer userId);

    /**
     * 获取所有的大棚和所在的基地
     *
     * @param page
     * @param size
     * @return
     */
    PageInfo<GreenHouse> getAllHasLocation(Integer page, Integer size);

    /**
     * 获取用户拥有的的大棚和所在的基地
     *
     * @param page
     * @param size
     * @param userId
     * @return
     */
    PageInfo<GreenHouse> getAllHasLocationByUserId(Integer page, Integer size, Integer userId);

    /**
     * 管理员删除大棚
     *
     * @param greenHouseId
     */
    void delete(Integer greenHouseId);

    /**
     * 先查看是否具有这个基地的权限，在进行是否删除
     *
     * @param greenHouseId
     * @param userId
     */
    void checkDelete(Integer greenHouseId, Integer userId);

    /**
     * 管理员修改大棚
     *
     * @param greenHouse
     */
    void update(GreenHouse greenHouse);

    /**
     * 用户修改大棚
     *
     * @param greenHouse
     * @param userId
     * @throws AuthorizationException
     */
    void checkUpdate(GreenHouse greenHouse, Integer userId) throws AuthorizationException;

    /**
     * 获取大棚的个数
     * @return
     */
    Integer getCount();

    /**
     * 通过locationId来获取大棚
     * @param locationId
     * @return
     */
    List<GreenHouse> getByLocationId(Integer locationId);

}
