package io.zzr.dynamicloader;

import io.zzr.dynamicloader.exception.XarNotDefineException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zrzhao
 * @date 2022/6/11
 */
public class DynamicClassLoaderContext {

    private static final ConcurrentHashMap<String, DynamicClassLoader> CLASS_LOADER_CACHE = new ConcurrentHashMap<>();


    public static void loadJar(String jarName, String xarPath) throws MalformedURLException {
        DynamicClassLoader dynamicClassLoader = CLASS_LOADER_CACHE.get(jarName);
        if (dynamicClassLoader != null) {
            return;
        }
        dynamicClassLoader = new DynamicClassLoader();

        URL url = new URL("jar:file:" + xarPath + "/" + jarName + "!/");
        dynamicClassLoader.addJarUrl(url);

        CLASS_LOADER_CACHE.put(jarName, dynamicClassLoader);
    }

    public static Class loadClass(String jarName, String className) throws ClassNotFoundException {
        DynamicClassLoader dynamicClassLoader = CLASS_LOADER_CACHE.get(jarName);
        if (dynamicClassLoader == null) {
            throw new XarNotDefineException(jarName);
        }
        return dynamicClassLoader.loadClass(className);
    }

    public static void unloadJar(String jarName) {
        DynamicClassLoader dynamicClassLoader = CLASS_LOADER_CACHE.get(jarName);
        if (dynamicClassLoader == null) {
            return;
        }
        dynamicClassLoader.unloadJarFile();
        CLASS_LOADER_CACHE.remove(jarName);
    }


}
