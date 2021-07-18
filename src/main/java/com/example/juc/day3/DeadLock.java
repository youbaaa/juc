package com.example.juc.day3;

import java.util.concurrent.TimeUnit;
/**
 * 查看死锁
 * 1，jps 查看进程号
 * 2，jstack pid 查看堆栈工具
 */

/**
 * @author : huang.zhangh
 * @Description: 死锁
 * @date Date : 2021-07-18 10:37 上午
 */
public class DeadLock {
    static Object a = new Object();
    static Object b = new Object();

    public static void main(String[] args) {
        new Thread(() -> {
            synchronized (a) {
                System.out.println(Thread.currentThread().getName() + "持有锁a，试图获取锁b");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (b) {
                    System.out.println(Thread.currentThread().getName() + "获取锁b");
                }
            }
        }, "A").start();

        new Thread(() -> {
            synchronized (b) {
                System.out.println(Thread.currentThread().getName() + "持有锁b，试图获取锁a");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (a) {
                    System.out.println(Thread.currentThread().getName() + "获取锁a");
                }
            }
        }, "B").start();
    }
}
