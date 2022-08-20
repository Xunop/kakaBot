package com.mybot.kakaBot.exception;

import com.mybot.kakaBot.enums.ErrorEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author xun
 * @create 2022/8/3 15:43
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class LocalRuntimeException extends RuntimeException{

    private ErrorEnum errorEnum;

    public LocalRuntimeException(String message) {
        super(message);
    }

    public LocalRuntimeException(ErrorEnum errorEnum) {
        super(errorEnum.getErrMsg());
        this.errorEnum = errorEnum;
    }
}
