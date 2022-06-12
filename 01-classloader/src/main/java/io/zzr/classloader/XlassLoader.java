package io.zzr.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * @author zrzhao
 * @date 2022/5/28
 */
public class XlassLoader extends ClassLoader {

    public static void main(String[] args) throws Exception {
        Class<?> helloClass = new XlassLoader().findClass("Hello");
        Method helloMethod = helloClass.getMethod("hello");
        helloMethod.invoke(helloClass.newInstance());
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        URL resource = getClass().getClassLoader().getResource(name + ".xlass");
        if (resource == null) {
            throw new ClassNotFoundException(name);
        }
        File xlassFile = new File(resource.getFile());
        try (FileInputStream fis = new FileInputStream(xlassFile)) {
            byte[] bytes = new byte[(int) xlassFile.length()];
            fis.read(bytes);
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) ~bytes[i];
            }

            return super.defineClass(name, bytes, 0, bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ClassNotFoundException(e.getMessage());
        }
    }





}
