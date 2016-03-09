package freedom.nightq.baselibrary.utils;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import freedom.nightq.baselibrary.NightQAppLib;
import freedom.nightq.baselibrary.R;

import java.io.InputStream;

/**
 * Created by H3c on 3/19/15.
 */
public class ResourceUtils {
    public static Resources res = NightQAppLib.getAppContext().getResources();

    public static Resources getResource() {
        return res;
    }

    public static int getColorResource(int resource) {
        return res.getColor(resource);
    }

    public static ColorStateList getColorStateList(int colorStateRes) {
        return res.getColorStateList(colorStateRes);
    }

    public static int getAppMainColor() {
        return getColorResource(R.color.app_main_color);
    }

    public static float getDimension(int dimension) {
        return res.getDimension(dimension);
    }

    public static Drawable getDrawable(int resource) {
        Drawable drawable = null;
        try {
            drawable = res.getDrawable(resource);
        } catch (Exception e) {}
        return drawable;
    }

    public static String[] getStringArray(int resource) {
        return res.getStringArray(resource);
    }

    public static int[] getIntArray(int resource) {
        return res.getIntArray(resource);
    }

    public static Integer[] getResourceArray(int resource) {
        TypedArray imgs = res.obtainTypedArray(resource);
        Integer[] resArr = new Integer[imgs.length()];
        for (int n = 0; n < imgs.length(); n++) {
            resArr[n] = imgs.getResourceId(n, -1);
        }
        imgs.recycle();
        return resArr;
    }

    public static InputStream openRawResource(int resource) {
        return res.openRawResource(resource);
    }

    public static String Color2String(int color) {
        String colorStr = Integer.toHexString(color);
        return '#' + colorStr.substring(2);
    }
}
