package com.example.juc.day1.thread;
/**
 * 调用notify方法时只有线程Thread-0被唤醒，但是调用notifyAll时，所有的线程都被唤醒了。
 * <p>
 * 最后，有两点点需要注意：
 * <p>
 * 　　（1）调用wait方法后，线程是会释放对monitor对象的所有权的。
 * <p>
 * 　　（2）一个通过wait方法阻塞的线程，必须同时满足以下两个条件才能被真正执行：
 * <p>
 * 　　　　线程需要被唤醒（超时唤醒或调用notify/notifyall）。
 * 　　　　线程唤醒后需要竞争到锁（monitor）。
 */

/**
 * @author : huang.zhangh
 * @Description: Notify
 * @date Date : 2021-07-16 11:38 下午
 */
public class NotifyTest {
    public synchronized void testWait() {
        System.out.println(Thread.currentThread().getName() + " Start-----");
        try {
            wait(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " End-----");
    }

    public static void main(String[] args) throws InterruptedException {
        NotifyTest test = new NotifyTest();
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                test.testWait();
            }).start();
        }
        synchronized (test) {
            test.notify();
        }

        Thread.sleep(3000);

        System.out.println("-----------分割线-------------");

        synchronized (test) {
            test.notifyAll();
        }
    }
}
