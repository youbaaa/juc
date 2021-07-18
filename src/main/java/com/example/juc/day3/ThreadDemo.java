package com.example.juc.day3;

/**
 * @author : huang.zhangh
 * @Description: synchronized锁的范围
 * @date Date : 2021-07-18 10:06 上午
 */

/**
 * 1.两个线程同时访问同一个对象的同步方法(锁this)
 * sendEmail...
 * sendMsg...
 * <p>
 * 2.两个线程同时访问两个对象的同步方法(锁this)
 * sendEmail...
 * sendMsg...
 * <p>
 * 3.两个线程同时访问（一个或两个）对象的静态同步方法(锁class)
 * sendEmail...
 * sendMsg...
 * <p>
 * 4.两个线程分别同时访问（一个或两个）对象的同步方法和非同步方法
 * sendEmail...
 * getHello...
 * <p>
 * 5.两个线程访问同一个对象中的同步方法，同步方法又调用一个非同步方法
 * sendEmail...
 * sendMsg...
 * getHello...
 * <p>
 * 6.两个线程同时访问同一个对象的不同的同步方法
 * sendEmail...
 * sendMsg...
 * <p>
 * 7.两个线程分别同时访问静态synchronized和非静态synchronized方法
 * sendEmail...
 * sendMsg...
 * <p>
 * 8.同步方法抛出异常后，JVM会自动释放锁的情况
 */
class Phone {
    public static synchronized void sendEmail() throws InterruptedException {
        System.out.println("sendEmail...");
        Thread.sleep(4000);
    }

    public synchronized void sendMsg() {
        System.out.println("sendMsg...");
//        getHello();
    }

    public void getHello() {
        System.out.println("getHello...");
    }
}

public class ThreadDemo {
    public static void main(String[] args) {
        Phone phone = new Phone();
        Phone phone1 = new Phone();
        new Thread(() -> {
            try {
                phone.sendEmail();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "aa").start();

        new Thread(() -> {
            phone.sendMsg();
        }, "bb").start();
    }
}
