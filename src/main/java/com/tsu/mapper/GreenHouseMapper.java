package com.tsu.mapper;

import com.github.pagehelper.PageInfo;
import com.tsu.entity.GreenHouse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zzz
 */
@Mapper
public interface GreenHouseMapper {

    /**
     * 通过基地id查询大棚
     *
     * @param locationId
     * @return
     */
    List<GreenHouse> getAllCollectionByLocationId(Integer locationId);

    /**
     * 添加大棚
     *
     * @param greenHouse
     */
    void save(GreenHouse greenHouse);

    /**
     * 通过大棚id获取所在基地id
     *
     * @param greenHouseId
     * @return
     */
    Integer getLocationIdByGreenHouseId(Integer greenHouseId);

    /**
     * 通过id获取大棚
     *
     * @param id
     * @return
     */
    GreenHouse getById(Integer id);

    /**
     * 通过location的ids获取greenhouse的ids
     *
     * @param locationIds
     * @return
     */
    List<Integer> getIdsByLocationIds(@Param("locationIds") List<Integer> locationIds);

    /**
     * 管理员获取所有的greenHouse
     *
     * @return
     */
    List<GreenHouse> getAll();

    /**
     * 通过基地地址来获取greenHouse
     *
     * @param locationIds
     * @return
     */
    List<GreenHouse> getAllByLocationIds(@Param("locationIds") List<Integer> locationIds);

    /**
     * 管理员获取所有的大棚以及大棚所在的基地
     *
     * @return
     */
    List<GreenHouse> getAllHasLocation();

    /**
     * 通过基地id获取基地下面所有的大棚
     *
     * @param locationIds
     * @return
     */
    List<GreenHouse> getAllHasLocationByLocationIds(List<Integer> locationIds);

    /**
     * 管理员删除
     *
     * @param greenHouseId
     */
    void delete(Integer greenHouseId);

    /**
     * 更新大棚
     *
     * @param greenHouse
     */
    void update(GreenHouse greenHouse);

    Integer getCount();

    List<GreenHouse> getAllByLocationId(Integer locationId);

    /* Integer getCountByLocationIds(@Param("locationIds") List<Integer> locationIds);*/
}
