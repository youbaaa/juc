package com.example.juc.day3;

import java.util.concurrent.CountDownLatch;

/**
 * @author : huang.zhangh
 * @Description: CountDownLatch 减少计数
 * @date Date : 2021-07-18 11:11 上午
 * 同学走完了，班长锁门走人
 */
public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countdown = new CountDownLatch(6);
        for (int i = 1; i <= 6; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "号同学离开教室...");
                countdown.countDown();
            }, String.valueOf(i)).start();
        }
        countdown.await();
        System.out.println(Thread.currentThread().getName() + "班长锁门走人了");
    }
}
