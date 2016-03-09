package freedom.nightq.baselibrary.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import freedom.nightq.baselibrary.NightQAppLib;
import freedom.nightq.baselibrary.model.ImageOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * MINI_KIND: 512 x 384 thumbnail
 * MICRO_KIND: 96 x 96 thumbnail
 * Created by H3c on 1/27/15.
 */
public class PhotoUtils {

    /**
     * 回收这个图片
     * @param bmp
     * @return
     */
    public static boolean bmpRecycle(Bitmap bmp) {
        if (bmp != null && !bmp.isRecycled()) {
            bmp.recycle();
            bmp = null;
            return true;
        }
        bmp = null;
        return false;
    }

    /**
     * 对bitmap 进行 re size
     * @param newWidth
     * @param newHeight
     * @param bmp
     * @return
     */
    public static Bitmap resizeBitmap(int newWidth, int newHeight, Bitmap bmp) {
        return resizeBitmap(newWidth, newHeight, bmp, false);
    }

    public static Bitmap resizeBitmap(int newWidth, int newHeight, Bitmap bmp,
                                      boolean fullView) {
        if (bmp == null || bmp.isRecycled()) {
            bmp = null;
            return null;
        }
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        float scaleWidth = (float) newWidth / w;
        float scaleHeight = (float) newHeight / h;

        float scale;
        if (fullView) {
            Matrix matrix = new Matrix();
            matrix.setScale(scaleWidth, scaleHeight);
            return transform(matrix, bmp, newWidth, newHeight);
        } else {
            scale = (scaleWidth > scaleHeight ? scaleHeight : scaleWidth);
            if (scale == 1) {
                return bmp;
            }
            try {
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);
                bmp = createBitmap(bmp, 0, 0, w, h, matrix);
            } catch (Exception e) {
                return bmp;
            } catch (OutOfMemoryError e) {
                return null;
            }
            return bmp;
        }
    }

    private static Bitmap transform(Matrix scaler, Bitmap source,
                                    int targetWidth, int targetHeight) {

        int deltaX = source.getWidth() - targetWidth;
        int deltaY = source.getHeight() - targetHeight;
        if ((deltaX < 0 || deltaY < 0)) {
            float scale;
            float scaleWidth = (float) targetWidth / source.getWidth();
            float scaleHeight = (float) targetHeight / source.getHeight();
            scale = (scaleWidth > scaleHeight ? scaleWidth : scaleHeight);

            Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight,
                    Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b2);

            Rect src;

            if (scaleWidth > scaleHeight) {
                scale = scaleWidth;
                src = new Rect(0, 0, source.getWidth(),
                        (int) (targetHeight / scale));
            } else {
                scale = scaleHeight;
                src = new Rect(
                        (source.getWidth() - (int) (targetWidth / scale)) / 2,
                        0,
                        source.getWidth()
                                - (source.getWidth() - (int) (targetWidth / scale))
                                / 2, (int) (targetHeight / scale));
            }
            Rect dst = new Rect(0, 0, targetWidth, targetHeight);

            c.drawBitmap(source, src, dst, null);
            source.recycle();
            try {
                c.setBitmap(null);
            } catch (Exception e) {
                // nohing
            }
            return b2;
        }
        float bitmapWidthF = source.getWidth();
        float bitmapHeightF = source.getHeight();

        float bitmapAspect = bitmapWidthF / bitmapHeightF;
        float viewAspect = (float) targetWidth / targetHeight;

        if (bitmapAspect > viewAspect) {
            float scale = targetHeight / bitmapHeightF;
            if (scale < .9F || scale > 1F) {
                scaler.setScale(scale, scale);
            } else {
                scaler = null;
            }
        } else {
            float scale = targetWidth / bitmapWidthF;
            if (scale < .9F || scale > 1F) {
                scaler.setScale(scale, scale);
            } else {
                scaler = null;
            }
        }

        if (scaler != null) {
            // this is used for minithumb and crop, so we want to filter here.
            source = createBitmap(source, 0, 0, source.getWidth(),
                    source.getHeight(), scaler);
        }

        int dx1 = Math.max(0, source.getWidth() - targetWidth);

        source = createBitmap(source, dx1 / 2, 0, targetWidth,
                targetHeight, null);

        return source;
    }

    /**
     * 创建一个bitmap
     * @param bmp
     * @param x
     * @param y
     * @param width
     * @param height
     * @param matrix
     * @return
     */
    public static Bitmap createBitmap(Bitmap bmp, int x, int y, int width,
                                      int height, Matrix matrix) {
        try {
            Bitmap bmpCopy = Bitmap.createBitmap(bmp, x, y, width, height,
                    matrix, true);
            if (bmpCopy != bmp) {
                bmp.recycle();
                bmp = bmpCopy;
            }
        } catch (Exception ex) {
            // We have no memory to rotate. Return the original bitmap.
        }
        return bmp;
    }


    /**
     * 旋转Bitmap
     */
    public static Bitmap rotateBitmap(int degrees, Bitmap b) {
        if (degrees % 360 != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) b.getWidth() / 2,
                    (float) b.getHeight() / 2);
            Bitmap newBmp = createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                    m);
            if(newBmp != b) {
                b.recycle();
            }

            b = newBmp;
        }
        return b;
    }

    /**
     * 改变图得颜色
     * @param color
     */
    public static Drawable changeDrawableColorRes(int drawable, int color) {
        Drawable bg = ResourceUtils.getDrawable(drawable);
        Drawable wrappedDrawable = DrawableCompat.wrap(bg);
        DrawableCompat.setTint(wrappedDrawable, ResourceUtils.getColorResource(color));
        return wrappedDrawable;
    }

    public static void changeBitmapColorRes(Bitmap sourceBitmap, ImageView image, int color) {
        image.setImageBitmap(changeBitmapColor(sourceBitmap, color));
    }

    public static Bitmap changeBitmapColorRes(int sourceBitmapId, int toColorRes) {
        return changeBitmapColor(sourceBitmapId, ResourceUtils.getColorResource(toColorRes));
    }

    public static Bitmap changeBitmapColor(int sourceBitmapId, int toColor) {
        Bitmap bmp = BitmapFactory.decodeResource(ResourceUtils.getResource(), sourceBitmapId);
        if(bmp != null && !bmp.isRecycled()) {
            return changeBitmapColor(bmp, toColor);
        }

        return null;
    }

    /**
     * 只能改变自己绘制的drawable颜色，主要用于btn背景，之类的
     * @param drawable
     * @param normalColor
     * @param pressColor
     * @return
     */
    public static Drawable changeDrawableColor(Drawable drawable, int normalColor, int pressColor) {
        ColorStateList csl = createColorStateList(normalColor, pressColor);
        return changeDrawableColor(drawable, csl);
    }

    /**
     * 只能改变自己绘制的drawable颜色，主要用于btn背景，之类的
     * @param drawable
     * @param csl
     * @return
     */
    public static Drawable changeDrawableColor(Drawable drawable, ColorStateList csl) {
        if(csl == null) {
            return drawable;
        }
        // 这里必须用DrawableCompat.wrap转换一次，否则不兼容4.x
        final Drawable shapeDrawable = DrawableCompat.wrap(drawable.mutate());
        DrawableCompat.setTintList(shapeDrawable, csl);
        return shapeDrawable;
    }

    public static ColorStateList createColorStateList(int normalColor, int pressColor) {
        int[][] states = new int[][] {
                new int[]{},
                new int[]{android.R.attr.state_pressed}
//                new int[] { android.R.attr.state_enabled}, // enabled
//                new int[] {-android.R.attr.state_enabled}, // disabled
//                new int[] {-android.R.attr.state_checked}, // unchecked
//                new int[] { android.R.attr.state_pressed}  // pressed
        };

        int[] colors = new int[] {
                normalColor,
                pressColor
        };

        return new ColorStateList(states, colors);
    }

    /**
     * 改变图得颜色
     * @param sourceBitmap
     */
    public static Bitmap changeBitmapColor(Bitmap sourceBitmap, int toColor) {
        Bitmap resultBitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap newBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(0x0, toColor);
        p.setAntiAlias(true);
        p.setColorFilter(filter);

        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);
        resultBitmap.recycle();
        return newBitmap;
//        return getAlphaBitmap(sourceBitmap, toColor);
    }

    //提取图像Alpha位图
//    public static Bitmap getAlphaBitmap(Bitmap sourceBitmap,int mColor) {
//
//        Bitmap mAlphaBitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true);
//        Bitmap newBitmap = Bitmap.createBitmapFromView(sourceBitmap.getWidth(), sourceBitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas mCanvas = new Canvas(newBitmap);
//        Paint mPaint = new Paint();
//
//        mPaint.setColor(mColor);
//        mPaint.setAntiAlias(true);
//
//        //从原位图中提取只包含alpha的位图
//        Bitmap alphaBitmap = sourceBitmap.extractAlpha();
//        //在画布上（mAlphaBitmap）绘制alpha位图
//        mCanvas.drawBitmap(alphaBitmap, 0, 0, mPaint);
//
//        return newBitmap;
//    }

    /**
     * 根据原图和变长绘制圆形图片
     *
     * @param source
     * @param photoWidth
     * @return
     */
    public static Bitmap createCircleImage(Bitmap source, int photoWidth) {
        if(photoWidth < 1) {
            return source;
        }

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(photoWidth, photoWidth, Bitmap.Config.ARGB_8888);
        // 产生一个同样大小的画布
        Canvas canvas = new Canvas(target);
        // 首先绘制圆形
        canvas.drawCircle(photoWidth / 2, photoWidth / 2, photoWidth / 2, paint);
        // 使用SRC_IN
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // 绘制图片
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }

    /**
     * 保存Bitmap为文件
     */
    public static boolean saveBitmapToFile(Bitmap bm, String filePath) {
        return saveBitmapToFile(bm, filePath, true);
    }
    public static boolean saveBitmapToFile(Bitmap bm, String filePath, boolean notifyUpdateGallery) {
        File f = new File(filePath);
        File parentFile = f.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        if (f.exists()) {
            f.delete();
        }

        boolean result = false;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            result = true;
            if(notifyUpdateGallery) {
                updateGallery(f.getAbsolutePath());
            }
        } catch (Exception e) {
            LogUtils.e("Save bmp to file Error:" + e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {}
            }
        }
        if (!result) {
            // 失败了删除f
            try {f.delete();} catch (Exception e) { }
        }
        return result;
    }

    /**
     * 扫描媒体数据库
     *
     * @param filename
     */
    public static void updateGallery(String filename) {// filename是我们的文件全名，包括后缀哦
//        MediaScannerConnection.scanFile(NightQAppLib.getAppContext(),
//                new String[]{filename}, null,
//                new MediaScannerConnection.OnScanCompletedListener() {
//                    public void onScanCompleted(String path, Uri uri) {
//                    }
//                });

        NightQAppLib.getAppContext().sendBroadcast(
                new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filename))));
    }

    /**
     * 获取系统拍照文件夹路径
     */
    public static String getSystemCameraTakePhotoFilePath() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (path.exists()) {
            File path1 = new File(path, "Camera/");
            if (path1.exists()) {
                path = path1;
            } else {
                File path2 = new File(path, "100ANDRO/");
                if (path2.exists()) {
                    path = path2;
                } else {
                    File path3 = new File(path, "100MEDIA/");
                    if (!path3.exists()) {
                        path3.mkdirs();
                    }
                    path = path3;
                }
            }
        } else {
            path = new File(path, "Camera/");
            path.mkdirs();
        }

        return path.getAbsolutePath();
    }

    /**
     * 获得视频的缩略图， 这个方法可能返回null
     *
     * @param filePath
     * @return
     */
    public static Bitmap getVideoThumbnail(String filePath) {
        // MediaMetadataRetriever.java 可以获取视频任意一帧
        if (!TextUtils.isEmpty(filePath)) {
            return ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
        }

        return null;
    }

    /**
     * 将任意一个图片文件转换为头像文件，主要用于原图片size过大需要压缩
     * @param filePath
     * @return
     */
    public static String getPhotoAvatar(String filePath) {
        if (TextUtils.isEmpty(filePath)){
            return null;
        }
        String resultPath = FileUtils.getPhotoCacheFile().getAbsolutePath();

        Bitmap fitAvatarBmp = ThumbnailUtils
                .createImageThumbnail(filePath, MediaStore.Images.Thumbnails.MINI_KIND);
        if(fitAvatarBmp != null && !fitAvatarBmp.isRecycled()) {
            saveBitmapToFile(fitAvatarBmp, resultPath);
            fitAvatarBmp.recycle();
        }

        return resultPath;
    }

    public static Bitmap getPhotoThumbnail(String filePath, int ortation) {
        return getPhotoThumbnail(filePath, 0, 0, ortation);
    }

    public static Bitmap getPhotoThumbnail(String filePath, int targetW, int targetH, int ortation) {
        if(!TextUtils.isEmpty(filePath)) {
            return ThumbnailUtils.createImageThumbnail(filePath, MediaStore.Images.Thumbnails.MINI_KIND);
        }

        return null;
    }


    /**
     * 通过path获取图片的尺寸和hash等数据
     * 只获取了 path 和 getTaken_at_gmt 两个值。
     */
    public static ImageOptions getImageOptions (String path, long time) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        File imageFile = new File(path);
        //文件存在且长度大于0
        if (!imageFile.exists() || imageFile.length() <= 0) {
            return null;
        }

        ImageOptions imageOptions = new ImageOptions();
        imageOptions.url = path;
        imageOptions.length = imageFile.length();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        imageOptions.width = bmOptions.outWidth;
        imageOptions.height = bmOptions.outHeight;
        String md5Hash = FileUtils.computeMd5Hash(path);
        if (md5Hash == null) {
            //计算错误不能返回错误的imageOptions
            return null;
        }

        imageOptions.md5Hash = md5Hash;
        imageOptions.taken_at_gmt = time;
        return imageOptions;
    }

    /**
     * 注意：文件会被删除
     * @param writeFile
     * @param bitmap
     * @return 返回非空 才是正确的。 需要判断。
     * @throws OutOfMemoryError
     */
    public static String compressBitmap(boolean writeFile, Bitmap bitmap,
                                        String fileName, int quality) {
        String newfilenameString = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            if (bitmap == null || bitmap.isRecycled()) {
                // do nothing
            } else if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality,
                    outputStream)) {

                if (writeFile) {
                    if (TextUtils.isEmpty(fileName)) {
                        newfilenameString = FileUtils.getPhotoCacheFilePath();
                    } else {
                        newfilenameString = fileName;
                    }
                    if (TextUtils.isEmpty(newfilenameString)) {
                        return null;
                    }
                    newfilenameString = FileUtils.saveStreamToFile(outputStream,
                            newfilenameString);
                }
            }
        } catch (OutOfMemoryError e) {
            newfilenameString = null;
        } catch (Exception e) {
            // catch all
            newfilenameString = null;
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {

            }
        }
        return newfilenameString;
    }

    /**
     * 通过content: uri获取实际地址
     * @param uri
     * @return
     */
    public static String getPath(final Uri uri) {
        final Context context = NightQAppLib.getAppContext();
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 获取要分享的微信的缩略图
     * @param srcPath
     * @return
     */
    public static byte[] getThumbImageToShare(String srcPath) {
        return compressImageBYTE(getThumbImageBitmapToShare(srcPath), 32);// 压缩好比例大小后再进行质量压缩
    }

    /**
     * 压缩图片质量
     */
    private static byte[] compressImageBYTE(Bitmap image, int target) {
        byte[] array = null;
        try {
            ByteArrayOutputStream baos = compressImageBAOS(image, target);
            array = baos.toByteArray();
            baos.close();
        } catch (Exception e) {
        }
        return array;
    }

    /**
     * 压缩图片质量
     */
    private static ByteArrayOutputStream compressImageBAOS(Bitmap image,
                                                           int target) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 90, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 85;
        while (baos.toByteArray().length > target * 1024) { // 循环判断如果压缩后图片是否大于target
            // KB,大于继续压缩

            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            if (options <= 10) {
                options --;
                if (options == 1) {
                    return null;
                }
            } else {
                options -= 10;// 每次都减少10
            }

        }
        return baos;
    }

    /**
     * 获取要分享的缩略图
     */
    private static Bitmap getThumbImageBitmapToShare(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 微信只支持大概100＊100的缩略图
        float hh = 360f;
        float ww = 360f;
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        float be = 1;// be=1表示不缩放
        if (w >= h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (w / ww);
        } else if (w <= h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (h / hh);
        }

        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = (int) be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, (int) (w / be),
                (int) (h / be));
        return bitmap;// 压缩好比例大小后再进行质量压缩
    }

    /**
     * get bmp from drawable res id
     * @return
     */
    public static Bitmap getBitmapFromDrawableId (int drawableId) {
        Drawable drawable = ResourceUtils.getDrawable(drawableId);

        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    /**
     * 压缩原图为合适尺寸
     * 这里直供上传的时候的图片压缩后存储到sd卡缓存。
     * 在每次上传完之后需要清除这个缓存
     * just for upload
     */
    public synchronized static String pressPhotoToSmaller(
            String filename,
            String destinationName,
            int target, int quality) {
        String resultFileName = null;
        try {
            Bitmap picture = null;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filename, options);

            int width = options.outWidth;
            int height = options.outHeight;

            int maxSide = width > height ? width : height;

            if (maxSide > target * 2) {
                options.inSampleSize = maxSide / target;
                options.inJustDecodeBounds = false;
                picture = BitmapFactory.decodeFile(filename, options);
                picture = PhotoUtils.resizeBitmap(target, target, picture, false);
            } else if (maxSide > target) {
                options.inSampleSize = maxSide / target;
                options.inJustDecodeBounds = false;
                picture = BitmapFactory.decodeFile(filename, options);
                picture = PhotoUtils.resizeBitmap(target, target, picture, false);
            } else {
                picture = BitmapFactory.decodeFile(filename);
            }

            if (picture != null) {
                resultFileName = PhotoUtils.compressBitmap(true, picture, destinationName, quality);
                PhotoUtils.bmpRecycle(picture);
            }
        } catch (OutOfMemoryError e) {
        } catch (Exception e) {
        }

        try {
            int defaultOrientation = ExifUtils.getExifOrientation(filename);
            ExifUtils.saveExifOrientation(defaultOrientation, resultFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultFileName;
    }

}
