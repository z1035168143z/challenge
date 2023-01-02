package io.zzr.concurrent.demo;

import java.util.concurrent.Semaphore;

/**
 * @author zrzhao
 * @date 2023/1/2
 */
public class AlternatePrintDemo {

    static volatile int start = 0;

    public static void main(String[] args) throws InterruptedException {
        Semaphore s1 = new Semaphore(1);
        Semaphore s2 = new Semaphore(0);

        Thread thread1 = new Thread(() -> {
            while (start < 100) {
                try {
                    s1.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + ":" + (++start));
                s2.release();
            }
        });
        Thread thread2 = new Thread(() -> {
            while (start < 100) {
                try {
                    s2.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + ":" + (++start));
                s1.release();
            }
        });

        thread1.setName("线程1");
        thread2.setName("线程2");
        thread1.start();
        thread2.start();
    }

}
