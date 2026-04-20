package com.mall.xiaomi.mapper;

import com.mall.xiaomi.pojo.Address;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface AddressMapper extends Mapper<Address> {

    int resetDefaultAddressByUserId(@Param("userId") Integer userId);

    int setDefaultAddress(@Param("addressId") Integer addressId, @Param("userId") Integer userId);
}
