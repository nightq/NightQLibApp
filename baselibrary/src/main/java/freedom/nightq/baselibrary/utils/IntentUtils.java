package freedom.nightq.baselibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;

import java.io.File;
import java.util.List;

import freedom.nightq.baselibrary.NightQAppLib;

/**
 * Created by H3c on 2/13/15.
 */
public class IntentUtils {

    /**
     * 判断Intent是否存在
     */
    public static boolean isIntentAvailable(Context contex, Intent intent) {
        final PackageManager packageManager = contex.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
        return list.size() > 0;
    }

    /**
     * 调用系统的添加联系人
     */
    public static void jumpToAddContacts(Context context) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.dir/person");
        intent.setType("vnd.android.cursor.dir/contact");
        intent.setType("vnd.android.cursor.dir/raw_contact");
        context.startActivity(intent);
    }

}
