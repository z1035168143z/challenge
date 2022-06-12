package io.zzr.nio.async;

import io.zzr.nio.utils.FileUtils;
import io.zzr.nio.vo.ConcurrentDownloadDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author zrzhao
 * @date 2022/6/12
 */
@Slf4j
public class DownloadThread implements Runnable {

    private int partFileNum;
    private int partFileSize;
    private String downloadUrl;
    private boolean[] stop;
    private String md5Key;
    private String fileName;
    private String fileSavePath;

    public DownloadThread(int partFileNum, int partFileSize, ConcurrentDownloadDto concurrentDownloadDto, boolean[] stop, String md5Key) {
        this.partFileNum = partFileNum;
        this.partFileSize = partFileSize;
        this.downloadUrl = concurrentDownloadDto.getDownloadUrl();
        this.stop = stop;
        this.md5Key = md5Key;
        this.fileName = concurrentDownloadDto.getFileName();
        this.fileSavePath = concurrentDownloadDto.getSavePath();
    }

    @Override
    @SneakyThrows
    public void run() {
        Thread.currentThread().setName(fileName + "-下载线程-" + partFileNum);
        if (stop[0]) {
            return;
        }

        File partFile = new File(fileSavePath + File.separator + md5Key + File.separator + md5Key + '_' + partFileNum);
        if (partFile.exists()) {
            log.info("文件已存在，跳过");
            return;
        }

        URL partDownloadUrl = new URL(downloadUrl);
        HttpURLConnection partDownloadConn = (HttpURLConnection) partDownloadUrl.openConnection();
        partDownloadConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36");
        partDownloadConn.setRequestMethod("GET");
        partDownloadConn.setConnectTimeout(5000);
        partDownloadConn.setRequestProperty("Range", "bytes=" + (partFileNum - 1) * partFileSize + "-" + (partFileNum * partFileSize)); //固定写法，请求部分资源
        int partDownloadCode = partDownloadConn.getResponseCode();  // 206表示请求部分资源
        if (partDownloadCode != 206) {
            log.warn("分片下载响应码有误:{}", partDownloadCode);
            stop[0] = true;
            return;
        }
        if (stop[0]) {
            return;
        }

        FileUtils.createNewFile(fileSavePath + File.separator + md5Key, md5Key + '_' + partFileNum);

        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(partFile));
             BufferedInputStream inputStream = new BufferedInputStream(partDownloadConn.getInputStream())) {
            byte[] cache = new byte[1024 * 1024 * 5];
            int len;
            boolean empty = true;
            while ((len = inputStream.read(cache)) != -1) {
                bufferedOutputStream.write(cache, 0, len);
                empty = false;
            }
            if (empty) {
                log.info("分片下载返回内容为空，结束");
                stop[0] = true;
                return;
            }
            log.info("分片下载完成。fileSavePath:{}", partFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("分片下载出错。partFileNum:{}", partFileNum, e);
        }
    }
}
