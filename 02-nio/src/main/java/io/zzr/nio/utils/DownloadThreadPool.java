package io.zzr.nio.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zrzhao
 * @date 2022/6/12
 */
public class DownloadThreadPool {

    public static DownloadThreadPool getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private final ThreadPoolExecutor threadPoolExecutor;

    private static final AtomicInteger threadNum = new AtomicInteger();
    private DownloadThreadPool() {
        threadPoolExecutor = new ThreadPoolExecutor(10, 50,
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(200),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    private static class SingletonHolder {
        private static final DownloadThreadPool INSTANCE = new DownloadThreadPool();
    }

    public void execute(Runnable runnable) {
        threadPoolExecutor.execute(runnable);
    }

}
