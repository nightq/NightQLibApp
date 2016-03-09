package freedom.nightq.baselibrary.utils.imageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.util.Map;

import freedom.nightq.baselibrary.NightQAppLib;
import freedom.nightq.baselibrary.R;
import freedom.nightq.baselibrary.utils.LogUtils;
import freedom.nightq.baselibrary.utils.PhotoUtils;
import freedom.nightq.baselibrary.utils.imageLoader.NightQImageLoaderHelper;

/**
 * Created by Nightq on 15/8/4.
 * 对image loader 的封装。在app 调用可以透明 于调用的 第三方库
 */
public class NightQImageLoader {
    /**
     * get bitmap  from resid
     *
     * @param id
     * @return
     */
    public static Bitmap loadBitmapById(int id) {
        return loadBitmapById(NightQAppLib.getAppContext(), id);
    }

    private static Bitmap loadBitmapById(Context context, int id) {
        return PhotoUtils.getBitmapFromDrawableId(id);
    }

    /**
     * get file from url
     * 下载 url。
     *
     * @param context
     * @param uri
     * @return
     */
    public static File loadFileFromUrl(Context context, String uri) {
        return loadFileFromUrl(context, uri, null);
    }


    /**
     * get file from url
     * 下载 url。
     *
     * @param context
     * @param url
     * @param secret  下载聊天图片的时候
     * @return
     */
    public static File loadFileFromUrl(Context context, String url, String secret) {
        RequestManager manager = Glide.with(context);
        FutureTarget<File> future;
        if (!TextUtils.isEmpty(secret)) {
            LazyHeaders.Builder builder = new LazyHeaders.Builder();
            builder.addHeader("share-secret", secret);
            GlideUrl glideUrl = new GlideUrl(url, builder.build());
            future = manager.load(glideUrl)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        } else {
            future = manager.load(url)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        }
        File file = null;
        try {
            file = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }


    /**
     * 是否是本地并存在
     * @param path
     * @return
     */
    public static boolean isLocalPath (String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return (file.exists() && file.length() > 0);
    }

    /**
     * get bitmap from url
     * 下载 url。
     * 最大为屏幕大
     * @param context
     * @param uri
     * @return
     */
    public static Bitmap loadScreenBitmapFromUrl(
            Context context,
            int centerCropWidth,
            int centerCropHeight,
            String uri) {
        long start = System.currentTimeMillis();
        FutureTarget<Bitmap> future = Glide.with(context)
                .load(uri)
                .asBitmap()
                .into(centerCropWidth, centerCropHeight);
        Bitmap bmp = null;
        try {
            bmp = future.get();
        } catch (Exception e) {
        } catch (OutOfMemoryError e) {
        }
        // 有得取不到，不知道为什么
        if (bmp == null && isLocalPath(uri)) {
            bmp = loadCenterCropPictureFromLocal(
                    centerCropWidth, centerCropHeight, uri);
        }
        if (bmp != null && !bmp.isRecycled()) {
            LogUtils.e("nightq",
                    uri + "loadScreenBitmapFromUrl time = " + (System.currentTimeMillis() - start)
                            + " size = " + bmp.getWidth() + " * " + bmp.getHeight());
        }
        return bmp;
    }


    /**
     * 从url 加载大图，但是不知道url 是本地还是网络
     * @return
     */
    public static Bitmap loadBmpFromUriForBigPicture(
            Context context,
            int centerCropWidth,
            int centerCropHeight,
            String url) {
        Bitmap result = null;
        if (isLocalPath(url)) {
            result = loadCenterCropPictureFromLocal(
                    centerCropWidth, centerCropHeight, url);
        } else {
            result = loadScreenBitmapFromUrl(
                    context,
                    centerCropWidth, centerCropHeight,
                    url);
        }
        return result;
    }


    /**
     * 加载本地大图，使用 CenterCrop 来撑满 width＊height，太小的图使用原图
     * @param localPath
     * @return
     */
    private static Bitmap loadCenterCropPictureFromLocal(
            int centerCropWidth,
            int centerCropHeight,
            String localPath) {
        int targetWidth = centerCropWidth;
        int targetHeight = centerCropHeight;

        BitmapFactory.Options options = NightQImageLoaderHelper.getOptionFromPath(localPath);
        if (options == null) {
            return null;
        }
        // 设置真的取图
        options.inJustDecodeBounds = false;

        // 文件原图 scale
        float widthFileScale = options.outWidth / (float) targetWidth;
        float heightFileScale = options.outHeight / (float) targetHeight;

        // 解压出来得到 最接近 target 那一边的bit然后resize
        options.inSampleSize = (int) Math.min(widthFileScale, heightFileScale);
        Bitmap result = NightQImageLoaderHelper.decodeBitmap(localPath, options);

        if (result == null || result.isRecycled()) {
            return null;
        }

        int bmpWidth = result.getWidth();
        int bmpHeight = result.getHeight();

        // 读取的图的 scale
        float widthBmpScale = bmpWidth / (float) targetWidth;
        float heightBmpScale = bmpHeight / (float) targetHeight;

        if (Float.compare(widthBmpScale, 1.1f) <= 0
                || Float.compare(heightBmpScale, 1.1f) <= 0) {
            // 1.有一边已经小于或者接近 target了。那也直接使用
        } else {
            // 2.图太大了，接下来只会使用 resize 了。这个时候 widthBmpScale  heightBmpScale 都大于1.1,
            float minScale = Math.min(widthBmpScale, heightBmpScale);
            // resize
            Bitmap tmp = PhotoUtils.resizeBitmap(
                    (int)(result.getWidth()/minScale),
                    (int)(result.getHeight()/minScale),
                    result);
            if (tmp != null && !tmp.isRecycled()) {
                result = tmp;
            }
        }
        // 旋转
        result = NightQImageLoaderHelper.rotateByOrientation(result,
                NightQImageLoaderHelper.getOrientationFromExif(localPath));
//        if (result != null && !result.isRecycled()) {
//            LogUtils.e("nightq",
//                    localPath + " loadCenterCropPictureFromLocal bmp size = "
//                            + result.getWidth() + " * " + result.getHeight());
//        }
        return result;
    }

    /**
     * get bitmap from url
     * 下载 url。
     *
     * @param context
     * @param uri
     * @return
     */
    public static Bitmap loadBitmapFromUrl(Context context, String uri) {
        FutureTarget<Bitmap> future = Glide.with(context)
                .load(uri)
                .asBitmap()
                .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        Bitmap bmp = null;
        try {
            bmp = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    /**
     * 加载并监听
     * 加载url 里面的图片到 imageview 可以设置 listener
     *
     * @param url
     */
    public static void displayPhotoWithUrl(String url, ImageView imageView) {
        Glide.with(imageView.getContext()).load(url).asBitmap().into(imageView);
//        displayPhotoWithGlide(imageView.getContext(), url, imageView, null);

    }
    public static void displayPhotoWithGlide(Context context,
                                             String url,
                                             ImageView imageView,
                                             final ImageLoadListener listener) {
        displayPhotoWithGlide(context,
                url,
                imageView,
                false,
                0,
                0,
                listener);
    }
    public static void displayPhotoWithGlide(Context context, String url,
                                             final ImageLoadListener listener) {
        displayPhotoWithGlide(context,
                url,
                null,
                false,
                0,
                0,
                listener);
    }


    /**
     * 加载并监听
     * 加载url 里面的图片到 imageview 可以设置 listener
     *
     * @param url
     * @param listener
     */
    public static void displayPhotoWithGlide(Context context,
                                             String url,
                                             ImageView imageView,
                                             int placeHoder,
                                             final ImageLoadListener listener) {
        displayPhotoWithGlide(context,
                url,
                imageView,
                false,
                0,
                placeHoder,
                listener);
    }

    /**
     * 加载并监听
     * 加载url 里面的图片到 imageview 可以设置 listener
     *
     * @param url
     * @param listener
     */
    public static void displayPhotoWithGlide(Context context,
                                             final String url,
                                             ImageView imageView,
                                             boolean isRound,
                                             float radius,
                                             int placeHoder,
                                             final ImageLoadListener listener) {
        displayPhotoWithGlide(context, url, imageView, isRound, radius, placeHoder, listener, null);
    }

    /**
     * 加载并监听
     * 加载url 里面的图片到 imageview 可以设置 listener
     *
     * @param url
     * @param listener
     */
    public static void displayPhotoWithGlide(Context context,
                                             final String url,
                                             ImageView imageView,
                                             boolean isRound,
                                             float radius,
                                             int placeHoder,
                                             final ImageLoadListener listener,
                                             Map<String, String> headerMaps) {
        if (placeHoder == 0) {
            placeHoder = R.color.img_loading_bg;
        }
        if (TextUtils.isEmpty(url)) {
            if (imageView != null) {
                imageView.setImageResource(placeHoder);
            }
            return;
        }
        if (context == null) {
            context = NightQAppLib.getAppContext();
        }

        RequestManager manager = Glide.with(context);
        BitmapTypeRequest bitmapTypeRequest;

        if (headerMaps != null) {

            LazyHeaders.Builder builder = new LazyHeaders.Builder();
            for (String key : headerMaps.keySet()) {
                builder.addHeader(key, headerMaps.get(key));
            }
            GlideUrl glideUrl = new GlideUrl(url, builder.build());
            bitmapTypeRequest =  manager.load(glideUrl).asBitmap();
        } else {
            bitmapTypeRequest =  manager.load(url).asBitmap();
        }
        if (isRound) {
            bitmapTypeRequest.transform(new CircleTransform(context, radius));
        }
        if (listener != null) {
            bitmapTypeRequest.listener(new RequestListener<String, Bitmap>() {
                @Override
                public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                    if (listener != null) {
                        listener.onFailed(e);
                    }
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    if (listener != null) {
                        listener.onSuccess(resource, model);
                    }
//                    LogUtils.e("nightq", "displayPhotoWithGlide =  " + model + "  " + resource.toString());
                    return false;
                }
            });
        }

        if (imageView != null) {
            bitmapTypeRequest.placeholder(placeHoder);
            bitmapTypeRequest.into(imageView);
        } else {
            bitmapTypeRequest.into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
//                    if (listener != null) {
//                        listener.onSuccess(resource, url);
//                    }
//                    LogUtils.e("nightq", "displayPhotoWithGlide SimpleTarget =  " + url + "  " + resource.toString());
                }
            });
        }
    }

    /**
     * 显示圆图片
     * @param url
     * @param iv
     */
    public static void displayRoundImage(String url, ImageView iv) {
        displayPhotoWithGlide(NightQAppLib.getAppContext(),
                url, iv, true, Float.MAX_VALUE,
                0,//R.mipmap.image_head_default,
                null);
    }

    /**
     * 显示用户圆头像
     * @param userAvatarUrl
     * @param imageView
     */
    public static void displayUserAvatar(String userAvatarUrl, ImageView imageView) {
        if(TextUtils.isEmpty(userAvatarUrl)) {
            imageView.setImageResource(0
                    //R.mipmap.image_head_default
                    );
        } else {
            displayRoundImage(userAvatarUrl, imageView);
        }
    }

    /**
     * 显示资源图片
     * @param resId
     * @param imageView
     */
    public static void displayResourceImage(
            int resId,
            ImageView imageView) {
        displayResourceImage(
                NightQAppLib.getAppContext(),
                resId, imageView, null);
    }

    /**
     * 显示资源图片
     * @param resId
     * @param imageView
     */
    public static void displayResourceImage(
            Context context,
            final int resId,
            ImageView imageView,
            final ImageLoadResListener imageLoadListener) {
        if (context == null) {
            context = NightQAppLib.getAppContext();
        }
        BitmapTypeRequest bitmapTypeRequest = Glide.with(context)
                .load(resId)
                .asBitmap();
        if (imageLoadListener != null) {
            bitmapTypeRequest.listener(new RequestListener<Integer, Bitmap>() {
                @Override
                public boolean onException(Exception e, Integer model, Target<Bitmap> target, boolean isFirstResource) {
                    if (imageLoadListener != null) {
                        imageLoadListener.onFailed(e);
                    }
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Integer model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    if (imageLoadListener != null) {
                        imageLoadListener.onSuccess(resource, model);
                    }
//                    LogUtils.e("nightq", "displayPhotoWithGlide =  " + model + "  " + resource.toString());
                    return false;
                }
            });
        }

        if (imageView != null) {
            bitmapTypeRequest.placeholder(R.color.img_loading_bg);
            bitmapTypeRequest.into(imageView);
        } else {
            bitmapTypeRequest.into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
//                    if (imageLoadListener != null) {
//                        imageLoadListener.onSuccess(resource, resId);
//                    }
//                    LogUtils.e("nightq", "displayPhotoWithGlide SimpleTarget =  " + url + "  " + resource.toString());
                }
            });
        }
    }

    public static void displayAnything(Object anyThing, ImageView iv) {
        Glide.with(iv.getContext()).load(anyThing).into(iv);
    }

    /**
     * 进行回收
     * @param bmp
     */
    public static void recycle (Bitmap bmp) {
        if (bmp != null && !bmp.isRecycled()) {
            bmp.recycle();
        }
    }
}
