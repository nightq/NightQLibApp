package freedom.nightq.baselibrary.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;

import freedom.nightq.baselibrary.NightQAppLib;
import freedom.nightq.baselibrary.R;
import freedom.nightq.baselibrary.utils.LogUtils;


/**
 * Created by Nightq on 14-7-31.
 */
public class DialogBaseForNightQ extends Dialog {

    public int dialogAnimStyle = R.style.dialog_animation;

    private int windowMatchParentWidth = ViewGroup.LayoutParams.MATCH_PARENT;
    private int windowMatchParentHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
    private boolean fullScreen = false;
    private boolean backDismiss = true;
    private boolean showAnimation = true;

    protected DialogBaseForNightQ(Context context, int theme) {
        super(context, theme);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (fullScreen) {
            //requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        getWindow().setGravity(Gravity.CENTER);
        if (showAnimation) {
            getWindow().setWindowAnimations(dialogAnimStyle);  //添加动画
        }
        setCancelable(true);
    }

    @Override
    public void onBackPressed() {
        if (backDismiss) {
            super.onBackPressed();
        }
    }

    /**
     * 设置是否全屏
     */
    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }

    /**
     * 设置布局是否撑满parent
     */
    public void setWindowMatchParentWidth(int windowMatchParent) {
        this.windowMatchParentWidth = windowMatchParent;
    }

    public void setWindowMatchParentHeight(int windowMatchParent) {
        this.windowMatchParentHeight = windowMatchParent;
    }

    /**
     * 设置是否点击后退消失
     */
    public void setBackDismiss(boolean backDismiss) {
        this.backDismiss = backDismiss;
        setCancelable(backDismiss);
    }

    /**
     * 设置启动开启关闭动画
     */
    public void setAnimation(boolean showAnimation) {
        this.showAnimation = showAnimation;
    }

    @Override
    public void show() {
        try {
            super.show();
            NightQAppLib.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        getWindow().setLayout(windowMatchParentWidth,
                                windowMatchParentHeight);
                    } catch (Exception e) {

                    }
                }
            }, 1);
        } catch (Exception e) {
            LogUtils.e("nightq", "show dialog e = " + e);
        }
    }
}
