package freedom.nightq.baselibrary.utils;

import android.content.SharedPreferences;

import freedom.nightq.baselibrary.NightQAppLib;

/**
 * Created by Nightq on 16/3/7.
 */
public class SPUtil {

    private static SharedPreferences libSharePreferences;

    public static synchronized SharedPreferences getInstance () {
        if (libSharePreferences == null) {
            libSharePreferences = NightQAppLib.getAppContext()
                    .getSharedPreferences(("NightQLibSharePreferences"), 0);
            return libSharePreferences;
        }
        return libSharePreferences;
    }

    public static boolean setKeyboardHeight(int tmp) {
        if (tmp == getKeyboardHeight()) {
            return true;
        }
        return getInstance().edit().putInt("keyboardHeight", tmp).commit();
    }

    public static int getKeyboardHeight() {
        return getInstance().getInt("keyboardHeight", 0);
    }
}
