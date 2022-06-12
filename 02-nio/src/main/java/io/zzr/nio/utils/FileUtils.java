package io.zzr.nio.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author zrzhao
 * @date 2022/6/12
 */
@Slf4j
public class FileUtils {

    public static boolean mkdirs(String directory) {
        File directoryFile = new File(directory);
        if (directoryFile.exists()) {
            return true;
        }
        return directoryFile.mkdirs();
    }

    public static boolean createNewFile(String filePath) {
        if (filePath.contains(File.separator)) {
            mkdirs(filePath.substring(0, filePath.lastIndexOf(File.separator)));
        }

        try {
            return new File(filePath).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean createNewFile(String fileDirectory, String fileName) {
        mkdirs(fileDirectory);

        try {
            return new File(fileDirectory + File.separator + fileName).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void printProgress(int length, int currentLength, String keyWord) {
        String alreadyDownloadSize = new BigDecimal(currentLength).divide(new BigDecimal(1024 * 1024), 2, RoundingMode.HALF_UP) + "MB";
        if (length == -1 || length == 0) {
            log.info("文件：{} .已下载：{}.文件总大小：{}", keyWord,
                    alreadyDownloadSize,
                    length);
        } else {
            log.info("文件：{} .已下载：{}.文件总大小：{}.当前进度：{}", keyWord,
                    alreadyDownloadSize,
                    new BigDecimal(length).divide(new BigDecimal(1024 * 1024), 2, RoundingMode.HALF_UP) + "MB",
                    new BigDecimal(currentLength).divide(new BigDecimal(length), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)) + "%");
        }
    }

    @SneakyThrows
    public static String margePartFile(String partFileFolder, int totalFileNum, String margeFileFolder, String margeFileName) {
        File temporaryFolder = new File(partFileFolder);
        if (!temporaryFolder.exists()) {
            return null;
        }
        File[] partFiles = temporaryFolder.listFiles();
        if (partFiles == null || partFiles.length == 0) {
            return null;
        }
        if (totalFileNum != -1 && partFiles.length != totalFileNum) {
            return "文件损坏，请重新上传";
        }
        byte[] cache = new byte[1024 * 1024];
        int len;

        File fullFilePath = new File(margeFileFolder);
        if (!fullFilePath.exists()) {
            fullFilePath.mkdirs();
        }
        File targetFile = new File(margeFileFolder + File.separator + margeFileName);
        targetFile.createNewFile();
        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            for (File partFile : partFiles) {
                try (FileInputStream fileInputStream = new FileInputStream(partFile)) {
                    while ((len = fileInputStream.read(cache)) != -1) {
                        fos.write(cache, 0, len);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return "文件合并失败";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "文件合并失败";
        }
        partFiles = temporaryFolder.listFiles();
        if (partFiles != null && partFiles.length > 0) {
            for (File partFile : partFiles) {
                partFile.delete();
            }
        }
        temporaryFolder.delete();

        return null;
    }

}
