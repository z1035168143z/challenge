package io.zzr.nio.controller;

import io.zzr.nio.utils.Base64ToMultipart;
import io.zzr.nio.vo.FileUploadDto;
import io.zzr.nio.vo.FileUploadResult;
import io.zzr.nio.vo.JsonResultVo;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件
 *
 * @author zrzhao
 * @date 2022/6/11
 */
@RestController
@RequestMapping("fileSystem")
public class FileController {

    private static final String FILE_MAPPING_TEXT = "G:\\workspace\\fileSystem\\fileMapping.txt";
    private static final String FILE_TEMPORARY_FOLDER = "G:\\workspace\\fileSystem\\temporary\\";
    private static final String FILE_SAVE_PATH = "G:\\workspace\\fileSystem\\fullFile\\";
    private static BufferedWriter mappingFileWriter;
    private static final ConcurrentHashMap<String, String> fileMd5Mapping = new ConcurrentHashMap<>();


    @PostMapping("upload")
    public JsonResultVo<?> fileUpload(@RequestBody FileUploadDto fileUploadDto) {
        File fullFileName = new File(FILE_SAVE_PATH + fileUploadDto.getMd5Key() + "." + fileUploadDto.getFileType());
        if (fullFileName.exists()) {
            createFileMapping(fileUploadDto.getFileName(), fileUploadDto.getMd5Key());

            return JsonResultVo.buildSuccess(FileUploadResult.fileUploadComplete());
        }

        File temporaryFolder = new File(FILE_TEMPORARY_FOLDER + fileUploadDto.getMd5Key());
        if (!temporaryFolder.exists()) {
            temporaryFolder.mkdirs();
        }

        File partFile = new File(FILE_TEMPORARY_FOLDER + fileUploadDto.getMd5Key() + "\\" + fileUploadDto.getMd5Key() + "_" + fileUploadDto.getFilePartNum());
        if (!partFile.exists()) {
            MultipartFile multipartFile = Base64ToMultipart.base64ToMultipart(fileUploadDto.getBase64Content());
            if (multipartFile == null) {
                return JsonResultVo.buildError("未获取到文件");
            }
            try {
                multipartFile.transferTo(partFile);
            } catch (IOException e) {
                e.printStackTrace();
                return JsonResultVo.buildError("文件上传出错");
            }
        }
        if (fileUploadDto.isLastPart()) {
            String margeResult = margePartFile(fileUploadDto);
            if (StringUtils.isNotBlank(margeResult)) {
                return JsonResultVo.buildError(margeResult);
            }
            createFileMapping(fileUploadDto.getFileName(), fileUploadDto.getMd5Key());

            return JsonResultVo.buildSuccess(FileUploadResult.fileUploadComplete());
        }

        return JsonResultVo.buildSuccess(FileUploadResult.partUploadSuccess());
    }

    @SneakyThrows
    private String margePartFile(FileUploadDto fileUploadDto) {
        File temporaryFolder = new File(FILE_TEMPORARY_FOLDER + fileUploadDto.getMd5Key());
        if (!temporaryFolder.exists()) {
            return null;
        }
        File[] partFiles = temporaryFolder.listFiles();
        if (partFiles == null || partFiles.length == 0) {
            return null;
        }
        if (partFiles.length != fileUploadDto.getFilePartNum()) {
            return "文件损坏，请重新上传";
        }
        byte[] cache = new byte[1024 * 1024];
        int len;

        File fullFilePath = new File(FILE_SAVE_PATH);
        if (!fullFilePath.exists()) {
            fullFilePath.mkdirs();
        }
        File targetFile = new File(FILE_SAVE_PATH + fileUploadDto.getMd5Key() + "." + fileUploadDto.getFileType());
        targetFile.createNewFile();
        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            for (int i = 1; i <= fileUploadDto.getFilePartNum(); i++) {
                File partFile = new File(FILE_TEMPORARY_FOLDER + fileUploadDto.getMd5Key() + "\\" + fileUploadDto.getMd5Key() + "_" + i);
                try (FileInputStream fileInputStream = new FileInputStream(partFile)) {
                    while ((len = fileInputStream.read(cache))!=-1) {
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

    private void createFileMapping(String fileName, String md5Key) {
        try {
            mappingFileWriter.write(fileName + "=" + md5Key);
            mappingFileWriter.newLine();
            mappingFileWriter.flush();

            fileMd5Mapping.put(fileName, md5Key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void loadFileMapping() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_MAPPING_TEXT))) {
            mappingFileWriter = new BufferedWriter(new FileWriter(FILE_MAPPING_TEXT));

            String lineText;
            while (StringUtils.isNotBlank(lineText = reader.readLine())) {
                String[] split = lineText.split("=");
                fileMd5Mapping.put(split[0], split[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
