package cn.pumpkin.angrypandarx.center.handle;

import android.os.Handler;
import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.pumpkin.angrypandarx.center.core.ThreadCenter;

import static cn.pumpkin.angrypandarx.center.handle.EnhanceHandler.Type.HANDLER_MAIN;
import static cn.pumpkin.angrypandarx.center.handle.EnhanceHandler.Type.HANDLER_SELF;

/**
 * @author: zhibao.Liu
 * @version:
 * @date: 2019/1/7 09:05
 * @des: 封装一下Handler对象,使用的时候建议不使用private Handler m*进行变量声明
 * @see {@link }
 */

public class EnhanceHandler extends Handler {

    private final static String TAG = EnhanceHandler.class.getName();
    /**
     * 每个handler 自带一个独一无二的令牌,不然删除对应自己的消息不方便
     * 如果使用sendMessage发送的消息,可以自行使用what删除就可以了
     */
    private Object token;

    /**
     * 象征性的参数
     */
    @IntDef({HANDLER_MAIN, HANDLER_SELF})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        int HANDLER_MAIN = 0;
        int HANDLER_SELF = 1;
    }

    /**
     * 暂定任意值,将把handler处理的消息都放到common队列里面
     */
    public EnhanceHandler() {
        super(ThreadCenter.getInstance().getCommonLooper());
        token = new Object();
    }

    /**
     * @param type 默认将消息放到主线程队列里面
     */
    public EnhanceHandler(int type) {
        super(ThreadCenter.getInstance().getMainLooper());
        token = new Object();
    }

    /**
     * 覆盖原生Handler.java对应的类
     * @param r      任务
     * @param delayMillis 顺序 : delay时间,token : 一定要带上自己的令牌token,不要去删除消息队列中其他人的待处理消息
     */
    public boolean postDelayed(Runnable r, int delayMillis) {
        return postAtTime(r, token,android.os.SystemClock.uptimeMillis() + delayMillis);
    }

    public void removeCallbacksAndMessages() {
        if (token != null) {
            removeCallbacksAndMessages(token);
        }
    }

    public void removeCallbacksAndMessages(String mask) {
        if (token != null) {
            removeCallbacksAndMessages(token);
        }
    }

}
