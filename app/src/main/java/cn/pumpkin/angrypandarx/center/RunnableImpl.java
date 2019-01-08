package cn.pumpkin.angrypandarx.center;

import android.util.Log;

public class RunnableImpl implements Runnable {
	private static final String TAG = RunnableImpl.class.getName();
	private FxSubcribe subcribe;
	public RunnableImpl(FxSubcribe sub) {
		subcribe = sub;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Log.d(TAG,"RunnableImpl Thread Name : " + Thread.currentThread().getName());
		subcribe.callTask();
	}

}
