> ReentrantReadWriteLock 基于AQS实现的，它的自定义同步器（继承AQS）需要在同步状态（一个整型变量state）上维护多个读线程和一个写线程的状态，使得该状态的设计成为读写锁实现的关键。如果在一个整型变量上维护多种状态，就一定需要“按位切割使用”这个变量，读写锁将变量切分成了两个部分，高16位表示读，低16位表示写。
>
> ReentrantReadWriteLock支持以下功能：
>1. 支持公平和非公平的获取锁的方式；
>2. 支持可重入。读线程在获取了读锁后还可以获取读锁；写线程在获取了写锁之后既可以再次获取写锁又可以获取读锁；
>3. 还允许从写入锁降级为读取锁，其实现方式是：先获取写入锁，然后获取读取锁，最后释放写入锁。但是，从读取锁升级到写入锁是不允许的；
>4. 读取锁和写入锁都支持锁获取期间的中断；
>5. Condition支持。仅写入锁提供了一个 Conditon 实现；读取锁不支持 Conditon ，readLock().newCondition() 会抛出 UnsupportedOperationException。

```java
    abstract static class Sync extends AbstractQueuedSynchronizer {
    private static final long serialVersionUID = 6317671515068378041L;
    static final int SHARED_SHIFT = 16;
    static final int SHARED_UNIT = (1 << SHARED_SHIFT);
    static final int MAX_COUNT = (1 << SHARED_SHIFT) - 1;
    static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;

    static int sharedCount(int c) {
        return c >>> SHARED_SHIFT;
    }

    static int exclusiveCount(int c) {
        return c & EXCLUSIVE_MASK;
    }
}
```

ReentrantReadWriteLock含有两把锁readerLock和writerLock，其中ReadLock和WriteLock都是内部类。

+ WriterLock

1. 获取锁

```java
    public static class WriteLock implements Lock, java.io.Serializable {
    private static final long serialVersionUID = -4992448646407690164L;

    private final Sync sync;

    protected WriteLock(ReentrantReadWriteLock lock) {
        sync = lock.sync;
    }

    //获取写锁
    public void lock() {
        sync.acquire(1);
    }

    //AQS实现的独占式获取同步状态方法
    public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
                acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }

    //Sync中自定义重写的tryAcquire方法
    protected final boolean tryAcquire(int acquires) {
        /*
         *  1. 如果读取计数非零或写入计数非零且所有者是不同的线程，则失败。
         *  2. 如果计数会饱和，则失败。 （这只会在 count 已经非零时发生。）
         *  3. 否则，如果该线程是可重入获取或队列策略允许，则该线程有资格获得锁定。如果是，请更新状态并设置所有者。
         */
        Thread current = Thread.currentThread();
        int c = getState();
        int w = exclusiveCount(c);
        if (c != 0) {
            // (Note: if c != 0 and w == 0 then shared count != 0)
            if (w == 0 || current != getExclusiveOwnerThread())
                return false;
            if (w + exclusiveCount(acquires) > MAX_COUNT)
                throw new Error("Maximum lock count exceeded");
            // Reentrant acquire
            setState(c + acquires);
            return true;
        }
        //FairSync中需要判断是否有前驱节点，如果有则返回false，否则返回true。遵循FIFO
        //NonfairSync中直接返回false，可插队。
        if (writerShouldBlock() ||
                !compareAndSetState(c, c + acquires))
            return false;
        setExclusiveOwnerThread(current);
        return true;
    }
}
```  

2. 释放锁

```java
public static class WriteLock implements Lock, java.io.Serializable {
    private static final long serialVersionUID = -4992448646407690164L;

    private final Sync sync;

    protected WriteLock(ReentrantReadWriteLock lock) {
        sync = lock.sync;
    }

    //写锁释放
    public void unlock() {
        sync.release(1);
    }

    //AQS提供独占式释放同步状态的方法
    public final boolean release(int arg) {
        if (tryRelease(arg)) {
            Node h = head;
            if (h != null && h.waitStatus != 0)
                unparkSuccessor(h);
            return true;
        }
        return false;
    }

    //自定义重写的tryRelease方法
    protected final boolean tryRelease(int releases) {
        if (!isHeldExclusively())
            throw new IllegalMonitorStateException();
        int nextc = getState() - releases;//同步状态减去releases
        boolean free = exclusiveCount(nextc) == 0;
        //判断同步状态的低16位（写同步状态）是否为0，如果为0则返回true，否则返回false.
        //因为支持可重入
        if (free)
            setExclusiveOwnerThread(null);
        setState(nextc);//以获取写锁，不需要其他同步措施，是线程安全的
        return free;
    }
}
```

+ ReadLock

1. 获取锁

```java
public static class ReadLock implements Lock, java.io.Serializable {
    private static final long serialVersionUID = -5992448646407690164L;
    private final Sync sync;

    protected ReadLock(ReentrantReadWriteLock lock) {
        sync = lock.sync;
    }

    public void lock() {
        sync.acquireShared(1);
    }

    //使用AQS提供的共享式获取同步状态的方法
    public final void acquireShared(int arg) {
        if (tryAcquireShared(arg) < 0)
            doAcquireShared(arg);
    }

    protected final int tryAcquireShared(int unused) {
        /*
         * 1. 如果写锁被另一个线程持有，则失败。
         * 2. 否则，该线程有资格获得锁写入状态，因此询问它是否应该因为队列策略而阻塞。如果没有，请尝试通过 CASing 状态和更新计数来授予。请注意，步骤不检查可重入获取，它被推迟到完整版本以避免在更典型的非可重入情况下检查保持计数。
         * 3. 如果第 2 步由于线程显然不符合条件或 CAS 失败或计数饱和而失败，则链接到具有完整重试循环的版本。
         */
        Thread current = Thread.currentThread();
        int c = getState();
        //exclusiveCount(c)取低16位写锁。存在写锁且当前线程不是获取写锁的线程，返回-1，获取读锁失败。
        if (exclusiveCount(c) != 0 &&
                getExclusiveOwnerThread() != current)
            return -1;
        int r = sharedCount(c);//取高16位读锁，
        //readerShouldBlock（）用来判断当前线程是否应该被阻塞
        if (!readerShouldBlock() &&
                r < MAX_COUNT &&//MAX_COUNT为获取读锁的最大数量，为16位的最大值
                compareAndSetState(c, c + SHARED_UNIT)) {
            //firstReader是不会放到readHolds里的, 这样，在读锁只有一个的情况下，就避免了查找readHolds。
            if (r == 0) {// 是 firstReader，计数不会放入  readHolds。
                firstReader = current;
                firstReaderHoldCount = 1;
            } else if (firstReader == current) {//firstReader重入
                firstReaderHoldCount++;
            } else {
                // 非 firstReader 读锁重入计数更新
                HoldCounter rh = cachedHoldCounter;//读锁重入计数缓存，基于ThreadLocal实现
                if (rh == null || rh.tid != getThreadId(current))
                    cachedHoldCounter = rh = readHolds.get();
                else if (rh.count == 0)
                    readHolds.set(rh);
                rh.count++;
            }
            return 1;
        }
        //第一次获取读锁失败，有两种情况：
        //1）没有写锁被占用时，尝试通过一次CAS去获取锁时，更新失败（说明有其他读锁在申请）
        //2）当前线程占有写锁，并且有其他写锁在当前线程的下一个节点等待获取写锁，除非当前线程的下一个节点被取消，否则fullTryAcquireShared也获取不到读锁
        return fullTryAcquireShared(current);
    }

}
```

2. 释放锁

```java
public static class ReadLock implements Lock, java.io.Serializable {
    private static final long serialVersionUID = -5992448646407690164L;
    private final Sync sync;

    protected ReadLock(ReentrantReadWriteLock lock) {
        sync = lock.sync;
    }

    public void unlock() {
        sync.releaseShared(1);
    }

    public final boolean releaseShared(int arg) {
        if (tryReleaseShared(arg)) {
            doReleaseShared();
            return true;
        }
        return false;
    }

    protected final boolean tryReleaseShared(int unused) {
        Thread current = Thread.currentThread();
        //更新计数
        if (firstReader == current) {
            // assert firstReaderHoldCount > 0;
            if (firstReaderHoldCount == 1)
                firstReader = null;
            else
                firstReaderHoldCount--;
        } else {
            HoldCounter rh = cachedHoldCounter;
            if (rh == null || rh.tid != current.getId())
                rh = readHolds.get();
            int count = rh.count;
            if (count <= 1) {
                readHolds.remove();
                if (count <= 0)
                    throw unmatchedUnlockException();
            }
            --rh.count;
        }
        //自旋CAS，减去1<<16
        for (; ; ) {
            int c = getState();
            int nextc = c - SHARED_UNIT;
            if (compareAndSetState(c, nextc))
                // Releasing the read lock has no effect on readers,
                // but it may allow waiting writers to proceed if
                // both read and write locks are now free.
                return nextc == 0;
        }
    }

}
```