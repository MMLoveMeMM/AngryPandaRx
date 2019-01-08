package cn.pumpkin.angrypandarx.center;

import java.util.concurrent.Callable;

public abstract class AbstractTaskWithResult<T> implements Callable<T>{

	public abstract T callTask();
	
	@Override
	public T call() throws Exception {
		// TODO Auto-generated method stub
		return callTask();
	}

}
