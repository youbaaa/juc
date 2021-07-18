package com.example.juc.day3;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author : huang.zhangh
 * @Description: FutureTask
 * @date Date : 2021-07-18 10:48 上午
 */

class MyThread1 implements Runnable {

    @Override
    public void run() {
        System.out.println("run");
    }
}

class MyThread2 implements Callable {

    @Override
    public Integer call() throws Exception {
        System.out.println("fs1 come in...");
        return 200;
    }
}

public class ThreadDemo1 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new Thread(new MyThread1(), "aa").start();
        FutureTask<Integer> integerFutureTask = new FutureTask<Integer>(new MyThread2());
        FutureTask<Integer> integerFutureTask1 = new FutureTask<>(() -> {

            System.out.println("fs2 come in...");
            return 1024;
        });
        integerFutureTask.run();
        integerFutureTask1.run();
        while (!integerFutureTask.isDone()) {
            System.out.println("fs1 wait");

        }
        while (!integerFutureTask.isDone()) {
            System.out.println("fs2 wait");
        }

        System.out.println(integerFutureTask.get());
        System.out.println(integerFutureTask1.get());

    }
}
