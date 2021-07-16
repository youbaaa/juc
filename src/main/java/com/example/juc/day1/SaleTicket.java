package com.example.juc.day1;
//  1. 创建资源类，在资源类创建属性和方法；
//  2. 创建多个线程，调用资源类的操作方法；

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author : huang.zhangh
 * @Description: 卖票
 * @date Date : 2021-07-16 10:21 下午
 */
class Ticket {
    //创建可重入锁
    ReentrantLock lock = new ReentrantLock();
    //票数量
    private int number = 30;

    //操作方法
    public synchronized void sale() {
        // 判断是否有票
        if (number > 0) {
            System.out.println(Thread.currentThread().getName() + " ：当前余票：" + (number--) + " : 卖出：1张" + "剩下：" + number);
        }
    }

    // 利用Lock来锁定资源
    public void lockSale() {
        // 判断是否有票
        lock.lock();
        try {
            if (number > 0) {
                System.out.println(Thread.currentThread().getName() + " ：当前余票：" + (number--) + " : 卖出：1张" + "剩下：" + number);
            }
        } finally {
            lock.unlock();
        }

    }

}

public class SaleTicket {
    // 创建多个线程，调用资源类的操作方法
    public static void main(String[] args) {
        //创建资源类对象
        Ticket ticket = new Ticket();
        //创建3个线程代表3个售票员
        new Thread(() -> {
            for (int i = 0; i < 40; i++)
//                ticket.sale();
                ticket.lockSale();
        }, "aa").start();
        new Thread(() -> {
            for (int i = 0; i < 40; i++)
//                ticket.sale();
                ticket.lockSale();
        }, "bb").start();
        new Thread(() -> {
            for (int i = 0; i < 40; i++)
//                ticket.sale();
                ticket.lockSale();
        }, "cc").start();
    }
}
