package cn.pumpkin.angrypandarx.center;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author: zhibao.Liu
 * @version:
 * @date: 2019/1/16 17:02
 * @des:
 * @see {@link }
 */

public interface FxConsumer <T>{
    void callTask(T/*ArrayBlockingQueue<T>*/ queue);
}
