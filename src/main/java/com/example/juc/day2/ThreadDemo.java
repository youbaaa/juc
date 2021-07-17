package com.example.juc.day2;

/**
 * @author : huang.zhangh
 * @Description: 虚假唤醒 判断需要放到while循环里，因为wait在哪里睡在哪里醒
 * @date Date : 2021-07-17 10:58 上午
 */
//创建资源类，定义属性 和方法
class Share {
    //初始值
    private int num = 0;

    //+1 方法
    public synchronized void sIncr() throws InterruptedException {
        // 判断、干活、通知
//        if (num != 0) {
//            this.wait();
//        }
        // if判断，多线程情况下会出现虚假唤醒的操作
        while (num != 0) {
            this.wait(); //在哪里睡就会在哪里醒
        }
        // 如果是0 就+1
        num++;
        System.out.println(Thread.currentThread().getName() + "::" + num);
        this.notifyAll();
    }

    //-1 方法
    public synchronized void sDecr() throws InterruptedException {
//        if (num != 1) {
//            this.wait();
//        }
        while (num != 1) {
            this.wait();
        }
        num--;
        System.out.println(Thread.currentThread().getName() + "::" + num);
        this.notifyAll();
    }
}

public class ThreadDemo {
    public static void main(String[] args) {
        //创建多个线程，调用资源类的操作方法
        Share share = new Share();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    share.sIncr();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "aa").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    share.sDecr();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "bb").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    share.sIncr();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "cc").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    share.sDecr();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "dd").start();

    }
}
