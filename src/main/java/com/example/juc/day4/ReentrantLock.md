## Lock接口

```java
public interface Lock {
    // 尝试去获得锁
    // 如果锁不可用，当前线程会变得不可用，直到获得锁为止。（中途会忽略中断）    
    void lock();
    
    // 尝试去获取锁，如果锁获取不到，线程将不可用
    // 直到获取锁，或者被其他线程中断
    // 线程在获取锁操作中，被其他线程中断，则会抛出InterruptedException异常，并且将中断标识清除。
    void lockInterruptibly() throws InterruptedException;
    
    // 锁空闲时返回true，锁不空闲是返回false
    // 该方法不会引起当前线程阻塞
    boolean tryLock();
    
    // 在unit时间内成功获取锁，返回true
    // 在unit时间内未成功获取锁，返回false
    // 如果当前线程在获取锁操作中，被其他线程中断，则会抛出InterruptedException异常，并且将中断标识清除。
    boolean tryLock(long time, TimeUnit unit) throws InterruptedException;
    
    // 释放锁
    void unlock();
    
    // 获取一个绑定到当前Lock对象的Condition对象
    // 获取Condition对象的前提是当前线程持有Lock对象
    Condition newCondition();
}
```
关于上面的lock()和lockInterruptibly()方法，有如下区别:

> lock()方法类似于使用synchronized关键字加锁，如果锁不可用，出于线程调度目的，将禁用当前线程，并且在获得锁之前，该线程将一直处于休眠状态。
lockInterruptibly()方法顾名思义，就是如果锁不可用，那么当前正在等待的线程是可以被中断的，这比synchronized关键字更加灵活。

Lock接口的经典用法

```java
Lock lock = new ReentrantLock();
//尝试获取锁，如果当前该锁没有被其他线程持有，则当前线程获取该锁并返回true，否则返回false。
//该方法不会引起当前线程阻塞
if (lock.tryLock()) {
    try {
        // manipulate protected state
    } finally {
        lock.unlock();
    }
} else {
    // perform alternative actions
}

```
或者
```java
lock.lock()
try {
    // manipulate protected state
} finally {
    lock.unlock();
}
```
**这边不要将获取锁的过程写在try块中，因为如果在获取锁（自定义锁的实现）时发生了异常，异常抛出的同时，也会导致锁无故释放。**

---

## ReentrantLock

> + ReentrantLock类是一个可重入的独占锁,除了具有和synchronized一样的功能外，还具有限时锁等待、锁中断和锁尝试等功能。
> + ReentrantLock底层是通过继承AQS来实现独占锁功能的。

### 公平锁

> 是指线程在抢占锁失败后会进入一个等待队列，先进入队列的线程会先获得锁。公平性体现在先来先得。

```java
public ReentrantLock() {
    sync = new NonfairSync();
}
//实现

 static final class FairSync extends Sync {
        private static final long serialVersionUID = -3000897897090466540L;

        final void lock() {
            acquire(1);
        }

    
        protected final boolean tryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (!hasQueuedPredecessors() &&
                    compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0)
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
    }


```


### 非公平锁
> 是指线程抢占锁失败后会进入一个等待队列，但是这些等待线程谁能先获得锁不是按照先来先得的规则，而是随机的。不公平性体现在后来的线程可能先得到锁。

```java
public ReentrantLock(boolean fair) {
    sync = fair ? new FairSync() : new NonfairSync();
}
//实现
static final class NonfairSync extends Sync {
    private static final long serialVersionUID = 7316153563782823691L;

    //如果没有线程占据锁，则占据锁，也就是将state从0设置为1
    //这种抢占方式不要排队，有人释放了锁，你可以直接插到第一位
    //去抢，只要你能抢到
    final void lock() {
        if (compareAndSetState(0, 1))
            setExclusiveOwnerThread(Thread.currentThread());
        else
        //否则尝试抢占锁
            acquire(1);
    }

    protected final boolean tryAcquire(int acquires) {
        return nonfairTryAcquire(acquires);
    }
}
    
final boolean nonfairTryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState();
    //锁已经被释放，则直接占据锁
    if (c == 0) {
        if (compareAndSetState(0, acquires)) {
            setExclusiveOwnerThread(current);
            return true;
        }
    }
    //否则判断锁是不是之前被自己占用过，并设置重入次数
    else if (current == getExclusiveOwnerThread()) {
        int nextc = c + acquires;
        if (nextc < 0) // overflow
            throw new Error("Maximum lock count exceeded");
        setState(nextc);
        return true;
    }
    return false;
}

```

> 如果有很多线程竞争一把公平锁，系统的总体吞吐量（即速度很慢，常常极其慢）比较低，因为此时在线程调度上面的开销比较大。
原因是采用公平策略时，当一个线程释放锁时，需要先将等待队列中的线程唤醒。这个唤醒的调度过程是比较耗费时间的。如果使用非公平锁的话，当一个线程释放锁之后，可用的线程能立马获得锁，效率较高。


