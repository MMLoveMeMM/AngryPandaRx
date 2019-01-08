package cn.pumpkin.angrypandarx;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import cn.pumpkin.angrypandarx.center.AbstractTaskWithResult;
import cn.pumpkin.angrypandarx.center.FxSubcribe;
import cn.pumpkin.angrypandarx.center.FxObserver;
import cn.pumpkin.angrypandarx.center.MonitorThread;
import cn.pumpkin.angrypandarx.center.RunnableExecutor;
import cn.pumpkin.angrypandarx.center.handle.EnhanceHandler;
import cn.pumpkin.angrypandarx.center.watchdog.Watchdog;
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
    private static final int DELAY_TIME = 100;

    private Button mBtn;
    private Button mBtn1;
    private Button mBtn2;
    private Button mBtn3;
    private Button mBtn4;
    private Button mBtn5;

    private int count = 0;

    private MonitorThread mThread;

    private TimeHandler mTimeHandler = new TimeHandler();

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.btn:{
                RunnableExecutor.create(new FxSubcribe() {
                    @Override
                    public void callTask() {
                        divTask();
                    }
                }).exeFunc();
            }
                break;
            case R.id.btn5:{
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
            case R.id.btn4:{
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
            case R.id.btn3:{
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    }

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
