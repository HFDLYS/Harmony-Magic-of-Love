package com.hfdlys.harmony.magicoflove.database.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author Jiasheng Wang
 * @since 2024-07-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Log implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private String ip;

    private String content;

    private LocalDateTime createTime;


}
