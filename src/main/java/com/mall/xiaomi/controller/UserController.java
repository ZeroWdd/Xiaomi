package com.mall.xiaomi.controller;

import com.mall.xiaomi.pojo.User;
import com.mall.xiaomi.service.UserService;
import com.mall.xiaomi.util.BeanUtil;
import com.mall.xiaomi.util.CookieUtil;
import com.mall.xiaomi.util.MD5Util;
import com.mall.xiaomi.util.ResultMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: wdd
 * @Date: 2020-03-19 13:27
 * @Description:
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private ResultMessage resultMessage;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param user
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/login")
    public ResultMessage login(@RequestBody User user, HttpServletRequest request, HttpServletResponse response) {
        user = userService.login(user);
        // 添加cookie，设置唯一认证
        String encode = MD5Util.MD5Encode(user.getUsername() + user.getPassword(), "UTF-8");
        // 进行加盐
        encode += "|" + user.getUserId() + "|" + user.getUsername() + "|";
        // 将encode放入redis中，用于认证
        try {
            redisTemplate.opsForHash().putAll(encode, BeanUtil.bean2map(user));
            redisTemplate.expire(encode, 30 * 60, TimeUnit.SECONDS); // 设置过期时间
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 将密码设为null,返回给前端
        user.setPassword(null);
        // TODO: 部署到linux中无法按cookie方式，只能当作参数返回        此处bug不知什么原因，只能这么写
        Map<String, Object> map = new HashMap<>();
        map.put("cookie", encode);
        map.put("user", user);
        resultMessage.success("001", "登录成功", map);
        return resultMessage;
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @PostMapping("/register")
    public ResultMessage register(@RequestBody User user) {
        userService.register(user);
        resultMessage.success("001", "注册成功");
        return resultMessage;
    }

    /**
     * 判断用户名是否已存在
     * @param username
     * @return
     */
    @GetMapping("/username/{username}")
    public ResultMessage username(@PathVariable String username) {
        userService.isUserName(username);
        resultMessage.success("001", "可注册");
        return resultMessage;
    }

    /**
     * 根据token获取用户信息
     * @param token
     * @return
     */
    @GetMapping("/token")
    public ResultMessage token(@CookieValue("XM_TOKEN") String token, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map map = redisTemplate.opsForHash().entries(token);
        // 可能map为空 ， 即redis中时间已过期，但是cookie还存在。
        // 这个时候应该删除cookie，让用户重新登录
        if (map.isEmpty()) {
            CookieUtil.delCookie(request, token);
            resultMessage.fail("002", "账号过期,请重新登录");
            return resultMessage;
        }

        redisTemplate.expire(token, 30 * 60, TimeUnit.SECONDS); // 设置过期时间
        User user = BeanUtil.map2bean(map, User.class);
        user.setPassword(null);
        resultMessage.success("001", user);
        return resultMessage;
    }

}
