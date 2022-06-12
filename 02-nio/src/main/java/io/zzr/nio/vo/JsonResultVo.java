package io.zzr.nio.vo;

import lombok.Data;

/**
 * @author zrzhao
 * @date 2022/6/11
 */
@Data
public class JsonResultVo<T> {

    private static int SUCCESS_CODE = 0;
    private static int ERROR_CODE = 1;

    private int code;
    private String message;
    private T data;

    public static <E> JsonResultVo<E> buildSuccess(E data) {
        JsonResultVo<E> jsonResultVo = new JsonResultVo<>();
        jsonResultVo.data = data;
        jsonResultVo.code = SUCCESS_CODE;

        return jsonResultVo;
    }

    public static <E> JsonResultVo<?> buildError(String message) {
        JsonResultVo<E> jsonResultVo = new JsonResultVo<>();
        jsonResultVo.message = message;
        jsonResultVo.code = ERROR_CODE;

        return jsonResultVo;
    }


}
