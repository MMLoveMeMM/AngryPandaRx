package cn.pumpkin.angrypandarx;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.ArrayBlockingQueue;

import cn.pumpkin.angrypandarx.center.AbstractTaskWithResult;
import cn.pumpkin.angrypandarx.center.FxConsumer;
import cn.pumpkin.angrypandarx.center.FxSubcribe;
import cn.pumpkin.angrypandarx.center.FxObserver;
import cn.pumpkin.angrypandarx.center.MonitorThread;
import cn.pumpkin.angrypandarx.center.RunnableConsumerImpl;
import cn.pumpkin.angrypandarx.center.RunnableExecutor;
import cn.pumpkin.angrypandarx.center.handle.EnhanceHandler;
import cn.pumpkin.angrypandarx.center.watchdog.Watchdog;
import cn.pumpkin.angrypandarx.ui.WindowUtils;

import static cn.pumpkin.angrypandarx.center.RunnableConsumerImpl.THTYPE.TH_CORE;

/**
 * @author: zhibao.Liu
 * @version:
 * @date: 2019/1/7 09:05
 * @des: 测试线程类
 * @see {@link }
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getName();

    private static final int NUM_CNT = 100;
    /**
     * 这个里面在做压力测试的时候为什么要加延时100ms
     * 这是因为线程池最大的任务量是Runtime.getRuntime().availableProcessors() * 2 + 1 = 17(在我的华为平板上是这个值)
     * 如果增加的任务数量太快,会有很多任务会被抛弃
     */
    private static final int DELAY_TIME = 100;

    private Button mBtn;
    private Button mBtn1;
    private Button mBtn2;
    private Button mBtn3;
    private Button mBtn4;
    private Button mBtn5;

    private Button mAlertBtn;

    private Button mThreadBtn;

    private int count = 0;

    private MonitorThread mThread;

    private TimeHandler mTimeHandler = new TimeHandler();

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn: {
                RunnableExecutor.create(new FxSubcribe() {
                    @Override
                    public void callTask() {
                        divTask();
                    }
                }).exeFunc();
            }
            break;
            case R.id.btn5: {
                for (int i = 0; i < NUM_CNT; i++) {

                    if (i % 10 == 0) {
                        try {
                            Thread.sleep(DELAY_TIME);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    RunnableExecutor.create(new AbstractTaskWithResult<String>() {

                        @Override
                        public String callTask() {
                            // TODO Auto-generated method stub
                            return yaliDivTask2();
                        }

                    }).mainThreadEnable(true)
                            .exeCBFunc(new FxObserver<String>() {

                                @Override
                                public void onNext(String t) {
                                    // TODO Auto-generated method stub
                                    Log.e(TAG, "sub thread +++++++++++++++ : " + t + " thread name : " + Thread.currentThread().getName());
                                    mBtn5.setText("main thread " + t);
                                }

                                @Override
                                public void onError(String t) {
                                    // TODO Auto-generated method stub
                                }

                            });

                }
            }
            break;
            case R.id.btn4: {
                for (int i = 0; i < NUM_CNT; i++) {

                    if (i % 10 == 0) {
                        try {
                            Thread.sleep(DELAY_TIME);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    RunnableExecutor.create(new AbstractTaskWithResult<String>() {

                        @Override
                        public String callTask() {
                            // TODO Auto-generated method stub
                            return yaliDivTask2();
                        }

                    }).mainThreadEnable(false)
                            .exeCBFunc(new FxObserver<String>() {

                                @Override
                                public void onNext(final String t) {
                                    // TODO Auto-generated method stub
                                    Log.e(TAG, "sub thread +++++++++++++++ : " + t + " thread name : " + Thread.currentThread().getName());

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mBtn4.setText("" + t);
                                        }
                                    });
                                }

                                @Override
                                public void onError(String t) {
                                    // TODO Auto-generated method stub
                                }

                            });
                }
            }
            break;
            case R.id.btn3: {
                for (int i = 0; i < NUM_CNT; i++) {

                    RunnableExecutor.create(new FxSubcribe() {
                        @Override
                        public void callTask() {
                            yaliTask(count);
                        }
                    }).exeFunc();

                }
            }
            break;
            default:
                break;
        }

    }

    private class TimeHandler extends EnhanceHandler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Log.e(TAG, "handle message : " + msg.what);

        }
    }

    private EnhanceHandler mHandler = new EnhanceHandler();

    private ArrayBlockingQueue<String> mArrayBlockingQueue = new ArrayBlockingQueue<>(100);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mThreadBtn = (Button) findViewById(R.id.thread);
        mThreadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < 50; i++) {
                    mArrayBlockingQueue.offer("liuzhibao " + count++);
                }

                RunnableConsumerImpl consumer1 = new RunnableConsumerImpl(new FxConsumer() {
                    @Override
                    public void callTask(Object queue) {

                    }
                }).start();

                RunnableConsumerImpl consumer = new RunnableConsumerImpl<>
                        (mArrayBlockingQueue, new FxConsumer<ArrayBlockingQueue<String>>() {
                            @Override
                            public void callTask(ArrayBlockingQueue<String> queue) {

                            }
                        }
                        /*new FxConsumer<ArrayBlockingQueue<String>>() {
                            @Override
                            public void callTask(ArrayBlockingQueue<String> queue) {

                            }
                        }*/
                        /*new FxConsumer<ArrayBlockingQueue<String>>() {
                    @Override
                    public void callTask(ArrayBlockingQueue<String> queue) {
                        try {
                            String content = queue.take();
                            Log.e(TAG, "consumer : " + content);
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }*/).start(TH_CORE);

                try {
                    Thread.sleep(5000);
                    consumer.pause();
                    Thread.sleep(5000);
                    consumer.resume();
                    Thread.sleep(5000);
                    consumer.finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        });


        mThread = new MonitorThread();
        mThread.start();
        Watchdog.getInstance().addMonitor(new Watchdog.Monitor() {
            @Override
            public void monitor() {
                synchronized (MainActivity.this) {
                }
            }
        });

        mTimeHandler.sendEmptyMessage(9527);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "======================================");
            }
        }, 1000);

        Watchdog.getInstance().addHandler(mTimeHandler);

        mBtn = (Button) findViewById(R.id.btn);
        mBtn.setOnClickListener(this);

        mBtn5 = (Button) findViewById(R.id.btn5);
        mBtn5.setOnClickListener(this);

        mBtn4 = (Button) findViewById(R.id.btn4);
        mBtn4.setOnClickListener(this);

        mBtn3 = (Button) findViewById(R.id.btn3);
        mBtn3.setOnClickListener(this);

        mBtn2 = (Button) findViewById(R.id.btn2);
        mBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RunnableExecutor.create(new AbstractTaskWithResult<String>() {

                    @Override
                    public String callTask() {
                        // TODO Auto-generated method stub
                        return divTask2();
                    }

                }).mainThreadEnable(true)
                        .exeCBFunc(new FxObserver<String>() {

                            @Override
                            public void onNext(String t) {
                                // TODO Auto-generated method stub
                                Log.e(TAG, "set main +++++++++++++++ : " + t + " thread name : " + Thread.currentThread().getName());
                                mBtn1.setText("main thread");
                            }

                            @Override
                            public void onError(String t) {
                                // TODO Auto-generated method stub
                            }

                        });
            }
        });

        mBtn1 = (Button) findViewById(R.id.btn1);
        mBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RunnableExecutor.create(new AbstractTaskWithResult<String>() {

                    @Override
                    public String callTask() {
                        // TODO Auto-generated method stub
                        return divTask2();
                    }

                }).mainThreadEnable(false)
                        .exeCBFunc(new FxObserver<String>() {

                            @Override
                            public void onNext(String t) {
                                // TODO Auto-generated method stub
                                Log.e(TAG, "sub thread +++++++++++++++ : " + t + " thread name : " + Thread.currentThread().getName());

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBtn1.setText("subthread");
                                    }
                                });
                            }

                            @Override
                            public void onError(String t) {
                                // TODO Auto-generated method stub
                            }

                        });


            }
        });

        // Toast.makeText(this, "", Toast.LENGTH_SHORT).show();

        mAlertBtn = (Button) findViewById(R.id.alert);

        mAlertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!windowsEnable && !WindowUtils.isShown) {
                    WindowUtils.showPopupWindow(getApplicationContext());
                } else {
                    WindowUtils.hidePopupWindow();
                }
                windowsEnable = !windowsEnable;
            }
        });

    }

    private boolean windowsEnable = false;

    private synchronized void divTask() {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "+++++++++++++++++++++++++++++++++++++");

    }

    private synchronized String divTask2() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "+++++++++++++++++++++++++++++++++++++");
        return "liuzhibao callback";
    }

    private synchronized String yaliDivTask2() {
        count++;
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "+++++++++++++++++++++++++++++++++++++ : " + count);
        return "liuzhibao yaliDivTask2 : " + count;
    }

    private synchronized String yaliTask(int cnt) {
        count++;
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "+++++++++++++++++++++++++++++++++++++ : " + count);
        return "liuzhibao callback" + count;
    }

}
