package io.zzr.concurrent.demo;

/**
 * @author zrzhao
 * @date 2023/1/2
 */
public class ThreadCountDemo {

    public static void main(String[] args) {
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup parent = threadGroup.getParent();

        parent.list();
    }

}
