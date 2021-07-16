package com.example.juc.day1;

/**
 * @author : huang.zhangh
 * @Description: 守护线程
 * @date Date : 2021-07-16 10:06 下午
 */
public class Main {
    public static void main(String[] args) {
        Thread aa = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "::" + Thread.currentThread().isDaemon());
            for (; ; ) {

            }
        }, "aa");
        // 设置守护线程
        aa.setDaemon(true);
        aa.start();
        System.out.println(Thread.currentThread().getName() + " over");
    }
}
