package io.zzr.dynamicloader;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * add VM options: -verbose:class -XX:+TraceClassLoading -XX:+TraceClassUnloading
 *
 *
 * @author zrzhao
 * @date 2022/6/11
 */
@Slf4j
public class DynamicClassLoadDemo {

    public static void main(String[] args) throws Exception {
        while (true) {
            String jarName = "reporter.xar";
            String xarClassPath = "src/main/resources/xar";
            String className = "io.zzr.timer.TimeReporter";

            // 加载jar包
            DynamicClassLoaderContext.loadJar(jarName, xarClassPath);

            // 加载jar包中的class
            Class<?> aClass = DynamicClassLoaderContext.loadClass(jarName, className);
            Method report = aClass.getMethod("report");
            report.invoke(aClass.newInstance());

            // 卸载
            DynamicClassLoaderContext.unloadJar(jarName);
            // unload class
            System.gc();
            TimeUnit.SECONDS.sleep(3);
        }
    }

}
