package cn.pumpkin.angrypandarx.center;

import android.os.Handler;
import android.os.Message;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cn.pumpkin.angrypandarx.center.core.ThreadCenter;

public class RunnableExecutor<T> {

	private static RunnableExecutor mRunnableExecutor;
	private static FxSubcribe mSubcribe;
	private RunnableImpl mRunnableImpl;
	private AbstractTaskWithResult<T> mAbsTaskWithResult;
	private FxObserver<T> mObserver;
	private static boolean enable = false;

	public RunnableExecutor() {

	}

	public RunnableExecutor(FxSubcribe sub) {
		mSubcribe = sub;
		mRunnableImpl = new RunnableImpl(mSubcribe);
	}

	public <T> RunnableExecutor(AbstractTaskWithResult<T> sub) {
		mAbsTaskWithResult = (AbstractTaskWithResult) sub;
	}

	public static RunnableExecutor create(FxSubcribe sub) {
		mRunnableExecutor = new RunnableExecutor(sub);
		return mRunnableExecutor;
	}

	public static <T> RunnableExecutor create(AbstractTaskWithResult<T> sub) {
		mRunnableExecutor = new RunnableExecutor(sub);
		return mRunnableExecutor;
	}

	public <T> RunnableExecutor mainThreadEnable(boolean able) {
		mRunnableExecutor.setEnable(able);
		return mRunnableExecutor;
	}

	public void setEnable(boolean able){
		enable = able;
	}

	public void exeFunc() {
		if (mRunnableImpl != null) {
			ThreadCenter.getInstance().runInDefaultPool(mRunnableImpl);
		}
	}

	public void exeCBFunc(FxObserver<T> observer) {
		if (mAbsTaskWithResult != null) {
			mObserver = observer;

			ThreadCenter.getInstance().runInDefaultPool(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Future future = ThreadCenter.getInstance().runInDefaultPoolSubmit(mAbsTaskWithResult);
					try {
						T t = null;
						try {
							t = (T) future.get(3, TimeUnit.SECONDS);

						} catch (TimeoutException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						final T tts = t;
						if (enable) {
							// 切换到android主线程上去
							Handler handler = new Handler(ThreadCenter.getInstance().getMainLooper()){
								@Override
								public void handleMessage(Message msg) {
									super.handleMessage(msg);
									if (mObserver != null) {
										if (tts != null) {
											mObserver.onNext(tts);
										} else {
											mObserver.onError(tts);
										}
									}
								}
							};
							handler.sendEmptyMessage(0);
						} else {
							// 不切换线程
							if (mObserver != null) {
								if (t != null) {
									mObserver.onNext(t);
								} else {
									mObserver.onError(t);
								}
							}
						}

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			});

		}
	}

}
