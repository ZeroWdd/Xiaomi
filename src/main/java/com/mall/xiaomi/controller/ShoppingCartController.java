package com.mall.xiaomi.controller;

import com.mall.xiaomi.exception.ExceptionEnum;
import com.mall.xiaomi.exception.XmException;
import com.mall.xiaomi.pojo.Address;
import com.mall.xiaomi.pojo.ShoppingCart;
import com.mall.xiaomi.service.AddressService;
import com.mall.xiaomi.service.ShoppingCartService;
import com.mall.xiaomi.util.ResultMessage;
import com.mall.xiaomi.vo.CartVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: wdd
 * @Date: 2020-03-19 13:27
 * @Description:
 */
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {

        private static final Logger logger = LoggerFactory.getLogger(ShoppingCartController.class);

        @Autowired
        private ResultMessage resultMessage;
        @Autowired
        private ShoppingCartService cartService;
        @Autowired
        private AddressService addressService;
        @Autowired
        private RedisTemplate redisTemplate;

        /**
         * 获取购物车信息
         *
         * @param userId
         * @return
         */
        @GetMapping("/user/{userId}")
        public ResultMessage cart(@PathVariable String userId) {
                List<CartVo> carts = cartService.getCartByUserId(userId);
                resultMessage.success("001", carts);
                return resultMessage;
        }

        /**
         * 添加购物车
         *
         * @param productId
         * @param userId
         * @return
         */
        @PostMapping("/product/user/{productId}/{userId}")
        public ResultMessage cart(@PathVariable String productId, @PathVariable String userId) {
                CartVo cartVo = cartService.addCart(productId, userId);

                if (cartVo != null) {

                        if (cartVo.isUpdateNum()) {
                                resultMessage.success("002", "添加购物车成功", cartVo.getUpdateMessage());
                        }
                        resultMessage.success("001", "添加购物车成功", cartVo);
                } else {
                        resultMessage.success("002", "该商品已经在购物车，数量+1");
                }
                return resultMessage;
        }

        @PutMapping("/user/num/{cartId}/{userId}/{num}")
        public ResultMessage cart(@PathVariable String cartId, @PathVariable String userId, @PathVariable String num) {
                cartService.updateCartNum(cartId, userId, num);
                resultMessage.success("001", "更新成功");
                return resultMessage;
        }

        @DeleteMapping("/user/{cartId}/{userId}")
        public ResultMessage deleteCart(@PathVariable String cartId, @PathVariable String userId) {
                cartService.deleteCart(cartId, userId);
                resultMessage.success("001", "删除成功");
                return resultMessage;
        }

        @GetMapping("/address/user/{userId}")
        public ResultMessage getCartAddresses(@PathVariable String userId) {
                List<Address> addresses = addressService.getAddressByUserId(userId);
                resultMessage.success("001", addresses);
                return resultMessage;
        }

        @PostMapping("/address")
        public ResultMessage addCartAddress(@Validated(Address.AddGroup.class) @RequestBody Address address,
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

        @PutMapping("/address/default/{addressId}")
        public ResultMessage setCartDefaultAddress(@PathVariable String addressId,
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

        @DeleteMapping("/address/{addressId}")
        public ResultMessage deleteCartAddress(@PathVariable String addressId,
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
