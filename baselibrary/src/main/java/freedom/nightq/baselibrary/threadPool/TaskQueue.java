package freedom.nightq.baselibrary.threadPool;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

/**
 * Created by Nightq on 15/5/4.
 * 一个线程的任务队列
 */
public abstract class TaskQueue {

    private static final int START_TASK = 99;

    private Looper mServiceLooper;
    private Handler mServiceHandler;
    private HandlerThread handlerThread;

//    private static TaskQueue me;
//
//    public synchronized static TaskQueue getAppContext() {
//        if (me == null) {
//            me = new TaskQueue();
//            me.init();
//        }
//        return me;
//    }

    /**
     * 初始化
     */
    protected void init () {
        handlerThread = new HandlerThread(getClass().getName() + "[" + System.currentTimeMillis() + "]");
        handlerThread.start();
        mServiceLooper = handlerThread.getLooper();
        mServiceHandler = new Handler(mServiceLooper){
            @Override
            public void handleMessage(Message msg) {
                removeMessages(msg.what);
//                LogUtils.e("night", getClass().getName() + " thread = " + Thread.currentThread().getName());
                onHandleTask();
                super.handleMessage(msg);
            }
        };
    }

    /**
     * 任务过程
     * 需要重写
     */
    protected abstract void onHandleTask();

    /**
     * 提交一个任务
     */
    public void submitTask () {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = START_TASK;
        // 每一次发送都要清除之前的 message 。 因为没必要重复处理太多。
        mServiceHandler.removeMessages(START_TASK);
        mServiceHandler.sendMessage(msg);
    }

}
