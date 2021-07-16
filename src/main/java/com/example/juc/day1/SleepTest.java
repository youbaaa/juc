package com.example.juc.day1;
/**
 * sleep方法的作用是让当前线程暂停指定的时间（毫秒），
 * sleep方法是最简单的方法，在上述的例子中也用到过，比较容易理解。
 * 唯一需要注意的是其与wait方法的区别。最简单的区别是，wait方法依赖于同步，而sleep方法可以直接调用。
 * 而更深层次的区别在于sleep方法只是暂时让出CPU的执行权，并不释放锁。而wait方法则需要释放锁。
 */

/**
 * @author : huang.zhangh
 * @Description: Sleep
 * @date Date : 2021-07-16 11:46 下午
 */
public class SleepTest {
    public synchronized void sleepMethod() {
        System.out.println("Sleep start-----");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Sleep end-----");
    }

    public synchronized void waitMethod() {
        System.out.println("Wait start-----");
        synchronized (this) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Wait end-----");
    }

    public static void main(String[] args) {
        final SleepTest test1 = new SleepTest();

        for (int i = 0; i < 3; i++) {
            new Thread(() -> test1.sleepMethod()).start();
        }


        try {
            Thread.sleep(10000);//暂停十秒，等上面程序执行完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("-----分割线-----");

        final SleepTest test2 = new SleepTest();

        for (int i = 0; i < 3; i++) {
            new Thread(() -> test2.waitMethod()).start();
        }

    }
}
