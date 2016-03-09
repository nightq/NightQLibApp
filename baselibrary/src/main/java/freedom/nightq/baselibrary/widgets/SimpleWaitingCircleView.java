package freedom.nightq.baselibrary.widgets;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;

import freedom.nightq.baselibrary.NightQAppLib;
import freedom.nightq.baselibrary.R;

/**
 * 主要用于loading或者waiting的小圈圈
 * Created by Nightq on 14-8-14.
 */
public class SimpleWaitingCircleView extends FloatViewBase {

    public static int DefaultLayout = R.layout.dialog_simple_circle_view;

    /**
     * make一个默认的toast
     * @return
     */
    public SimpleWaitingCircleView(Activity activity, int layoutId) throws Exception {
        super(activity, NightQAppLib.getHandler(), layoutId == 0 ? DefaultLayout : layoutId);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.gravity = Gravity.CENTER;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mLayoutParams.format = PixelFormat.TRANSLUCENT;
        mLayoutParams.windowAnimations = android.R.style.Animation_Dialog;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;//TYPE_APPLICATION_ATTACHED_DIALOG TYPE_TOAST

        setLayoutParams(mLayoutParams);

        addTargetView();
    }
}
