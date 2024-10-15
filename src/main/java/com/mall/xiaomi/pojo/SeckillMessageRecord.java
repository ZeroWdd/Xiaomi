package com.mall.xiaomi.pojo;

import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * seckill_message_record
 */
@Data
@Table(name = "seckill_message_record")
public class SeckillMessageRecord implements Serializable {
    private Long id;

    private String messageId;

    private String userId;

    private String seckillId;

    private String status;

    private Integer retryCount;

    private String errorMessage;

    private Date createdAt;

    private Date updatedAt;

    private static final long serialVersionUID = 1L;
}
