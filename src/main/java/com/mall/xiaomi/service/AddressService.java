package com.mall.xiaomi.service;

import com.mall.xiaomi.exception.ExceptionEnum;
import com.mall.xiaomi.exception.XmException;
import com.mall.xiaomi.mapper.AddressMapper;
import com.mall.xiaomi.pojo.Address;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String DEFAULT_ADDRESS_LOCK_KEY = "address:default:lock:";
    private static final long LOCK_LEASE_TIME = 10;

    public List<Address> getAddressByUserId(String userId) {
        Address address = new Address();
        address.setUserId(Integer.parseInt(userId));
        List<Address> list = null;
        try {
            list = addressMapper.select(address);
            if (ArrayUtils.isEmpty(list.toArray())) {
                throw new XmException(ExceptionEnum.GET_ADDRESS_NOT_FOUND);
            }
        } catch (XmException e) {
            e.printStackTrace();
            throw new XmException(ExceptionEnum.GET_ADDRESS_ERROR);
        }
        return list;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void addAddress(Address address) {
        try {
            if (address.getIsDefault() == null) {
                address.setIsDefault(0);
            }
            
            if (address.getIsDefault() == 1) {
                setDefaultAddressSafely(address.getUserId(), () -> {
                    addressMapper.resetDefaultAddressByUserId(address.getUserId());
                    int count = addressMapper.insert(address);
                    if (count != 1) {
                        throw new XmException(ExceptionEnum.ADD_ADDRESS_ERROR);
                    }
                });
            } else {
                int count = addressMapper.insert(address);
                if (count != 1) {
                    throw new XmException(ExceptionEnum.ADD_ADDRESS_ERROR);
                }
            }
        } catch (XmException e) {
            e.printStackTrace();
            throw new XmException(ExceptionEnum.ADD_ADDRESS_ERROR);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void updateAddress(Address address) {
        try {
            if (address.getIsDefault() != null && address.getIsDefault() == 1) {
                setDefaultAddressSafely(address.getUserId(), () -> {
                    addressMapper.resetDefaultAddressByUserId(address.getUserId());
                    int count = addressMapper.updateByPrimaryKeySelective(address);
                    if (count != 1) {
                        throw new XmException(ExceptionEnum.UPDATE_ADDRESS_ERROR);
                    }
                });
            } else {
                int count = addressMapper.updateByPrimaryKeySelective(address);
                if (count != 1) {
                    throw new XmException(ExceptionEnum.UPDATE_ADDRESS_ERROR);
                }
            }
        } catch (XmException e) {
            e.printStackTrace();
            throw new XmException(ExceptionEnum.UPDATE_ADDRESS_ERROR);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void setDefaultAddressById(Integer addressId, Integer userId) {
        setDefaultAddressSafely(userId, () -> {
            addressMapper.resetDefaultAddressByUserId(userId);
            int count = addressMapper.setDefaultAddress(addressId, userId);
            if (count != 1) {
                throw new XmException(ExceptionEnum.UPDATE_ADDRESS_ERROR);
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteAddress(String addressId, String userId) {
        Address address = new Address();
        address.setId(Integer.parseInt(addressId));
        address.setUserId(Integer.parseInt(userId));
        try {
            int count = addressMapper.delete(address);
            if (count != 1) {
                throw new XmException(ExceptionEnum.DELETE_ADDRESS_ERROR);
            }
        } catch (XmException e) {
            e.printStackTrace();
            throw new XmException(ExceptionEnum.DELETE_ADDRESS_ERROR);
        }
    }

    private void setDefaultAddressSafely(Integer userId, Runnable action) {
        String lockKey = DEFAULT_ADDRESS_LOCK_KEY + userId;
        boolean locked = false;
        try {
            locked = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, "1", LOCK_LEASE_TIME, TimeUnit.SECONDS);
            
            if (locked) {
                action.run();
            } else {
                int retryCount = 0;
                while (retryCount < 3) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    locked = redisTemplate.opsForValue()
                            .setIfAbsent(lockKey, "1", LOCK_LEASE_TIME, TimeUnit.SECONDS);
                    if (locked) {
                        action.run();
                        return;
                    }
                    retryCount++;
                }
                throw new XmException(ExceptionEnum.UPDATE_ADDRESS_ERROR);
            }
        } finally {
            if (locked) {
                redisTemplate.delete(lockKey);
            }
        }
    }

    public Address getById(String addressId) {
        return addressMapper.selectByPrimaryKey(Integer.parseInt(addressId));
    }
}
