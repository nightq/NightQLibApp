package freedom.nightq.baselibrary.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by H3c on 1/27/15.
 */
public class ViewUtils {


    public static void toFullScreen(Activity activity, boolean flag) {
        Window window = activity.getWindow();
        toFullScreen(window, flag);
    }

    /**
     * 修复5.x上相机全屏之后顶部还有一条状态栏
     * @param window
     * @param flag
     */
    public static void toFullScreen(Window window, boolean flag) {
        if(flag) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /**
     * 给ET设置错误提示
     * @param et
     * @param content
     */
    public static void showEditTextError(TextView et, String content) {
        if(et != null) {
            if(TextUtils.isEmpty(content)) {
                et.setError(null);
            } else {
                et.setError(Html.fromHtml("<font color='white'>" + content + "</font>"));
            }
        }
    }

    public static void getViewWH(final View view , final GetViewWHCallback callback) {
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                    if(callback != null) {
                        callback.getViewWH(view.getWidth(), view.getHeight());
                    }
                }
            });
        }
    }

    /** 将一个View转为图片 */
    public static Bitmap createBitmapFromView(View v) {
        return createBitmapFromView(v, Bitmap.Config.ARGB_8888);
    }

    public static Bitmap createBitmapFromView(View v, Bitmap.Config config) {
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), config);
            Canvas canvas = new Canvas(bitmap);
            v.draw(canvas);
        } catch (Exception e) {}
        return bitmap;
    }

    public interface GetViewWHCallback {
        void getViewWH(int width, int height);
    }

    public static ImageView fIV(View view, int id) {
        return (ImageView) view.findViewById(id);
    }

    public static TextView fTV(View view, int id) {
        return (TextView) view.findViewById(id);
    }

    public static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }

    /**
     * 设置topmargin， 范围为0，到负的height
     * @param view
     * @param dTopMargin
     */
    public static void setViewTopMarginByRelativeLayout(View view, int dTopMargin) {
        view.setPadding(0, dTopMargin, 0, 0);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
//        params.topMargin = dTopMargin;
//        view.setLayoutParams(params);
    }

    public static void setStatusBarTransparent(boolean show, Activity activity) {
        if(activity == null || !DeviceUtils.isUpAsKitkat()) return;
        Window w = activity.getWindow();
        if(show) {
            w.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            final WindowManager.LayoutParams attrs = w
                    .getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.setAttributes(attrs);
            w.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    public static void setImageViewAlpha(ImageView iv, float alpha) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            iv.setImageAlpha((int) (alpha * 0xFF));
        } else {
            iv.setAlpha(alpha);
        }
    }
}
