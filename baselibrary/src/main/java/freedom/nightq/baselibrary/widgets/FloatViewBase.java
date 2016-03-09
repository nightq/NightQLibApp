package freedom.nightq.baselibrary.widgets;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import freedom.nightq.baselibrary.NightQAppLib;

/**
 * Created by Nightq on 14-8-14.
 * must in main thread
 */
public class FloatViewBase {

    Handler mHandler;
    WindowManager mWindowManager;
    View mLayout;

    public void setLayoutParams(WindowManager.LayoutParams mLayoutParams) {
        this.mLayoutParams = mLayoutParams;
    }

    WindowManager.LayoutParams mLayoutParams;
    Context mContext;

    /**
     * 创建的时候就加到window里面了。
     * @param context
     * @param handler
     * @param layoutId
     */
    public FloatViewBase(Context context, Handler handler, int layoutId) {
        if (context == null || handler == null) {
            throw new IllegalArgumentException();
        }
        mHandler = handler;
        mContext = context;

        mLayoutParams = new WindowManager.LayoutParams();

        mLayout = LayoutInflater.from(context).inflate(layoutId, null);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * mainthread调用这个方法添加view
     */
    public void addTargetView () throws Exception {
        if (mWindowManager == null || mLayout == null || mLayoutParams == null) {
            throw new Exception("addTargetView ERROR");
        }
        mWindowManager.addView(mLayout, mLayoutParams);
    }

    Runnable toastHideRunnable = new Runnable() {
        public void run() {
            onDestroy();
        }
    };

    /**
     * 显示duration
     * duration <= 0的时候不消失，需要调用destroy来消失
     */
    public void show(final long duration) {
        NightQAppLib.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                show();
                //The duration that you want
                if (duration > 0) {
                    mHandler.postDelayed(toastHideRunnable, duration);
                }
            }
        });
    }

    /**
     * 显示
     */
    public boolean show() {
        if (mLayout != null) {
            mLayout.setVisibility(View.VISIBLE);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 显示
     */
    public void hide() {
        NightQAppLib.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLayout != null) {
                    mLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 隐藏
     */
    public void onDestroy() {
        NightQAppLib.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLayout != null && mWindowManager != null) {
                    if (mLayout.getParent() != null) {
                        mWindowManager.removeViewImmediate(mLayout);
                    }
                    mLayout = null;
                }
            }
        });
    }
}
