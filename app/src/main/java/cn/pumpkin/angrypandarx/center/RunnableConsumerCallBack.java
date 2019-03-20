package cn.pumpkin.angrypandarx.center;

/**
 * @author: zhibao.Liu
 * @version:
 * @date: 2019/1/16 16:53
 * @des:
 * @see {@link }
 */

public interface RunnableConsumerCallBack<T> {

    RunnableConsumerImpl start();
    RunnableConsumerImpl start(int type);
    void pause();
    void resume();
    void finish();

}
