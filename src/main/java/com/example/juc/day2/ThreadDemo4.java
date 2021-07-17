package com.example.juc.day2;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author : huang.zhangh
 * @Description: 集合不安全及解决方案
 * @date Date : 2021-07-17 11:49 上午
 */
public class ThreadDemo4 {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        Vector<String> vector = new Vector<>();
        List<String> list1 = Collections.synchronizedList(new ArrayList<>());
        CopyOnWriteArrayList<String> copyOnWriteArrayList = new CopyOnWriteArrayList<>(); //写时复制技术
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                // Exception in thread "3" java.util.ConcurrentModificationException
//                list.add(UUID.randomUUID().toString().substring(0, 8));
//                vector.add(UUID.randomUUID().toString().substring(0, 8));
//                list1.add(UUID.randomUUID().toString().substring(0, 8));
                copyOnWriteArrayList.add(UUID.randomUUID().toString().substring(0, 8));

//                System.out.println(list);
//                System.out.println(vector);
//                System.out.println(list1);
                System.out.println(copyOnWriteArrayList);
            }, String.valueOf(i)).start();
        }
    }
}
