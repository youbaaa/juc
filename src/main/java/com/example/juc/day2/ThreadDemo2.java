package com.example.juc.day2;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author : huang.zhangh
 * @Description: lock
 * @date Date : 2021-07-17 11:17 上午
 */
class Share1 {
    private int num = 0;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void lIncr() throws InterruptedException {
        lock.lock();
        try {
            //判断
            while (num != 0) {
                condition.await();
            }
            //干活
            num++;
            System.out.println(Thread.currentThread().getName() + "::" + num);
            //通知
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void lDecr() throws InterruptedException {
        lock.lock();
        try {
            //判断
            while (num != 1) {
                condition.await();
            }
            //干活
            num--;
            System.out.println(Thread.currentThread().getName() + "::" + num);
            //通知
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}

public class ThreadDemo2 {
    public static void main(String[] args) {
        //创建多个线程，调用资源类的操作方法
        Share1 share = new Share1();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    share.lIncr();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "aa").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    share.lDecr();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "bb").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    share.lIncr();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "cc").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    share.lDecr();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "dd").start();

    }
}
