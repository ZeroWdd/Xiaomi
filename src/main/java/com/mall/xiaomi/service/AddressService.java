package com.mall.xiaomi.service;

import com.mall.xiaomi.exception.ExceptionEnum;
import com.mall.xiaomi.exception.XmException;
import com.mall.xiaomi.mapper.AddressMapper;
import com.mall.xiaomi.pojo.Address;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressMapper addressMapper;

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

    @Transactional
    public void addAddress(Address address) {
        try {
            if (address.getIsDefault() == 1) {
                resetDefaultAddress(address.getUserId());
            }
            int count = addressMapper.insert(address);
            if (count != 1) {
                throw new XmException(ExceptionEnum.ADD_ADDRESS_ERROR);
            }
        } catch (XmException e) {
            e.printStackTrace();
            throw new XmException(ExceptionEnum.ADD_ADDRESS_ERROR);
        }
    }

    @Transactional
    public void updateAddress(Address address) {
        try {
            if (address.getIsDefault() == 1) {
                resetDefaultAddress(address.getUserId());
            }
            int count = addressMapper.updateByPrimaryKeySelective(address);
            if (count != 1) {
                throw new XmException(ExceptionEnum.UPDATE_ADDRESS_ERROR);
            }
        } catch (XmException e) {
            e.printStackTrace();
            throw new XmException(ExceptionEnum.UPDATE_ADDRESS_ERROR);
        }
    }

    @Transactional
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

    private void resetDefaultAddress(Integer userId) {
        Address example = new Address();
        example.setUserId(userId);
        example.setIsDefault(1);
        Address defaultAddress = addressMapper.selectOne(example);
        if (defaultAddress != null) {
            defaultAddress.setIsDefault(0);
            addressMapper.updateByPrimaryKeySelective(defaultAddress);
        }
    }
}
