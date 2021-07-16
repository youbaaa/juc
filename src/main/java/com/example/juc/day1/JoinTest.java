package com.example.juc.day1;
/**
 * join方法的作用是父线程等待子线程执行完成后再执行，换句话说就是将异步执行的线程合并为同步的线程。
 * JDK中提供三个版本的join方法，其实现与wait方法类似，join()方法实际上执行的join(0)，
 * 而join(long millis, int nanos)也与wait(long millis, int nanos)的实现方式一致，暂时对纳秒的支持也是不完整的。
 */

/**
 * @author : huang.zhangh
 * @Description: Join
 * @date Date : 2021-07-16 11:57 下午
 */
public class JoinTest implements Runnable {
    @Override
    public void run() {
        try {
            System.out.println(Thread.currentThread().getName() + " start-----");
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() + " end------");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            Thread test = new Thread(new JoinTest());
            test.start();
            try {
                test.join(); //调用join方法
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Finished~~~");
    }
}
