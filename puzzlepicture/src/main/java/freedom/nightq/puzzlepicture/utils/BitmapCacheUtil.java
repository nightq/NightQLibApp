package freedom.nightq.puzzlepicture.utils;

import android.graphics.Bitmap;
import android.text.TextUtils;


import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import freedom.nightq.puzzlepicture.model.ProcessPicModel;

/**
 * Created by Nightq on 15/12/10.
 */
public class BitmapCacheUtil {

    /**
     * 图片编辑的缓存
     * imageKey path or url
     */
    private static ConcurrentMap<String, SoftReference<Bitmap>> processPicCache = new ConcurrentHashMap<>();

    /**
     * @UIThread
     * @return
     */
    public static Bitmap getValidBmpFromCache(ProcessPicModel model) {
        if (model == null) {
            return null;
        }
        return getValidBmpFromCache(model.getCutedPath());
    }


    /**
     * @UIThread
     * @return
     */
    public static Bitmap getValidBmpFromCache(String imageKey) {
        Bitmap bmp;
        if (!TextUtils.isEmpty(imageKey)
                && processPicCache.get(imageKey) != null
                && (bmp = processPicCache.get(imageKey).get()) != null && !bmp.isRecycled()) {
            return bmp;
        }
        return null;
    }

    /**
     * @return
     */
    public static void saveValidBmpToCache(String key, Bitmap bmp) {
        if (bmp == null || bmp.isRecycled() || TextUtils.isEmpty(key)) {
            return;
        }
        processPicCache.put(key, new SoftReference<Bitmap>(bmp));
    }

    /**
     * clear cache
     */
    public static void clearCache() {
        processPicCache.clear();
    }


}
