package com.mall.xiaomi.controller;

import com.mall.xiaomi.pojo.Address;
import com.mall.xiaomi.service.AddressService;
import com.mall.xiaomi.util.ResultMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

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
}
