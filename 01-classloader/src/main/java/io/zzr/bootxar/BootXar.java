package io.zzr.bootxar;

import lombok.extern.slf4j.Slf4j;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/**
 * 打包 xar包
 *
 * @author zrzhao
 * @date 2022\\6\\10
 */
@Slf4j
public class BootXar {

    /**
     * xar生成存放路径
     */
    private final String XAR_TARGET_PATH = "01-classloader\\src\\main\\resources\\xar";
    /**
     * 转xlass
     */
    private final String XLASS_TARGET_PATH = "01-classloader\\src\\main\\resources\\xlass";
    /**
     * 编译后文件
     */
    private final String CLASS_TARGET_PATH = "01-classloader\\src\\main\\resources\\class";
    /**
     * 待编译文件
     */
    private final String JAVA_FILE_PATH = "01-classloader\\src\\main\\java\\io\\zzr\\timer";


    public static void main(String[] args) {
        BootXar bootXar = new BootXar();

        bootXar.compileJava();

        bootXar.convertClass2Xlass();

        bootXar.tarXar("reporter.xar");
    }

    /**
     * 打包为xar
     *
     * @param xarName 文件名称
     */
    private void tarXar(String xarName) {
        try {
            // xar生成路径
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(XAR_TARGET_PATH + "\\" + xarName));

            // 加入队列，递归压缩
            File xlassFileDirectory = new File(XLASS_TARGET_PATH);
            LinkedList<File> directories = new LinkedList<>();
            directories.add(xlassFileDirectory);
            while (!directories.isEmpty()) {
                File xlassFile = directories.poll();
                if (xlassFile.isDirectory()) {
                    // 添加进队列，继续递归
                    File[] listFiles = xlassFile.listFiles();
                    if (listFiles == null || listFiles.length == 0) {
                        continue;
                    }
                    directories.addAll(Arrays.asList(listFiles));
                } else {
                    // xlass文件路径 去除父目录，再去除开头的 \
                    String xlassFileName = xlassFile.getPath().replace(xlassFileDirectory.getPath(), "").substring(1);
                    // 添加到xar内
                    jarOutputStream.putNextEntry(new JarEntry(xlassFileName));
                    FileInputStream fis = new FileInputStream(xlassFile);
                    int readLen = 0;
                    byte[] bytes = new byte[1024];
                    while((readLen = fis.read(bytes)) != -1){
                        jarOutputStream.write(bytes,0, readLen);
                    }
                    fis.close();
                }
            }
            jarOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * class转xlass
     */
    private void convertClass2Xlass() {
        File classFileFolder = new File(CLASS_TARGET_PATH);
        LinkedList<File> directories = new LinkedList<>();
        directories.add(classFileFolder);

        while (!directories.isEmpty()) {
            File directoryFile = directories.poll();
            File[] files = directoryFile.listFiles();
            // 空文件夹
            if (files == null || files.length == 0) {
                continue;
            }

            for (File file : files) {
                if (file.isDirectory()) {
                    // 继续递归
                    directories.add(file);
                } else if (classFileFolder.getName().endsWith("class")) {
                    // class文件进行转换

                    try (FileInputStream fis = new FileInputStream(file)) {
                        if (file.length() > Integer.MAX_VALUE) {
                            log.warn("class 文件过大. fileName:{},length:{}", file.getName(), file.length());
                            continue;
                        }

                        // 转xlass
                        byte[] bytes = new byte[(int) file.length()];
                        fis.read(bytes);
                        for (int i = 0; i < bytes.length; i++) {
                            bytes[i] = (byte) ~bytes[i];
                        }

                        // 生成路径与文件名替换
                        String xlassFilePath = file.getPath().replace(CLASS_TARGET_PATH, XLASS_TARGET_PATH);
                        xlassFilePath = xlassFilePath.replace(".class", ".xlass");

                        // 创建生成路径文件夹
                        String xlassFileDir = xlassFilePath.substring(0, xlassFilePath.lastIndexOf("\\"));
                        File xlassFileDirFile = new File(xlassFileDir);
                        if (!xlassFileDirFile.exists()) {
                            xlassFileDirFile.mkdirs();
                        }
                        // 创建xlass文件
                        File xlassFile = new File(xlassFilePath);
                        xlassFile.createNewFile();
                        // 写入
                        FileOutputStream fos = new FileOutputStream(xlassFile);
                        fos.write(bytes);
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    /**
     * 编译java文件
     */
    private void compileJava() {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

        File javaFileFolder = new File(JAVA_FILE_PATH);
        File[] files = javaFileFolder.listFiles();
        if (files == null) {
            throw new RuntimeException("java file not find !");
        }
        for (File file : files) {
            javaCompiler.run(null, null, null, "-d", CLASS_TARGET_PATH, file.getPath());
        }
    }






}
