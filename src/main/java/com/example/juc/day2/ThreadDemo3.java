package com.example.juc.day2;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author : huang.zhangh
 * @Description: 特定唤醒
 * @date Date : 2021-07-17 11:36 上午
 */
class ShareResource {
    //定义标志位
    private int flag = 1; //1 代表线程aa,2:bb,3:cc
    //创建锁
    private Lock lock = new ReentrantLock();
    //创建condition
    private Condition conditionA = lock.newCondition();
    private Condition conditionB = lock.newCondition();
    private Condition conditionC = lock.newCondition();

    //打印5次；参数第几轮；
    public void print5(int loop) throws InterruptedException {
        lock.lock();
        try {
            while (flag != 1) {
                conditionA.await();
            }
            for (int i = 1; i <= 5; i++) {
                System.out.println(Thread.currentThread().getName() + "::" + i + ":轮数：" + loop);
            }
            flag = 2;
            conditionB.signal();
        } finally {
            lock.unlock();
        }
    }

    //打印10次；参数第几轮；
    public void print10(int loop) throws InterruptedException {
        lock.lock();
        try {
            while (flag != 2) {
                conditionB.await();
            }
            for (int i = 1; i <= 10; i++) {
                System.out.println(Thread.currentThread().getName() + "::" + i + ":轮数：" + loop);
            }
            flag = 3;
            conditionC.signal();
        } finally {
            lock.unlock();
        }
    }

    //打印15次；参数第几轮；
    public void print15(int loop) throws InterruptedException {
        lock.lock();
        try {
            while (flag != 3) {
                conditionC.await();
            }
            for (int i = 1; i <= 15; i++) {
                System.out.println(Thread.currentThread().getName() + "::" + i + ":轮数：" + loop);
            }
            flag = 1;
            conditionA.signal();
        } finally {
            lock.unlock();
        }
    }
}

public class ThreadDemo3 {
    public static void main(String[] args) {
        ShareResource shareResource = new ShareResource();
        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    shareResource.print5(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "aa").start();
        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    shareResource.print10(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "bb").start();
        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    shareResource.print15(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "cc").start();
    }
}
