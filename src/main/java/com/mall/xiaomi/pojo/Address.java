package com.mall.xiaomi.pojo;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "JDBC")
    private Integer id;

    @NotNull(message = "用户ID不能为空", groups = {UpdateGroup.class, AddGroup.class})
    private Integer userId;

    @NotBlank(message = "收货人姓名不能为空", groups = {AddGroup.class})
    @Size(max = 50, message = "收货人姓名不能超过50个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String name;

    @NotBlank(message = "手机号不能为空", groups = {AddGroup.class})
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确", groups = {AddGroup.class, UpdateGroup.class})
    private String phone;

    @Size(max = 50, message = "省份不能超过50个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String province;

    @Size(max = 50, message = "城市不能超过50个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String city;

    @Size(max = 50, message = "区/县不能超过50个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String district;

    @NotBlank(message = "详细地址不能为空", groups = {AddGroup.class})
    @Size(max = 200, message = "详细地址不能超过200个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String detail;

    private Integer isDefault;

    public interface AddGroup {}
    public interface UpdateGroup {}
}
