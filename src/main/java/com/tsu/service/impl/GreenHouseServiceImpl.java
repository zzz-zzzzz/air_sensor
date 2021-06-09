package com.tsu.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tsu.entity.GreenHouse;
import com.tsu.entity.Location;
import com.tsu.mapper.GreenHouseMapper;
import com.tsu.mapper.LocationMapper;
import com.tsu.service.GreenHouseService;
import com.tsu.service.LocationService;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zzz
 */
@Service
public class GreenHouseServiceImpl implements GreenHouseService {

    @Autowired
    GreenHouseMapper greenHouseMapper;

    @Autowired
    LocationService locationService;


    @Override
    public void add(GreenHouse greenHouse) {
        greenHouseMapper.save(greenHouse);
    }

    @Override
    public void checkAdd(GreenHouse greenHouse, Integer userId) throws AuthorizationException {
        if (locationService.hasLocation(userId, greenHouse.getLocationId())) {
            add(greenHouse);
        } else {
            throw new AuthorizationException("没有权限");
        }
    }

    @Override
    public Integer getLocationIdByGreenHouseId(Integer greenHouseId) {
        return greenHouseMapper.getLocationIdByGreenHouseId(greenHouseId);
    }

    @Override
    public List<Integer> getIdsByLocationIds(List<Integer> locationIds) {
        return greenHouseMapper.getIdsByLocationIds(locationIds);
    }

    @Override
    public List<GreenHouse> getAll() {
        return greenHouseMapper.getAll();
    }

    @Override
    public List<GreenHouse> getAllByUserId(Integer userId) {
        List<Integer> locationIds = locationService.getIdsByUserId(userId);
        return greenHouseMapper.getAllByLocationIds(locationIds);
    }

    @Override
    public PageInfo<GreenHouse> getAllHasLocation(Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<GreenHouse> greenHouseList = greenHouseMapper.getAllHasLocation();
        return new PageInfo<>(greenHouseList);
    }

    @Override
    public PageInfo<GreenHouse> getAllHasLocationByUserId(Integer page, Integer size, Integer userId) {
        List<Integer> locationIds = locationService.getIdsByUserId(userId);
        PageHelper.startPage(page, size);
        List<GreenHouse> greenHouseList = greenHouseMapper.getAllHasLocationByLocationIds(locationIds);
        return new PageInfo<>(greenHouseList);
    }

    @Override
    public void delete(Integer greenHouseId) {
        greenHouseMapper.delete(greenHouseId);
    }

    @Override
    public void checkDelete(Integer greenHouseId, Integer userId) throws AuthorizationException {
        Integer locationId = greenHouseMapper.getLocationIdByGreenHouseId(greenHouseId);
        if (locationService.hasLocation(userId, locationId)) {
            delete(greenHouseId);
        } else {
            throw new AuthorizationException("没有权限");
        }
    }

    @Override
    public void update(GreenHouse greenHouse) {
        greenHouseMapper.update(greenHouse);
    }

    @Override
    public void checkUpdate(GreenHouse greenHouse, Integer userId) throws AuthorizationException {
        Integer locationId = greenHouseMapper.getLocationIdByGreenHouseId(greenHouse.getId());
        if (locationService.hasLocation(userId, locationId)) {
            update(greenHouse);
        } else {
            throw new AuthorizationException("没有权限");
        }
    }

    @Override
    public Integer getCount() {
        return greenHouseMapper.getCount();
    }

    @Override
    public List<GreenHouse> getByLocationId(Integer locationId) {
        return greenHouseMapper.getAllByLocationId(locationId);
    }
}
