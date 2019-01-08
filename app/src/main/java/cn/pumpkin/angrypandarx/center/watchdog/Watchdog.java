package cn.pumpkin.angrypandarx.center.watchdog;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.os.SystemClock;

import java.util.ArrayList;

import android.util.EventLog;
import android.util.Log;

/**
 * @author: zhibao.Liu
 * @version:
 * @date: 2019/1/7 10:57
 * @des: 线程监管和处理
 * @see {@link }
 */

public class Watchdog extends Thread {

    private static final String TAG = Watchdog.class.getName();
    private static Watchdog mThreadMonitor;
    private final HandlerChecker mMonitorChecker;
    private String threadName = "MonitorCheckThread";

    private static final long DEFAULT_TIMEOUT = 30 * 1000;
    private static final long CHECK_INTERVAL = DEFAULT_TIMEOUT / 2;

    /**
     * These are temporally ordered: larger values as lateness increases
     */
    static final int COMPLETED = 0;
    static final int WAITING = 1;
    static final int OVERDUE = 3;

    final ArrayList<HandlerChecker> mHandlerCheckers = new ArrayList<>();

    public final class HandlerChecker implements Runnable {
        private final Handler mHandler;
        private final String mName;
        private final long mWaitMax;
        private boolean mCompleted;
        private Monitor mCurrentMonitor;
        private long mStartTime;

        private final ArrayList<Monitor> mMonitors = new ArrayList<Monitor>();

        HandlerChecker(Handler handler, String name, long waitMaxMillis) {
            mHandler = handler;
            mName = name;
            mWaitMax = waitMaxMillis;
            mCompleted = true;
        }

        public void addMonitor(Monitor monitor) {
            mMonitors.add(monitor);
            Log.d(TAG, "addMonitor mMonitors.size = " + mMonitors.size() + ", monitor = " + monitor);
        }

        public void scheduleCheckLocked() {
            if (!mCompleted) {
                // we already have a check in flight, so no need
                return;
            }
            mCompleted = false;
            mCurrentMonitor = null;
            mStartTime = SystemClock.uptimeMillis();
            mHandler.postAtFrontOfQueue(this);
        }

        public boolean isOverdueLocked() {
            return (!mCompleted) && (SystemClock.uptimeMillis() > mStartTime + mWaitMax);
        }

        public int getCompletionStateLocked() {
            if (mCompleted) {
                return COMPLETED;
            } else {
                long latency = SystemClock.uptimeMillis() - mStartTime;
                if (latency < mWaitMax) {
                    return WAITING;
                } else {
                    return OVERDUE;
                }
            }
        }

        public String describeBlockedStateLocked() {
            return "Blocked in handler on " + mName + " (" + getThread().getName() + ")";
        }

        public Thread getThread() {
            return mHandler.getLooper().getThread();
        }

        public String getName() {
            return mName;
        }

        @Override
        public void run() {
            final int size = mMonitors.size();
            for (int i = 0; i < size; i++) {
                synchronized (Watchdog.this) {
                    mCurrentMonitor = mMonitors.get(i);
                }
                mCurrentMonitor.monitor();
            }
            synchronized (Watchdog.this) {
                mCompleted = true;
                mCurrentMonitor = null;
            }
        }
    }

    public interface Monitor {
        void monitor();
    }

    public static Watchdog getInstance() {
        if (mThreadMonitor == null) {
            mThreadMonitor = new Watchdog();
        }
        return mThreadMonitor;
    }

    private Watchdog() {
        HandlerThread handlerThread = new HandlerThread(threadName);
        handlerThread.start();
        mMonitorChecker = new HandlerChecker(new Handler(handlerThread.getLooper()),
                threadName, DEFAULT_TIMEOUT);
        mHandlerCheckers.add(mMonitorChecker);
    }

    public void addHandler(Handler handler) {
        addHandler(handler, DEFAULT_TIMEOUT);
    }

    public void addMonitor(Monitor monitor) {
        synchronized (this) {
            if (isAlive()) {
                throw new RuntimeException("Monitors can't be added once the Watchdog is running");
            }
            mMonitorChecker.addMonitor(monitor);
        }
    }

    public void removeThread(Handler thread) {
        for (int i = 0; i < mHandlerCheckers.size(); i++) {
            if (mHandlerCheckers.get(i).getThread().equals(thread)) {
                mHandlerCheckers.remove(i);
                i--;
            }
        }
    }

    public void addHandler(Handler handler, long timeoutMillis) {
        synchronized (this) {
            if (isAlive()) {
                throw new RuntimeException("Threads can't be added once the Watchdog is running");
            }
            final String name = handler.getLooper().getThread().getName();
            mHandlerCheckers.add(new HandlerChecker(handler, name, timeoutMillis));
        }
    }

    private int evaluateCheckerCompletionLocked() {
        int state = COMPLETED;
        for (int i = 0; i < mHandlerCheckers.size(); i++) {
            HandlerChecker hc = mHandlerCheckers.get(i);
            state = Math.max(state, hc.getCompletionStateLocked());
        }
        return state;
    }

    private ArrayList<HandlerChecker> getBlockedCheckersLocked() {
        ArrayList<HandlerChecker> checkers = new ArrayList<HandlerChecker>();
        for (int i = 0; i < mHandlerCheckers.size(); i++) {
            HandlerChecker hc = mHandlerCheckers.get(i);
            if (hc.isOverdueLocked()) {
                checkers.add(hc);
            }
        }
        return checkers;
    }

    private String describeCheckersLocked(ArrayList<HandlerChecker> checkers) {
        StringBuilder builder = new StringBuilder(128);
        for (int i = 0; i < checkers.size(); i++) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(checkers.get(i).describeBlockedStateLocked());
        }
        return builder.toString();
    }

    @Override
    public void run() {
        Log.d(TAG, "threadMonitor run size = " + mHandlerCheckers.size());
        while (true) {
            final ArrayList<HandlerChecker> blockedCheckers;
            final String subject;
            synchronized (this) {
                long timeout = CHECK_INTERVAL;
                for (int i = 0; i < mHandlerCheckers.size(); i++) {
                    HandlerChecker hc = mHandlerCheckers.get(i);
                    hc.scheduleCheckLocked();
                }
                long start = SystemClock.uptimeMillis();
                while (timeout > 0) {
                    try {
                        wait(timeout);
                    } catch (InterruptedException e) {
                        // Log.d(TAG,e.printStackTrace());
                    }
                    timeout = CHECK_INTERVAL - (SystemClock.uptimeMillis() - start);
                }
                final int waitState = evaluateCheckerCompletionLocked();
                if (waitState == COMPLETED || waitState == WAITING) {
                    continue;
                }
                blockedCheckers = getBlockedCheckersLocked();
                subject = describeCheckersLocked(blockedCheckers);
            }
            EventLog.writeEvent(1, subject);
            Log.d(TAG, "THREAD MONITOR KILLING THE PROPCESS: " + subject);
            for (int i = 0; i < blockedCheckers.size(); i++) {
                Log.d(TAG, "thread.name = " + blockedCheckers.get(i).getName() + " stack trace:");
                StackTraceElement[] statckTrace
                        = blockedCheckers.get(i).getThread().getStackTrace();
                for (StackTraceElement element : statckTrace) {
                    Log.d(TAG, " at " + element);
                }
            }
            Log.d(TAG, "*** KILL PROCESS!");
            Process.killProcess(Process.myPid());
            System.exit(10);
        }
    }

}
