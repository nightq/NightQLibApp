package freedom.nightq.baselibrary.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;


/**
 * Created by Nightq on 15/10/20.
 */
public class KeyboardUtil {

    /**
     * 为界面设置键盘开关的监听
     *
     * @param activity
     * @param view     界面内的一个view
     * @param listener
     */
    public static void setKeyboardListerToView(
            final Activity activity,
            View view,
            final OnKeyboardStateChangeListener listener) {
        if (activity == null
                || view == null) {
            throw new IllegalArgumentException();
        }
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            int lastinputMethodHeight = -1;

            @Override
            public void onGlobalLayout() {
                int softInputHeight = getSupportSoftInputHeight(activity);
                if (softInputHeight != lastinputMethodHeight) {
                    lastinputMethodHeight = softInputHeight;
                    if (softInputHeight > 100) {
                        // 随时记录键盘高度
                        SPUtil.setKeyboardHeight(softInputHeight);
                    }
                    if (listener != null) {
                        listener.OnKeyboardStateChange(softInputHeight > 100, softInputHeight);
                    }
                }
            }
        });
    }


    /**
     * phone            displayBottom     usableHeight  	realHeight 	 	    windowHeight   softButtonsBarHeight
     * <p/>
     * mi           	1776-60=1716 	1776(1920-144)		1860(1920-60)		1776(固定)		144  			yes
     * <p/>
     * n5				1776-72=1704	1776(1920-144)		1848(1920-72)		1920(固定)		144 			yes
     * <p/>
     * meizu 			1776=(1920-144)	1920(1920)			1845(1920-75)		1776(固定) 		0				no
     * <p/>
     * 三星				1920-60=1860    1920(1920)			1860(1920-60)		1920 (固定)		0				no
     * <p/>
     * 魅族的奇葩把底部actionbar 算在了可用的里面。但是实际显示的时候会在根view 下加bar，所以导致不对。
     * <p/>
     * 总的来说，因为meizu的actionbar 喝5.0的actionbar 的特性，导致在计算键盘的时候有问题。很难精确计算
     * 所以大概计算高度来判断键盘是否打开。
     * 所以取可用高度和窗口高度的最小值来作为标准
     * @param activity
     * @return
     */
    public static int getSupportSoftInputHeight(Activity activity) {
        Rect r = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);

        // 下面的屏幕高度都没算 顶部的状态栏
        // 屏幕当前显示activity的高度
        int displayBottom = r.bottom;
        // 屏幕可用高度 mi 1716, n5 1848  meizu 1848
        int usableHeight = DeviceUtils.screenHPixels;
        //windowHeight mi 1776, n5 1920  meizu 1920
        int windowHeight = activity.getWindow().getDecorView().getRootView().getHeight();
//        LogUtils.e("nightq", " displayBottom = " + displayBottom
//                + " usableHeight = " + usableHeight
//                + " windowHeight = " + windowHeight);
        int softInputHeight = Math.min(usableHeight, windowHeight) - displayBottom;
//        LogUtils.e("nightq", " current 键盘高度 = " + softInputHeight);
        return softInputHeight;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static int getRealHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        return metrics.heightPixels;
    }

    public interface OnKeyboardStateChangeListener {
        /**
         * @param isOpened       键盘的打开状态
         * @param keyboardHeight 键盘打开的时候可以有高度，没打开的时候这个值是0
         */
        void OnKeyboardStateChange(boolean isOpened, int keyboardHeight);
    }


}
