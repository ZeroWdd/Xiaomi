# 仿小米商城

## 前言

本项目是基于GitHub上一个作者[hai-27](https://github.com/hai-27)开发的补充。他是使用vue、node.js做的前后端分离项目。

我是对他的前端进行采用并进行修改，后端使用SpringBoot框架实现的一个仿小米商城。

### 感谢 [hai-27](https://github.com/hai-27) 的开源 [vue-store](https://github.com/hai-27/vue-store) 项目提供前端页面及框架支持


## 项目简介

本项目前后端分离，前端基于`Vue`+`Vue-router`+`Vuex`+`Element-ui`+`Axios`，参考小米商城实现。后端基于`SpringBoot` +`Redis`+ `RabbitMQ` + `MySQL`实现。

实现了**用户注册与登录**，**商城首页展示**，**商品分类展示**，**商品详情页**，**购物车**，**订单结算**，**我的收藏**等功能。

并在原作者的基础上添加了**商品秒杀**部分。

**后端接口全部采用Resultful风格，因此前端接口以及部分内容也有修改。**

前端项目地址：https://github.com/ZeroWdd/vue-store

后端项目地址：https://github.com/ZeroWdd/Xiaomi

## 项目已部署

仿小米商城： http://47.95.254.97:8080/

## 技术栈

- **前端：**`Vue`+`Vue-router`+`Vuex`+`Element-ui`+`Axios`
- **后端：**`SpringBoot` +`Redis`+ `RabbitMQ`
- **数据库：**`Mysql`

## 功能实现

- [x] 用户注册与登录
- [x] 商品首页展示
- [x] 商品分类列表展示
- [x] 商品详情页
- [x] 购物车
- [x] 订单结算
- [x] 我的收藏
- [ ] 我的地址
- [x] 秒杀商品
- [ ] 商品支付

## 运行项目

**前端运行**

```
1. Clone project

git clone https://github.com/ZeroWdd/vue-store.git

2. Project setup

cd vue-store
npm install

3. Compiles and hot-reloads for development

npm run serve

4. Compiles and minifies for production

npm run build
```
**后端运行**

```
1. 修改application.yml文件中的mysql、redis、rabbitmq的地址
2. 运行SpringBoot项目
```

## 页面截图

**首页**

![](https://images.gitee.com/uploads/images/2020/0317/154615_cffbacfe_6502229.png "home.png")

**全部商品**

![](https://images.gitee.com/uploads/images/2020/0317/154637_fa50ca7a_6502229.png "goods.png")

**购物车**

![](https://images.gitee.com/uploads/images/2020/0317/154737_f0417e36_6502229.gif "shoppingCart.gif")

**我的收藏**

![](https://images.gitee.com/uploads/images/2020/0317/154717_e2baa55c_6502229.png "collect.png")

**我的订单**

![](https://images.gitee.com/uploads/images/2020/0317/154756_5813ae89_6502229.png "order.png")

**登录**

![](https://images.gitee.com/uploads/images/2020/0317/154814_27bcc9f1_6502229.png "login.png")

**注册**

![](https://images.gitee.com/uploads/images/2020/0317/154827_2399157d_6502229.png "register.png")


