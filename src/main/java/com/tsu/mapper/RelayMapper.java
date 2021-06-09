package com.tsu.mapper;

import com.github.pagehelper.PageInfo;
import com.tsu.entity.Relay;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zzz
 */
@Mapper
public interface RelayMapper {

    /**
     * 保存
     *
     * @param relay
     */
    void save(Relay relay);

    /**
     * 更新继电器的状态
     *
     * @param isOpen
     * @param identityId
     */
    void updateIsOpenByIdentityIdAndDeviceId(@Param("isOpen") boolean isOpen, @Param("identityId") Integer identityId, @Param("deviceId") String deviceId);

    /**
     * 通过大棚id来获取继电器
     *
     * @param greenHouseId
     * @return
     */
    List<Relay> getByGreenHouseId(Integer greenHouseId);

    /**
     * 获取所有继电器
     *
     * @return
     */
    List<Relay> getAllHasGreenHouse();

    /**
     * 通过大棚id来获取继电器
     *
     * @param greenHouseIds
     * @return
     */
    List<Relay> getAllHasGreenHouseByGreenHouseIds(@Param("greenHouseIds") List<Integer> greenHouseIds);

    /**
     * 删除
     *
     * @param id
     */
    void deleteById(Integer id);

    /**
     * 更新
     *
     * @param relay
     */
    void update(Relay relay);

    /**
     * @param id
     */
    Relay getIdentityIdAndDeviceIdById(Integer id);

    /**
     * 通过id来获取继电器所在的大棚id
     *
     * @param userId
     * @return
     */
    Integer getIdGreenHouseIdById(Integer userId);

    /**
     * 根据id来更新继电器状态
     *
     * @param action
     * @param id
     */
    void updateIsOpenById(@Param("isOpen") Boolean isOpen, @Param("id") Integer id);

    Integer getCount();

    Integer getCountByGreenHouseIds(@Param("greenHouseIds") List<Integer> greenHouseIds);

    List<Relay> getByIdsAndType(@Param("type") int type, @Param("ids") List<Integer> ids);

    void updateAutoTriggerById(@Param("id") Integer id,@Param("autoTrigger") Boolean autoTrigger);
}
