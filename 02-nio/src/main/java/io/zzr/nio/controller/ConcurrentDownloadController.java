package io.zzr.nio.controller;

import io.zzr.nio.async.DownloadThread;
import io.zzr.nio.utils.DownloadThreadPool;
import io.zzr.nio.utils.FileUtils;
import io.zzr.nio.utils.JSONObject;
import io.zzr.nio.vo.ConcurrentDownloadDto;
import io.zzr.nio.vo.JsonResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author zrzhao
 * @date 2022/6/12
 */
@RequestMapping("concurrentDownload")
@Controller
@Slf4j
public class ConcurrentDownloadController {


    @PostMapping("download")
    @ResponseBody
    public JsonResultVo<?> concurrentDownload(@RequestBody ConcurrentDownloadDto concurrentDownloadDto) {
        if (StringUtils.isBlank(concurrentDownloadDto.getDownloadUrl())) {
            return JsonResultVo.buildError("下载链接不存在");
        }

        if (StringUtils.isBlank(concurrentDownloadDto.getSavePath())) {
            concurrentDownloadDto.setSavePath("G:\\迅雷下载");
        }

        if (StringUtils.isBlank(concurrentDownloadDto.getFileName())) {
            concurrentDownloadDto.setFileName(UUID.randomUUID().toString());
        }

        if (concurrentDownloadDto.getPartFileSizeMb() == null) {
            concurrentDownloadDto.setPartFileSizeMb(50);
        }

        concurrentDownloadDto.setDownloadUrl(URLDecoder.decode(concurrentDownloadDto.getDownloadUrl()));
        try {
            URL url = new URL(concurrentDownloadDto.getDownloadUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36");
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpStatus.OK.value() && responseCode != HttpStatus.PARTIAL_CONTENT.value()) {
                return JsonResultVo.buildError("连接失败");
            }
            String acceptRanges = conn.getHeaderField(HttpHeaders.ACCEPT_RANGES);
            if ("bytes".equals(acceptRanges)) {
                DownloadThreadPool.getInstance().execute(() -> {
                    try {
                        log.info("下载链接：{} 支持range请求。开始多线程下载...", concurrentDownloadDto.getDownloadUrl());
                        int contentLength = conn.getContentLength();
                        if (contentLength == -1) {
                            log.info("下载链接：{} 未声明length...", concurrentDownloadDto.getDownloadUrl());
                        }

                        int partFileSizeByte = concurrentDownloadDto.getPartFileSizeMb() * 1024 * 1024;
                        String md5key = MD5Encoder.encode((contentLength + concurrentDownloadDto.getFileName()).getBytes(StandardCharsets.UTF_8));

                        boolean[] stop = new boolean[]{false};
                        int partFileNum = 1;
                        while (true) {
                            if (stop[0]) {
                                break;
                            }
                            DownloadThreadPool.getInstance().execute(new DownloadThread(partFileNum++, partFileSizeByte, concurrentDownloadDto, stop, md5key));
                            TimeUnit.MILLISECONDS.sleep(50);
                        }
                        log.info("文件下载完成，开始校验完整性...");
                        File partFileFolder = new File(concurrentDownloadDto.getSavePath() + File.separator + md5key);
                        int localFileLength = 0;
                        for (File partFile : partFileFolder.listFiles()) {
                            localFileLength += partFile.length();
                        }
                        if (localFileLength < contentLength) {
                            log.info("文件下载不完整，请重试:{}", JSONObject.toJsonString(concurrentDownloadDto));
                            return;
                        }

                        FileUtils.margePartFile(concurrentDownloadDto.getSavePath() + File.separator + md5key, -1, concurrentDownloadDto.getSavePath(), concurrentDownloadDto.getFileName());
                    } catch (Exception e) {
                        log.error("多线程下载出错。param:{}", JSONObject.toJsonString(concurrentDownloadDto), e);
                    }
                });
            } else {
                DownloadThreadPool.getInstance().execute(() -> {
                    log.info("下载链接：{} 不支持range请求,acceptRanges:[{}]。开始单线程下载...", concurrentDownloadDto.getDownloadUrl(), acceptRanges);

                    FileUtils.createNewFile(concurrentDownloadDto.getSavePath(), concurrentDownloadDto.getFileName());
                    try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(concurrentDownloadDto.getSavePath() + File.separator + concurrentDownloadDto.getFileName()));
                         BufferedInputStream inputStream = new BufferedInputStream(conn.getInputStream())) {
                        byte[] cache = new byte[1024 * 1024 * 5];
                        int len;
                        int totalLen = 0;
                        long lastPrintStamp = System.currentTimeMillis();
                        while ((len = inputStream.read(cache)) != -1) {
                            totalLen += len;
                            bufferedOutputStream.write(cache, 0, len);
                            if (System.currentTimeMillis() - lastPrintStamp >= 1000) {
                                FileUtils.printProgress(conn.getContentLength(), totalLen, concurrentDownloadDto.getFileName());
                                lastPrintStamp = System.currentTimeMillis();
                            }
                        }
                        log.info("单线程下载完成。url:{},fileSavePath:{}", concurrentDownloadDto.getDownloadUrl(), concurrentDownloadDto.getSavePath() + File.separator + concurrentDownloadDto.getFileName());
                    } catch (IOException e) {
                        log.error("单线程下载出错。param:{}", JSONObject.toJsonString(concurrentDownloadDto), e);
                    }
                });
            }
        } catch (IOException e) {
            log.error("下载出错:{}", JSONObject.toJsonString(concurrentDownloadDto), e);
            return JsonResultVo.buildError(e.getMessage());
        }

        return JsonResultVo.buildSuccess("下载任务开始成功");
    }

}
