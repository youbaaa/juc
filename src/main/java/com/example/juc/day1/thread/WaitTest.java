package com.example.juc.day1.thread;

/**
 * @author : huang.zhangh
 * @Description: Wait
 * @date Date : 2021-07-16 11:34 下午
 */
public class WaitTest {
    public void noMonitorTestWait() {
        System.out.println("start-------");
        try {
            wait(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end-------");
    }

    public synchronized void testWait() {
        System.out.println("start-------");
        try {
            wait(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end-------");
    }

    public static void main(String[] args) {
        WaitTest test = new WaitTest();
        new Thread(() -> {
//            test.noMonitorTestWait();
            test.testWait();
        }).start();
    }
}
