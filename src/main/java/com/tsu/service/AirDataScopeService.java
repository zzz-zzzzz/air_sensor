package com.tsu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tsu.entity.AirDataScope;

/**
 * @author zzz
 */
public interface AirDataScopeService {
    /**
     * 根据设备主键获取空气数据范围
     *
     * @param deviceId
     * @return
     */
    AirDataScope getByDeviceId(Integer deviceId);

    /**
     * 根据传入的AirDataScope是否由主键，来判断是保存还是修改
     *
     * @param airDataScope
     */
    void saveOrUpdate(AirDataScope airDataScope) throws JsonProcessingException;

    /**
     * 检查是否具有权限在进行保存或修改
     *
     * @param airDataScope
     */
    void saveOrUpdateAndCheck(AirDataScope airDataScope, Integer userId) throws JsonProcessingException;

}
