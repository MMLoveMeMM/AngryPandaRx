package cn.pumpkin.angrypandarx.center.core;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.pumpkin.angrypandarx.center.AbstractTaskWithResult;

class ThreadPoolManager <T>{

    static final String THREAD_POOL_NAME_DEFAULT = "DefaultPool";
    static final String THREAD_POOL_NAME_CORE = "CorePool";
    static final String THREAD_POOL_NAME_OTHER = "NewPool";

    private final int corePoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;

    private final int maximumPoolSize = corePoolSize;
    private final long mKeepAliveTime = 1L;
    private final TimeUnit unit = TimeUnit.HOURS;

    private ThreadPoolExecutorImpl mDefaultThreadPoolExecutor = null;
    private ThreadPoolExecutorImpl mCoreThreadPoolExecutor = null;

    ThreadPoolManager() {
        mDefaultThreadPoolExecutor = createDefaultThreadPool();
        mCoreThreadPoolExecutor = createCoreThreadPool();
    }

    void runInDefaultPool(Runnable runnable) {
        mDefaultThreadPoolExecutor.execute(runnable);
    }
    
    Future runInDefaultPoolSubmit(Runnable runnable) {
        return mDefaultThreadPoolExecutor.submit(runnable);
    }
    
    Future<T> runInDefaultPoolSubmit(AbstractTaskWithResult<T> mAbsTaskWithResult) {
        return mDefaultThreadPoolExecutor.submit(mAbsTaskWithResult);
    }
    
    void runInCorePool(Runnable runnable) {
        mCoreThreadPoolExecutor.execute(runnable);
    }

    void removeRunnable(Runnable runnable) {
        if (null != mDefaultThreadPoolExecutor) {
            mDefaultThreadPoolExecutor.remove(runnable);
        }

        if (null != mCoreThreadPoolExecutor) {
            mCoreThreadPoolExecutor.remove(runnable);
        }
    }

    private ThreadPoolExecutorImpl createDefaultThreadPool() {
        return createThreadPool(Thread.NORM_PRIORITY, THREAD_POOL_NAME_DEFAULT);
    }

    private ThreadPoolExecutorImpl createCoreThreadPool() {
        return createThreadPool(Thread.MAX_PRIORITY, THREAD_POOL_NAME_CORE);
    }

    ThreadPoolExecutor getDefaultPool() {
        return mDefaultThreadPoolExecutor;
    }

    ThreadPoolExecutor getCorePool() {
        return mCoreThreadPoolExecutor;
    }

    private ThreadPoolExecutorImpl createThreadPool(int priority, String poolName) {
        return new ThreadPoolExecutorImpl(
                corePoolSize,
                maximumPoolSize,
                mKeepAliveTime,
                unit,
                new LinkedBlockingQueue<Runnable>(),
                new ThreadFactoryImpl(priority, poolName),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
