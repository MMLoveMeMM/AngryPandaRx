package cn.pumpkin.angrypandarx.center;

public interface FxObserver<T> {
	void onNext(T t);
	void onError(T t);
	// void onComplete(T t);
}
