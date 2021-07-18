package com.example.juc.day3;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author : huang.zhangh
 * @Description: CyclicBarrier 循环栅栏
 * @date Date : 2021-07-18 11:16 上午
 * 集齐7龙珠召唤神龙
 */
public class CyclicBarrierDemo {
    private static final int NUM = 7;

    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(NUM, () -> {
            System.out.println("集齐7颗龙珠就可以召唤神龙");
        });

        for (int i = 1; i <= 7; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "星龙珠被收集到了");
                try {
                    //等待
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }, String.valueOf(i)).start();
        }

    }
}
