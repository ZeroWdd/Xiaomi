package com.mall.xiaomi.controller;

import com.mall.xiaomi.exception.ExceptionEnum;
import com.mall.xiaomi.exception.XmException;
import com.mall.xiaomi.pojo.Address;
import com.mall.xiaomi.service.AddressService;
import com.mall.xiaomi.util.ResultMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);

    @Autowired
    private ResultMessage resultMessage;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private AddressService addressService;

    @GetMapping("/user/{userId}")
    public ResultMessage getAddress(@PathVariable String userId) {
        List<Address> addresses = addressService.getAddressByUserId(userId);
        resultMessage.success("001", addresses);
        return resultMessage;
    }

    @GetMapping("/{addressId}")
    public ResultMessage getAddressById(@PathVariable String addressId) {
        Address address = addressService.getById(addressId);
        resultMessage.success("001", address);
        return resultMessage;
    }

    @PostMapping("")
    public ResultMessage addAddress(@Validated(Address.AddGroup.class) @RequestBody Address address, 
                                     @CookieValue(value = "XM_TOKEN", required = false) String cookie) {
        if (cookie != null) {
            Integer userId = (Integer) redisTemplate.opsForHash().get(cookie, "userId");
            if (userId != null) {
                address.setUserId(userId);
            }
        }
        addressService.addAddress(address);
        resultMessage.success("001", "添加地址成功");
        return resultMessage;
    }

    @PutMapping("")
    public ResultMessage updateAddress(@Validated(Address.UpdateGroup.class) @RequestBody Address address, 
                                        @CookieValue(value = "XM_TOKEN", required = false) String cookie) {
        if (cookie != null) {
            Integer userId = (Integer) redisTemplate.opsForHash().get(cookie, "userId");
            if (userId != null) {
                address.setUserId(userId);
            }
        }
        addressService.updateAddress(address);
        resultMessage.success("001", "更新地址成功");
        return resultMessage;
    }

    @PutMapping("/default/{addressId}")
    public ResultMessage setDefaultAddress(@PathVariable String addressId,
                                            @CookieValue(value = "XM_TOKEN", required = false) String cookie) {
        Integer userId = null;
        if (cookie != null) {
            userId = (Integer) redisTemplate.opsForHash().get(cookie, "userId");
        }
        if (userId == null) {
            resultMessage.fail("002", "用户未登录");
            return resultMessage;
        }
        
        Integer addressIdInt = parseAddressId(addressId);
        addressService.setDefaultAddressById(addressIdInt, userId);
        resultMessage.success("001", "设置默认地址成功");
        return resultMessage;
    }

    @DeleteMapping("/{addressId}")
    public ResultMessage deleteAddress(@PathVariable String addressId, 
                                        @CookieValue(value = "XM_TOKEN", required = false) String cookie) {
        String userId = null;
        if (cookie != null) {
            Integer userIdInt = (Integer) redisTemplate.opsForHash().get(cookie, "userId");
            if (userIdInt != null) {
                userId = userIdInt.toString();
            }
        }
        addressService.deleteAddress(addressId, userId);
        resultMessage.success("001", "删除地址成功");
        return resultMessage;
    }

    private Integer parseAddressId(String addressId) {
        if (StringUtils.isBlank(addressId)) {
            logger.warn("Invalid addressId: {}", addressId);
            throw new XmException(ExceptionEnum.GET_ADDRESS_ERROR);
        }
        try {
            return Integer.parseInt(addressId);
        } catch (NumberFormatException e) {
            logger.warn("Failed to parse addressId: {}", addressId, e);
            throw new XmException(ExceptionEnum.GET_ADDRESS_ERROR);
        }
    }
}
