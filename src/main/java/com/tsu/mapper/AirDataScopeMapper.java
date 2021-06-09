package com.tsu.mapper;

import com.tsu.entity.AirDataScope;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author zzz
 */
@Mapper
public interface AirDataScopeMapper {

    /**
     * 根据设备主键获取空气数据范围
     * @param deviceId
     * @return
     */
     AirDataScope getByDeviceId(Integer deviceId);

    /**
     * 保存
     * @param airDataScope
     */
    void save(AirDataScope airDataScope);

    /**
     * 更新
     * @param airDataScope
     */
    void update(AirDataScope airDataScope);

}
