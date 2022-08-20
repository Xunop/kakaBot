package com.mybot.kakaBot.entity;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;

import java.util.List;

/**
 * @Author xun
 * @create 2022/5/1 12:17
 */
@Data
@TableName(value = "images", autoResultMap = true)
public class Image {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long pid;

    private Long uid;

    private Boolean isR18;

    private String title;

    private String author;

    private String url;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONArray tags;

    /**
     * 图片格式
     */
    private String ext;

    /**
     * 存到 bucket 中的名字
     * 确保不重名，所以选择使用 pid + 标题 + 图片格式 进行命名
     */
    private String fileName;
}
