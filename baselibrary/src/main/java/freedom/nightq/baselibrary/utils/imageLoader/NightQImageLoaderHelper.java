package freedom.nightq.baselibrary.utils.imageLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.text.TextUtils;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;

import freedom.nightq.baselibrary.NightQAppLib;
import freedom.nightq.baselibrary.utils.DeviceUtils;
import freedom.nightq.baselibrary.utils.PhotoUtils;
import freedom.nightq.baselibrary.utils.IntentUtils;
import freedom.nightq.baselibrary.utils.StorageUtils;

/**
 * Created by H3c on 2/3/15.
 */
public class NightQImageLoaderHelper {
    private static final String FILE_PREFIX = "file://";
    public static final String RES_PREFIX = "drawable://";

    /**
     * 全局的图片尺寸
     */
    public static final int PHOTO_SIZE_HUGE = DeviceUtils.screenWPixels * 2;
    public static final int PHOTO_SIZE_BIG = DeviceUtils.screenWPixels;
    public static final int PHOTO_SIZE_MIDDLE = PHOTO_SIZE_BIG / 2;
    public static final int PHOTO_SIZE_SMALL = PHOTO_SIZE_BIG / 4;

    public static final long DEFAULT_DISK_SIZE = 512 * 1024 * 1024;// 512M
    public static final File PHOTO_CACHE_FILE;

    static {
        // 真正的缓存路径
        PHOTO_CACHE_FILE = new File(StorageUtils.getAppCacheDirCachePath());
    }

    public static String getResUrl(int resId) {
        return  "drawable://" + resId;
    }

    public static String getFileUrl(String filePath) {
        if(!TextUtils.isEmpty(filePath)) {
            return FILE_PREFIX + filePath;
        }
        return "";
    }

    /**
     * 取 Orientation
     * @return
     */
    public static int getOrientationFromExif (String localPath) {
        ExifInterface exif = null;
        int orientation = 0;
        try {
            exif = new ExifInterface(localPath);
            orientation = exif.getAttributeInt("Orientation", 0);
        } catch (IOException ex) {
        }
        return orientation;
    }

    /**
     * 转图
     * @param result
     * @param orientation
     * @return
     */
    public static Bitmap rotateByOrientation (Bitmap result, int orientation) {
        // 旋转
        if(result != null && !result.isRecycled() && orientation != 0) {
            int degress = 0;
            if(orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                degress = 90;
            } else if(orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                degress = 180;
            } else if(orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                degress = 270;
            }
            result = PhotoUtils.rotateBitmap(degress, result);
        }
        return result;
    }

    /**
     * 取 Options
     * @param localPath
     * @return
     */
    public static BitmapFactory.Options getOptionFromPath (String localPath) {
        BitmapFactory.Options options = null;
        if (!TextUtils.isEmpty(localPath)) {
            File file = new File(localPath);
            if (file.exists() && file.length() > 0) {
                options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(localPath, options);
            } else {
                return null;
            }
        } else {
            return null;
        }
        return options;
    }

    /**
     * 解码
     * @param localPath
     * @param options
     * @return
     */
    public static Bitmap decodeBitmap (String localPath, BitmapFactory.Options options) {
        Bitmap result = null;
        try {
            result = BitmapFactory.decodeFile(localPath, options);
        } catch (OutOfMemoryError e) {
            Glide.get(NightQAppLib.getAppContext()).clearMemory();
            System.gc();
        }
        return result;
    }

}
