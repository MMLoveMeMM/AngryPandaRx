package cn.pumpkin.angrypandarx.center;

import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.ArrayBlockingQueue;

import cn.pumpkin.angrypandarx.center.core.ThreadCenter;

import static cn.pumpkin.angrypandarx.center.RunnableConsumerImpl.THTYPE.TH_COMMON;
import static cn.pumpkin.angrypandarx.center.RunnableConsumerImpl.THTYPE.TH_CORE;

/**
 * @author: zhibao.Liu
 * @version:
 * @date: 2019/1/16 16:48
 * @des:
 * @see {@link }
 */

public class RunnableConsumerImpl<T> implements Runnable, RunnableConsumerCallBack {

    @IntDef({TH_CORE, TH_COMMON})
    @Retention(RetentionPolicy.SOURCE)
    public @interface THTYPE {
        int TH_CORE = 0;
        int TH_COMMON = 1;
    }

    private static final String TAG = RunnableConsumerImpl.class.getName();
    private T/*ArrayBlockingQueue<T>*/ mArrayBlockingQueue;
    private boolean isPause = false;
    private boolean isInterrupt = false;

    private FxConsumer<T> mFxConsumer;

    public RunnableConsumerImpl(T/*ArrayBlockingQueue*/ queue, FxConsumer<T> consumer) {
        mArrayBlockingQueue = queue;
        mFxConsumer = consumer;
    }

    public RunnableConsumerImpl(FxConsumer<T> consumer) {
        mFxConsumer = consumer;
    }

    @Override
    public void run() {

        while (!isInterrupt) {
            if (!isPause) {
                if (mFxConsumer != null) {
                    if(mArrayBlockingQueue!=null) {
                        mFxConsumer.callTask(mArrayBlockingQueue);
                    }else{
                        mFxConsumer.callTask(null);
                    }
                }
            } else {
                Log.e(TAG, "thread name : " + Thread.currentThread().getName() + " under pause status !");
            }
        }

        Log.e(TAG, "thread name : " + Thread.currentThread().getName() + " is finish !");

    }

    @Override
    public RunnableConsumerImpl start() {
        ThreadCenter.getInstance().runInDefaultPool(this);
        return this;
    }

    @Override
    public RunnableConsumerImpl start(int type) {
        if(type==TH_CORE){
            ThreadCenter.getInstance().runInCorePool(this);
        }else{
            ThreadCenter.getInstance().runInDefaultPool(this);
        }
        return null;
    }

    @Override
    public void pause() {
        isPause = true;
    }

    @Override
    public void resume() {
        isPause = false;
    }

    @Override
    public void finish() {
        isInterrupt = true;
    }
}
