package io.zzr.nio.vo;

import lombok.Data;

/**
 * @author zrzhao
 * @date 2022/6/11
 */
@Data
public class FileUploadResult {


    private boolean partUploadSuccess;
    /**
     * 文件上传完成
     */
    private boolean fileUploadComplete;

    public static FileUploadResult partUploadSuccess() {
        FileUploadResult fileUploadResult = new FileUploadResult();
        fileUploadResult.partUploadSuccess = true;

        return fileUploadResult;
    }

    public static FileUploadResult partUploadFail() {
        FileUploadResult fileUploadResult = new FileUploadResult();
        fileUploadResult.partUploadSuccess = false;

        return fileUploadResult;
    }

    public static FileUploadResult fileUploadComplete() {
        FileUploadResult fileUploadResult = new FileUploadResult();
        fileUploadResult.partUploadSuccess = true;
        fileUploadResult.fileUploadComplete = true;

        return fileUploadResult;
    }

}
