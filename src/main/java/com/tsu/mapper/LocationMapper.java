package com.tsu.mapper;

import com.tsu.entity.Location;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zzz
 */
@Mapper
public interface LocationMapper {
    /**
     * 通过用户名查询基地Id
     *
     * @param userid
     * @return
     */
    List<Integer> getIdsByUserId(Integer userid);

    /**
     * 通过用户名查询基地
     *
     * @param userid
     * @return
     */
    List<Location> getAllByUserId(Integer userid);

    /**
     * 通过用户名查询基地和大棚和设备
     *
     * @param userId
     * @return
     */
    List<Location> getAllCollectionByUserId(Integer userId);

    /**
     * 添加基地
     *
     * @param location
     */
    void save(Location location);

    /**
     * 判断当前用户是否拥有基地
     *
     * @param userId     用户id
     * @param locationId 基地id
     * @return
     */
    Integer hasLocation(@Param("userId") Integer userId, @Param("locationId") Integer locationId);

    /**
     * 为用户分配基地
     *
     * @param userId
     * @param locationId
     */
    void distributeLocation(@Param("userId") Integer userId, @Param("locationId") Integer locationId);

    /**
     * 查询所有的基地
     *
     * @return
     */
    List<Location> getAll();

    /**
     * 查询所有基地及其大棚、设备
     *
     * @return
     */
    List<Location> getAllAndCollection();

    /**
     * 根据userId 和 locationId 查询 行的个数
     *
     * @param userId
     * @param locationId
     * @return
     */
    int getCountUserIdAndLocationId(@Param("userId") Integer userId, @Param("locationId") Integer locationId);

    /**
     * 根据userId 和 locationId删除用户和基地之间的关系
     * @param userId
     * @param locationId
     */
    void deleteRelationByUserIdAndLocationId(Integer userId, Integer locationId);

    /**
     * 通过id获取location
     * @param id
     * @return
     */
    Location getById(Integer id);

    /**
     * 更新基地
     * @param location
     */
    void update(Location location);

    /**
     * 删除基地
     * @param locationId
     */
    void delete(Integer locationId);

    /**
     * 删除基地与用户之间的关系
     * @param locationId
     */
    void deleteRelationByLocationId(Integer locationId);

    Integer getCount();

    Location getByGreenHouseId(Integer greenHouseId);
}
