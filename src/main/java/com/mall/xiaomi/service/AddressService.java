package com.mall.xiaomi.service;

import com.mall.xiaomi.exception.ExceptionEnum;
import com.mall.xiaomi.exception.XmException;
import com.mall.xiaomi.mapper.AddressMapper;
import com.mall.xiaomi.pojo.Address;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
public class AddressService {

    private static final Logger logger = LoggerFactory.getLogger(AddressService.class);

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private static final String DEFAULT_ADDRESS_LOCK_KEY = "address:default:lock:";
    private static final long LOCK_LEASE_TIME = 10;
    private static final int MAX_RETRY_COUNT = 3;
    private static final long RETRY_INTERVAL_MS = 100;

    public List<Address> getAddressByUserId(String userId) {
        Address address = new Address();
        address.setUserId(parseInteger(userId, "userId"));
        List<Address> list = addressMapper.select(address);
        if (ArrayUtils.isEmpty(list.toArray())) {
            throw new XmException(ExceptionEnum.GET_ADDRESS_NOT_FOUND);
        }
        return list;
    }

    public void addAddress(Address address) {
        if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }

        if (address.getIsDefault() == 1) {
            executeWithLockAndTransaction(address.getUserId(), () -> {
                addressMapper.resetDefaultAddressByUserId(address.getUserId());
                int count = addressMapper.insert(address);
                if (count != 1) {
                    throw new XmException(ExceptionEnum.ADD_ADDRESS_ERROR);
                }
                return null;
            });
        } else {
            int count = addressMapper.insert(address);
            if (count != 1) {
                throw new XmException(ExceptionEnum.ADD_ADDRESS_ERROR);
            }
        }
    }

    public void updateAddress(Address address) {
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            executeWithLockAndTransaction(address.getUserId(), () -> {
                addressMapper.resetDefaultAddressByUserId(address.getUserId());
                int count = addressMapper.updateByPrimaryKeySelective(address);
                if (count != 1) {
                    throw new XmException(ExceptionEnum.UPDATE_ADDRESS_ERROR);
                }
                return null;
            });
        } else {
            int count = addressMapper.updateByPrimaryKeySelective(address);
            if (count != 1) {
                throw new XmException(ExceptionEnum.UPDATE_ADDRESS_ERROR);
            }
        }
    }

    public void setDefaultAddressById(Integer addressId, Integer userId) {
        if (addressId == null) {
            throw new XmException(ExceptionEnum.UPDATE_ADDRESS_ERROR);
        }
        executeWithLockAndTransaction(userId, () -> {
            addressMapper.resetDefaultAddressByUserId(userId);
            int count = addressMapper.setDefaultAddress(addressId, userId);
            if (count != 1) {
                throw new XmException(ExceptionEnum.UPDATE_ADDRESS_ERROR);
            }
            return null;
        });
    }

    public void deleteAddress(String addressId, String userId) {
        Address address = new Address();
        address.setId(parseInteger(addressId, "addressId"));
        address.setUserId(parseInteger(userId, "userId"));
        int count = addressMapper.delete(address);
        if (count != 1) {
            throw new XmException(ExceptionEnum.DELETE_ADDRESS_ERROR);
        }
    }

    private <T> T executeWithLockAndTransaction(Integer userId, Supplier<T> action) {
        String lockKey = DEFAULT_ADDRESS_LOCK_KEY + userId;
        boolean locked = false;
        try {
            locked = tryAcquireLock(lockKey);

            if (!locked) {
                locked = retryAcquireLock(lockKey);
            }

            if (!locked) {
                logger.warn("Failed to acquire lock for userId: {}, max retry exceeded", userId);
                throw new XmException(ExceptionEnum.UPDATE_ADDRESS_ERROR);
            }

            return transactionTemplate.execute(status -> {
                try {
                    return action.get();
                } catch (Exception e) {
                    status.setRollbackOnly();
                    throw e;
                }
            });

        } catch (XmException e) {
            logger.error("Address operation failed, userId: {}", userId, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during address operation, userId: {}", userId, e);
            throw new XmException(ExceptionEnum.UPDATE_ADDRESS_ERROR);
        } finally {
            if (locked) {
                releaseLock(lockKey);
            }
        }
    }

    private boolean tryAcquireLock(String lockKey) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, "1", LOCK_LEASE_TIME, TimeUnit.SECONDS));
        } catch (Exception e) {
            logger.error("Failed to acquire lock: {}", lockKey, e);
            return false;
        }
    }

    private boolean retryAcquireLock(String lockKey) {
        int retryCount = 0;
        while (retryCount < MAX_RETRY_COUNT) {
            try {
                Thread.sleep(RETRY_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Thread interrupted while waiting for lock: {}", lockKey, e);
                return false;
            }

            if (tryAcquireLock(lockKey)) {
                return true;
            }
            retryCount++;
            logger.debug("Retry {} acquiring lock: {}", retryCount, lockKey);
        }
        return false;
    }

    private void releaseLock(String lockKey) {
        try {
            redisTemplate.delete(lockKey);
        } catch (Exception e) {
            logger.error("Failed to release lock: {}", lockKey, e);
        }
    }

    private Integer parseInteger(String value, String fieldName) {
        if (StringUtils.isBlank(value)) {
            logger.warn("Invalid {} value: {}", fieldName, value);
            throw new XmException(ExceptionEnum.GET_ADDRESS_ERROR);
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("Failed to parse {}: {}", fieldName, value, e);
            throw new XmException(ExceptionEnum.GET_ADDRESS_ERROR);
        }
    }

    public Address getById(String addressId) {
        return addressMapper.selectByPrimaryKey(parseInteger(addressId, "addressId"));
    }
}
