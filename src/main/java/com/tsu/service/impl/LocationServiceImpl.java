package com.tsu.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tsu.entity.GreenHouse;
import com.tsu.entity.Location;
import com.tsu.mapper.GreenHouseMapper;
import com.tsu.mapper.LocationMapper;
import com.tsu.service.GreenHouseService;
import com.tsu.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

/**
 * @author zzz
 */
@Service
public class LocationServiceImpl implements LocationService {
    @Autowired
    LocationMapper locationMapper;

    @Autowired
    GreenHouseService greenHouseService;

    @Autowired
    DataSourceTransactionManager transactionManager;

    @Override
    public PageInfo<Location> getAllByUserIdAndPage(Integer page, Integer size, Integer userId) {
        PageHelper.startPage(page, size);
        List<Location> locationList = locationMapper.getAllByUserId(userId);
        return new PageInfo<>(locationList);
    }

    @Override
    public List<Location> getAllAndCollectionByPageAndUserId(Integer page, Integer size, Integer userId) {
        PageHelper.startPage(page, size);
        return locationMapper.getAllCollectionByUserId(userId);
    }

    @Override
    public void add(Location location) {
        locationMapper.save(location);
    }

    @Override
    public boolean hasLocation(Integer userId, Integer locationId) {
        Integer total = locationMapper.hasLocation(userId, locationId);
        return total != null && total > 0;
    }

    @Override
    public void distributeLocation(Integer userId, Integer locationId) {
        if (!hasByUserIdAndLocationId(userId, locationId)) {
            locationMapper.distributeLocation(userId, locationId);
        }
    }

    private boolean hasByUserIdAndLocationId(Integer userId, Integer locationId) {
        int count = locationMapper.getCountUserIdAndLocationId(userId, locationId);
        return count > 0;
    }

    @Override
    public PageInfo<Location> getAll(Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<Location> locationList = locationMapper.getAll();
        return new PageInfo<>(locationList);
    }

    @Override
    public List<Location> getAllAndCollection(Integer page, Integer size) {
        PageHelper.startPage(page, size);
        return locationMapper.getAllAndCollection();
    }

    @Override
    public List<Location> getAll() {
        return locationMapper.getAll();
    }

    @Override
    public List<Location> getAllByUserId(Integer userId) {
        return locationMapper.getAllByUserId(userId);
    }

    @Override
    public void deleteLocationUserRelation(Integer userId, Integer locationId) {
        locationMapper.deleteRelationByUserIdAndLocationId(userId, locationId);
    }

    @Override
    public List<Location> getAllAndCollection() {
        return locationMapper.getAllAndCollection();
    }

    @Override
    public List<Location> getAllAndCollectionByUserId(Integer userId) {
        return locationMapper.getAllCollectionByUserId(userId);
    }

    @Override
    public List<Integer> getIdsByUserId(Integer userId) {
        return locationMapper.getIdsByUserId(userId);
    }

    @Override
    public void update(Location location) {
        locationMapper.update(location);
    }


    @Transactional
    @Override
    public void delete(Integer locationId) {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setName("locationService.delete");
        transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(transactionDefinition);
        try {
            locationMapper.delete(locationId);
            locationMapper.deleteRelationByLocationId(locationId);
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    @Override
    public Integer getCount() {
        return locationMapper.getCount();
    }

    @Override
    public Location getByGreenHouseId(Integer greenHouseId) {
        return locationMapper.getByGreenHouseId(greenHouseId);
    }
}
