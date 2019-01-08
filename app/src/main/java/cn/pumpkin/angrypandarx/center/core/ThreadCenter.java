package cn.pumpkin.angrypandarx.center.core;

import android.os.HandlerThread;
import android.os.Looper;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import cn.pumpkin.angrypandarx.center.AbstractTaskWithResult;

public class ThreadCenter<T> {

    private static volatile ThreadCenter sInstance;

    private ThreadFactory mThreadFactoryImpl = null;

    private ThreadPoolManager mThreadPoolManager = null;

    public static ThreadCenter getInstance() {
        if (sInstance == null) {
            synchronized (ThreadCenter.class) {
                if (sInstance == null) {
                    sInstance = new ThreadCenter();
                }
            }
        }
        return sInstance;
    }
    private HandlerThread mCommonHandlerThread;
    private ThreadCenter() {
        mCommonHandlerThread = new HandlerThread("common");
        mCommonHandlerThread.start();
        mThreadPoolManager = new ThreadPoolManager();
    }

    public HandlerThread getCommonHandlerThread() {
        return mCommonHandlerThread;
    }

    public Looper getCommonLooper() {
        return mCommonHandlerThread.getLooper();
    }

    public Looper getMainLooper() {
        return Looper.getMainLooper();
    }

    public synchronized Thread newThread(Runnable r) {
        if (null == mThreadFactoryImpl) {
            mThreadFactoryImpl = new ThreadFactoryImpl(Thread.NORM_PRIORITY, ThreadPoolManager.THREAD_POOL_NAME_OTHER);
        }
        return mThreadFactoryImpl.newThread(r);
    }

    public synchronized void runInDefaultPool(Runnable runnable) {
        if (null == runnable) {
            return;
        }
        mThreadPoolManager.runInDefaultPool(runnable);
    }
    
    public synchronized Future runInDefaultPoolSubmit(Runnable runnable) {
        if (null == runnable) {
            return null;
        }
        return mThreadPoolManager.runInDefaultPoolSubmit(runnable);
    }
    
    public synchronized Future<T> runInDefaultPoolSubmit(AbstractTaskWithResult<T> mAbsTaskWithResult) {
        if (null == mAbsTaskWithResult) {
            return null;
        }
        return mThreadPoolManager.runInDefaultPoolSubmit(mAbsTaskWithResult);
    }

    public synchronized void runInCorePool(Runnable runnable) {
        if (null == runnable) {
            return;
        }
        mThreadPoolManager.runInCorePool(runnable);
    }

    public synchronized ThreadPoolExecutor getDefaultPool() {
        return mThreadPoolManager.getDefaultPool();
    }

    public synchronized ThreadPoolExecutor getCorePool() {
        return mThreadPoolManager.getCorePool();
    }

}