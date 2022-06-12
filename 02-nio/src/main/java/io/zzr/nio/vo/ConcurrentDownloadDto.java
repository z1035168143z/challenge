package io.zzr.nio.vo;

import lombok.Data;

/**
 * @author zrzhao
 * @date 2022/6/12
 */
@Data
public class ConcurrentDownloadDto {

    private String downloadUrl;

    private String savePath;

    private String fileName;

    private Integer partFileSizeMb;

}
