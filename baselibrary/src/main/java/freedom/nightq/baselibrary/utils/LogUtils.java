package freedom.nightq.baselibrary.utils;

import android.util.Log;


/**
 * Created by H3c on 1/23/15.
 */
public class LogUtils {
    private static final boolean mDebug = false;//BuildConfig.DEBUG;

    public static void d(String tag, String message) {
        if (mDebug) {
            Log.d(tag, message);
        }
    }

    public static void d(String tag, String format, Object... args) {
        if (mDebug) {
            Log.d(tag, String.format(format, args));
        }
    }

    public static void e(String tag, String message) {
        if (mDebug && message != null) {
            Log.e(tag, message);
        }
    }

    public static void e(String... message) {
        if (mDebug && message != null) {
            if(message.length == 2) {
                e(message[0], message[1]);
                return;
            }

            StringBuffer sb = new StringBuffer();
            for (String str : message) {
                sb.append(str);
                sb.append(" ===");
            }
            Log.e("H6c", sb.toString());
        }
    }

    public static void e(String tag, String format, Object... args) {
        if (mDebug && format != null) {
            Log.e(tag, String.format(format, args));
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (mDebug && msg != null) {
            Log.e(tag, msg, tr);
        }
    }

    public static void i(String tag, String message) {
        if (mDebug && message != null) {
            Log.i(tag, message);
        }
    }

    public static void i(String tag, String format, Object... args) {
        if (mDebug && format != null) {
            Log.i(tag, String.format(format, args));
        }
    }

    public static void v(String tag, String format, Object... args) {
        if (mDebug && format != null) {
            if (args.length > 0) {
                Log.v(tag, String.format(format, args));
            } else {
                Log.v(tag, format);
            }
        }
    }

    public static void w(String tag, String format, Object... args) {
        if (mDebug) {
            if (args.length > 0) {
                Log.w(tag, String.format(format, args));
            } else {
                Log.w(tag, format);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public static String logTag(Class cls) {
        return cls.getSimpleName();
    }
}
