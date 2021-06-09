package com.tsu.service;

import com.github.pagehelper.PageInfo;
import com.tsu.entity.Relay;
import org.apache.shiro.authz.AuthorizationException;

import java.util.List;

/**
 * @author zzz
 */
public interface RelayService {
    /**
     * 保存
     *
     * @param relay
     */
    void add(Relay relay);

    /**
     * 保存mqtt服务器发送的数据
     *
     * @param bytes
     */
    void saveMqttBytes(byte[] bytes);

    /**
     * 通过大棚id获取继电器
     *
     * @param greenHouseId
     * @return
     */
    List<Relay> getByGreenHouseId(Integer greenHouseId);


    PageInfo<Relay> getAllHasGreenHouseByPage(Integer page, Integer size);

    PageInfo<Relay> getAllHasGreenHouseByPageAndUserId(Integer page, Integer size, Integer userId);

    void delete(Integer id);

    void update(Relay relay);

    void sendInstruction(Integer id, Boolean action);

    void checkAndSendInstruction(Integer id, Boolean action, Integer userId) throws AuthorizationException;

    void checkDelete(Integer id, Integer userId);

    void checkAdd(Relay relay,Integer userId);

    void checkUpdate(Relay relay, Integer userId);

    /**
     * 获取总数
     */
    Integer getCount();

    Integer getCountByGreenHouseIds(List<Integer> greenHouseIds);

    List<Relay> getByIdsAndType(int type, List<Integer> ids);

    void sendBatchInstruction(List<Relay> relayList, boolean action);

    void switchAutoTrigger(Integer id, Boolean action);

    void checkSwitchAutoTrigger(Integer id, Boolean action, Integer userId);
}
