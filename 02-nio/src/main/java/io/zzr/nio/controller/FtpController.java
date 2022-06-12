package io.zzr.nio.controller;

import io.zzr.nio.vo.JsonResultVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ftpController
 *
 * @author zrzhao
 * @date 2022/6/11
 */
@RequestMapping("ftp")
@Controller
public class FtpController {

    @GetMapping("main")
    public ModelAndView main() {
        ModelAndView modelAndView = new ModelAndView("ftp");
        modelAndView.addObject("pwd", "G:/workspace/codes");

        return modelAndView;
    }

    @GetMapping("cd")
    @ResponseBody
    public JsonResultVo<?> changeDirectory(@RequestParam String baseDirectory, @RequestParam String targetDirectory) {
        if (StringUtils.isAnyBlank(baseDirectory, targetDirectory)) {
            return JsonResultVo.buildError("参数有误");
        }
        String targetPath;
        if (targetDirectory.startsWith("/")) {
            targetPath = targetDirectory;
        } else {
            if ("../".equals(targetDirectory)) {
                if (baseDirectory.contains("/")) {
                    targetPath = baseDirectory.substring(0, baseDirectory.lastIndexOf("/"));
                } else {
                    targetPath = baseDirectory + '/' + targetDirectory;
                }
            } else if ("./".equals(targetDirectory)) {
                targetPath = baseDirectory;
            } else {
                targetPath = baseDirectory + '/' + targetDirectory;
            }
        }

        File targetPathFile = new File(targetPath);
        if (!targetPathFile.exists()) {
            return JsonResultVo.buildError("没有那个文件或目录");
        }
        return JsonResultVo.buildSuccess(targetPathFile.getPath().replace("\\", "/"));
    }

    @GetMapping("ls")
    @ResponseBody
    public JsonResultVo<?> list(@RequestParam String baseDirectory) {
        if (StringUtils.isAnyBlank(baseDirectory)) {
            return JsonResultVo.buildError("参数有误");
        }

        File baseDirectoryFile = new File(baseDirectory);
        if (!baseDirectoryFile.exists()) {
            return JsonResultVo.buildError("目录不存在");
        }
        File[] listFiles = baseDirectoryFile.listFiles();
        if (listFiles == null || listFiles.length == 0) {
            return JsonResultVo.buildSuccess(Collections.emptyList());
        }
        List<String> fileNames = Arrays.stream(listFiles).map(File::getName).collect(Collectors.toList());
        return JsonResultVo.buildSuccess(fileNames);
    }

}
