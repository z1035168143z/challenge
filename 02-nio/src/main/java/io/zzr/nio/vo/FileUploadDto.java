package io.zzr.nio.vo;

import lombok.Data;

/**
 * @author zrzhao
 * @date 2022/6/11
 */
@Data
public class FileUploadDto {

    /**
     * 文件唯一标识
     */
    private String md5Key;
    /**
     * 当前是第几分片
     */
    private int filePartNum;
    /**
     * 分片内容
     */
    private String base64Content;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * fileType
     */
    private String fileType;
    /**
     * 最后一个分片
     */
    private boolean lastPart;

}
