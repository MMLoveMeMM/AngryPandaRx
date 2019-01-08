package cn.pumpkin.angrypandarx.center.core;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class ThreadFactoryImpl implements ThreadFactory {

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private final ThreadGroup mGroup;
    private final String mNamePrefix;
    private final int mThreadPriority;

    ThreadFactoryImpl(int threadPriority, String poolName) {
        mThreadPriority = threadPriority;
        mGroup = Thread.currentThread().getThreadGroup();
        mNamePrefix = poolName + "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(mGroup, r, mNamePrefix + threadNumber.getAndIncrement(), 0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        t.setPriority(mThreadPriority);
        return t;
    }
}
