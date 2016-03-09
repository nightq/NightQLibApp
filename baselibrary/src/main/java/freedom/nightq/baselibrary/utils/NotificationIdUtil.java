package freedom.nightq.baselibrary.utils;

import android.app.NotificationManager;
import android.content.Context;

import freedom.nightq.baselibrary.NightQAppLib;

/**
 * Created by Nightq on 15/3/25.
 */
public class NotificationIdUtil {

    public static int Notification_ID = 0;

    private static int atomicId = 0;
    //TODO NIGHTQ  应该使用文件存储这个的。不然怕有的同志去不掉

    public synchronized static String getNotificationId(String key) {
        return key;
    }

    /**
     * 取消notification
     *
     * @param key 要取消的key
     */
    public static void cancelNotification(String key) {
        ((NotificationManager) NightQAppLib.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE)).cancel(
                NotificationIdUtil.getNotificationId(key),
                Notification_ID);
    }

}
