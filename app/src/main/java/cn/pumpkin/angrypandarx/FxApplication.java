package cn.pumpkin.angrypandarx;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * @author: zhibao.Liu
 * @version:
 * @date: 2019/1/8 12:13
 * @des:
 * @see {@link }
 */

public class FxApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initLeak();

    }

    private void initLeak(){
        /**
         * 增加内存泄漏检测
         * */
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}
