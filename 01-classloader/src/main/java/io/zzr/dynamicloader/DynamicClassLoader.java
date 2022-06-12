package io.zzr.dynamicloader;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.jar.JarEntry;

/**
 * @author zrzhao
 * @date 2022/6/11
 */
@Slf4j
public class DynamicClassLoader extends URLClassLoader {

    private JarURLConnection jarURLConnection;

    public DynamicClassLoader() {
        super(new URL[]{}, null);
    }

    public void addJarUrl(URL url) {
        try {
            URLConnection urlConnection = url.openConnection();
            if (urlConnection instanceof JarURLConnection) {
                urlConnection.setUseCaches(true);
                jarURLConnection = (JarURLConnection) urlConnection;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        addURL(url);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            Enumeration<JarEntry> entries = jarURLConnection.getJarFile().entries();
            String path = name.replace('.', '/').concat(".xlass");
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String jarFilePath = jarEntry.getName();
                jarFilePath = jarFilePath.replace("\\", "/");
                if (!jarFilePath.equals(path)) {
                    continue;
                }
                InputStream inputStream = jarURLConnection.getJarFile().getInputStream(jarEntry);
                byte[] bytes = new byte[(int) jarEntry.getSize()];
                inputStream.read(bytes);
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = (byte) ~bytes[i];
                }
                return defineClass(name, bytes, 0, bytes.length);
            }
        } catch (Exception e) {
             e.printStackTrace();
        }
        return null;
    }

    public void unloadJarFile() {
        if (jarURLConnection == null) {
            return;
        }

        try {
            jarURLConnection.getJarFile().close();
            this.close();
            jarURLConnection = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
