package com.example.juc.alg;

/**
 * @author : huang.zhangh
 * @Description: 大数据排序
 * @date Date : 2021-07-17 6:57 下午
 */

import java.util.Random;

/**
 * 问题：M（如10亿）个int整数，只有其中N个数重复出现过，读取到内存中并将重复的整数删除。<br/>
 * 使用位映射来进行海量数据的去重排序，原先一个元素用一个int现在只用一个bit， 内存占比4*8bit:1bit=32:1<br/>
 * 亦可用java语言提供的BitSet，不过其指定bit index的参数为int类型，因此在此例中将输入数转为bit index时对于较大的数会越界<br><br/>
 */
public class BigDataSort {
    //    private static final int CAPACITY = 1_000_000;// 数据容量 100w
    private static final int CAPACITY = 10;// 数据容量 100w

    public static void main(String[] args) {

        testMyFullBitMap();

    }

    private static void testMyFullBitMap() {
        MyFullBitMap ms = new MyFullBitMap();

        byte[] bytes = null;

        Random random = new Random();
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < CAPACITY; i++) {
            int num = random.nextInt();
            // System.out.println("读取了第 " + (i + 1) + "\t个数: " + num);
            bytes = ms.setBit(num);
        }
        long endTime = System.currentTimeMillis();
        System.out.printf("存入%d个数，用时%dms\n", CAPACITY, endTime - startTime);

        startTime = System.currentTimeMillis();
        ms.output(bytes);
        endTime = System.currentTimeMillis();
        System.out.printf("取出%d个数，用时%dms\n", CAPACITY, endTime - startTime);
    }
}

/**
 * int数据---- -2^31--------0-------------2^31-1
 * <p>
 * bit数组-----bit[0]-------bit[2^31]-----bit[2^32]
 * <p>
 * byte数组----byte[0]------byte[2^28]----byte[2^29]
 * <p>
 * <p>
 * 但如上定义的bit[]数组显然在计算机中是不存在的，所我们需要将其转化为 java 中的一个基本数据类型存储。显然，byte[] 是最好的选择。
 * <p>
 * 将其转化为byte[] 数组方案：
 * <p>
 * 自定义的映射关系表，每个bit对应一个 int 数值，将 int 的最大值，最小值与数组的最大最小索引相对应。从上图可以看出来 int 数值与bit索引相差 2^31次方。当然，你也可以定义其他的映射关系，只是注意不要发生数组越界的情况。
 * <p>
 * bit[]索引：由于最大值可能是2^32,故用long接收: long bitIndex = num + (1l << 31);
 * <p>
 * byte[]索引:  int index = (int) (bitIndex / 8);  ，在字节byte[index]中的具体位置：  int innerIndex = (int) (bitIndex % 8);
 * <p>
 * 更新值： dataBytes[index] = (byte) (dataBytes[index] | (1 << innerIndex));
 */


class MyFullBitMap {
    // 定义一个byte数组表示所有的int数据，一bit对应一个，共2^32b=2^29B=512MB
    private byte[] dataBytes = new byte[1 << 29];

    /**
     * 读取数据，并将对应数数据的 到对应的bit中，并返回byte数组
     *
     * @param num 读取的数据
     * @return byte数组 dataBytes
     */
    public byte[] setBit(int num) {

        /**
         * 将其转化为byte[] 数组方案：
         *
         * 自定义的映射关系表，每个bit对应一个 int 数值，鄙人将 int 的最大值，最小值与数组的最大最小索引相对应。
         * 从上图可以看出来 int 数值与bit索引相差 2^31次方。当然，你也可以定义其他的映射关系，只是注意不要发生数组越界的情况。由于最大值可能是2^32,故用long接收。
         *
         * long bitIndex = num + (1l << 31);
         */
        long bitIndex = num + (1l << 31); // 获取num数据对应bit数组（虚拟）的索引

        /**
         * 计算在转化为byte[]数组的索引，由于上面定义的bitIndex 索引是非负数，故无需引入位运算去符号。
         *
         *  int index = (int) (bitIndex / 8);
         */
        int index = (int) (bitIndex / 8); // bit数组（虚拟）在byte数组中的索引

        /**
         * 计算bitIndex 在byte[]数组索引index 中的具体位置。
         */
        int innerIndex = (int) (bitIndex % 8); // bitIndex 在byte[]数组索引index 中的具体位置

        System.out.println("byte[" + index + "] 中的索引：" + innerIndex + ",num:" + num);

        /**
         * 引入位运算将byte[]数组索引index 的各个位按权值相加
         */
        dataBytes[index] = (byte) (dataBytes[index] | (1 << innerIndex));
        return dataBytes;
    }

    /**
     * 输出数组中的数据
     *
     * @param bytes byte数组
     */
    public void output(byte[] bytes) {
        int count = 0;
        for (int i = 0; i < bytes.length; i++) {
            for (int j = 0; j < 8; j++) {
                if (((bytes[i]) & (1 << j)) != 0) {
                    count++;
                    int number = (int) ((((long) i * 8 + j) - (1l << 31)));
                    System.out.println("取出的第 " + count + "\t个数: " + number);
                }
            }
        }
    }
}