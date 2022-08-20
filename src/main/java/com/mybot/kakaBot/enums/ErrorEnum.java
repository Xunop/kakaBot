package com.mybot.kakaBot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author xun
 * @create 2022/8/3 15:40
 */
@Getter
@AllArgsConstructor
public enum ErrorEnum {

    COM_ERROR(1000, "错误"),
    GET_OBJECT_ERROR(2001, "取出图片错误"),
    IMAGE_NOT_EXIST(3001, "图片不存在"),
    STREAM_ERROR(4001, "流异常");
    private final Integer errCode;
    private final String errMsg;
}
