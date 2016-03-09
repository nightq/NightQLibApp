package freedom.nightq.baselibrary.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;

import freedom.nightq.baselibrary.NightQAppLib;

/**
 * Created by H3c on 10/20/15.
 */
public class ClipboardUtils {
    public static void copyToClipboard(String content) {
        ClipboardManager cmb = (ClipboardManager) NightQAppLib.getAppContext().getSystemService(Activity.CLIPBOARD_SERVICE);
        cmb.setPrimaryClip(ClipData.newPlainText("copy", content));
        ToastUtils.showToast("已经复制到剪贴板");
    }
}
