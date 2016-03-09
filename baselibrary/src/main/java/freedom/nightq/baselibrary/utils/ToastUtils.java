package freedom.nightq.baselibrary.utils;

import freedom.nightq.baselibrary.NightQAppLib;
import freedom.nightq.baselibrary.R;
import freedom.nightq.baselibrary.widgets.ToastBase;

/**
 * Created by H3c on 1/23/15.
 * 可以自定义的 toast
 */

public class ToastUtils {

    /**
     * toast layout id
     */
    public static int toastLayoutId = R.layout.toast_for_nightq;
    /**
     * toast duration
     */
    public static int toastDuration = 3000;

    private static ToastBase toast = null;

    public static Object CreateToastLock = new Object();

    public static void showToast(final CharSequence text) {
        NightQAppLib.getHandler().post(new Runnable() {
            @Override
            public void run() {
                synchronized (CreateToastLock) {
                    if (toast != null) {
                        toast.onDestroy();
                    }
                    try {
                        toast = new ToastBase(toastLayoutId);
                    } catch (Exception e) {
                        toast = null;
                    }
                }
                if (toast != null) {
                    toast.setText(text);
                    toast.show(toastDuration);
                }
            }
        });
    }

    /**
     * 可以在任意线程调用。仅供测试使用
     * @param content
     */
    public static void showToast(int content) {
        showToast(StringUtils.getStringFromRes(content));
    }

}
