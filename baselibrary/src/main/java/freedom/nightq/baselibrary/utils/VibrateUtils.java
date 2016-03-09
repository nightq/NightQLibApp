package freedom.nightq.baselibrary.utils;

import android.content.Context;
import android.os.Vibrator;

import freedom.nightq.baselibrary.NightQAppLib;

/**
 * Created by H3c on 2/17/15.
 */
public class VibrateUtils {
    public static final void shortVibrate() {
        Vibrator vibrator = (Vibrator) NightQAppLib.getAppContext().getSystemService(Context.VIBRATOR_SERVICE);
        long [] pattern = {100, 50};   // 停止 开启
        vibrator.vibrate(pattern, -1);
    }
}
