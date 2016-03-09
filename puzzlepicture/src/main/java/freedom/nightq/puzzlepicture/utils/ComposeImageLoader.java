package freedom.nightq.puzzlepicture.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import freedom.nightq.baselibrary.NightQAppLib;
import freedom.nightq.baselibrary.threadPool.NormalEngine;
import freedom.nightq.baselibrary.utils.imageLoader.NightQImageLoader;
import freedom.nightq.puzzlepicture.R;
import freedom.nightq.puzzlepicture.widgets.OnPicLoadListener;
import freedom.nightq.puzzlepicture.widgets.PolygonImageView;

/**
 * Created by Nightq on 16/3/7.
 */
public class ComposeImageLoader {

    /**
     * 版式选择界面加载版式里面的大图，加载 center inside 的图就行。不要太大的了。
     */
    public static void loadLocalPictureToComposeLayout(final String localPath,
                                                       final PolygonImageView imageView,
                                                       final OnPicLoadListener listener) {
        imageView.setImageBitmap(null);
        imageView.setTag(R.id.item_view_tag, localPath);
        NormalEngine.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                int retryCount = 3;
                while (retryCount > 0) {
                    if (!checkImageViewValid(imageView, localPath)) {
                        return;
                    }
                    Bitmap bmp = null;
                    try {
                        // 这里显示小图，以免内存占用太多。有的手机会卡的。
                        bmp = NightQImageLoader.loadBmpFromUriForBigPicture(
                                NightQAppLib.getAppContext(),
                                ComposeUtil.getComposeVisibleWidth(),
                                ComposeUtil.getComposeVisibleWidth(),
                                localPath);
                    } catch (Exception e) {}
                    if (bmp != null && !bmp.isRecycled()) {
                        if (!TextUtils.isEmpty(localPath)) {
                            // 保存到全局的缓存，一会会用到
                            BitmapCacheUtil.saveValidBmpToCache(localPath, bmp);
                        }
                        final Bitmap result = bmp;
                        NightQAppLib.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                boolean loadResult = false;
                                try {
                                    if (imageView != null && imageView.getContext() != null
                                            && result != null && !result.isRecycled()
                                            // 有效就加载
                                            && checkImageViewValid(imageView, localPath)) {
                                        imageView.setImageBitmap(result);
                                        loadResult = true;
                                    }
                                } catch (Exception e) {

                                }
                                if (listener != null) {
                                    listener.onPicLoad(
                                            imageView,
                                            localPath,
                                            result,
                                            loadResult);
                                }
                            }
                        });
                        // 成功就返回
                        return;
                    } else {
                        retryCount --;
                    }
                }
                if (listener != null) {
                    listener.onPicLoad(imageView, null, null, false);
                }
            }
        });
    }

    /**
     * 加载图片有效
     * @return
     */
    public static boolean checkImageViewValid (ImageView imageView, String path) {
        if (imageView != null
                && imageView.getTag(R.id.item_view_tag) != null
                && imageView.getTag(R.id.item_view_tag) instanceof String
                && !TextUtils.isEmpty(path)
                && path.equalsIgnoreCase((String) imageView.getTag(R.id.item_view_tag))) {
            return true;
        }
        return false;
    }
}
