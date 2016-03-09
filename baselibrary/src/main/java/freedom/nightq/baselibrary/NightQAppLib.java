package freedom.nightq.baselibrary;

import android.content.Context;
import android.os.Handler;

import freedom.nightq.baselibrary.utils.DeviceUtils;

/**
 * Created by Nightq on 16/3/7.
 */
public class NightQAppLib {

    public static NightQAppLib nightQAppLib;

    public Context nightqApp;
    public Handler mHandler;
    public Thread mUiThread;

    private NightQAppLib(Context nightqApp) {
        this.nightqApp = nightqApp;
        mHandler = new Handler();
        mUiThread = Thread.currentThread();
    }

    /**
     * 必须主线程初始化
     * 在 application 里面初始化
     * @param context
     */
    public static void init (Context context) {
        if (context == null && nightQAppLib == null) {
            throw new ExceptionInInitializerError("nightq lib init error!!!");
        }
        if (context == null) {
            return;
        }
        if (nightQAppLib != null
                && nightQAppLib.nightqApp == context.getApplicationContext()) {
            // 同一个app context 那就直接返回
            return;
        }
        nightQAppLib = new NightQAppLib(context.getApplicationContext());
        DeviceUtils.init();
    }

    public static Context getAppContext() {
        return nightQAppLib.nightqApp;
    }

    public static NightQAppLib getInstance() {
        return nightQAppLib;
    }

    public static void runOnUiThread(Runnable action) {
        if (Thread.currentThread() != getInstance().mUiThread) {
            getInstance().mHandler.post(action);
        } else {
            action.run();
        }
    }

    public static void runOnUiThreadDelayed(Runnable action, long delayed) {
        getInstance().mHandler.postDelayed(action, delayed);
    }

    public static Handler getHandler () {
        return getInstance().mHandler;
    }
}
