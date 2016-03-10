package freedom.nightq.puzzlepicture.fragment;

import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.util.concurrent.ConcurrentHashMap;

import freedom.nightq.baselibrary.NightQAppLib;
import freedom.nightq.baselibrary.threadPool.NormalEngine;
import freedom.nightq.baselibrary.utils.imageLoader.NightQImageLoader;
import freedom.nightq.puzzlepicture.fragment.ProcessPictureFragment.DataSource;
import freedom.nightq.puzzlepicture.model.FilterPicResult;
import freedom.nightq.puzzlepicture.model.ProcessComposeModel;
import freedom.nightq.puzzlepicture.model.ProcessPicModel;
import freedom.nightq.puzzlepicture.utils.BitmapCacheUtil;
import freedom.nightq.puzzlepicture.utils.ComposeUtil;

/**
 * Created by Nightq on 15/12/9.
 */
public class ProcessPicFragmentHelper implements
        ViewTreeObserver.OnGlobalLayoutListener {

    public ProcessPictureFragment mFragment;

    /**
     * 加载重试
     */
    public int retryFilterForFailed = 0;
    private static final int RetryMax = 3;


    /**
     * 处理的图片数据，包括图片，位置，贴纸等
     */
    public ProcessPicModel mProcessPicModel;
    public ProcessComposeModel mProcessComposeModel;
    public int dataCount = 0;

    /**
     * 图片缓存
     */
    public ConcurrentHashMap<String, Bitmap> bmpCache = new ConcurrentHashMap<>();
    /**
     * 原版式中的框位置
     */
//    public Path mPath;
    /**
     * 要显示的框
     */
    public Path mDisplayPath;
    public RectF mDisplayRectF;

    public ProcessPicFragmentHelper(ProcessPictureFragment processPictureFragment) {
        mFragment = processPictureFragment;
        onCreate();
    }

    /**
     * on create
     */
    public void onCreate () {
        if (!(mFragment.getActivity() instanceof DataSource)) {
            return;
        }
        mProcessPicModel = ((DataSource) mFragment.getActivity()).getDataByPosition(mFragment.position);
        mProcessComposeModel = ((DataSource) mFragment.getActivity()).getComposeModel();
        dataCount = ((DataSource) mFragment.getActivity()).getDataCount();
    }

    /**
     * 获取当前界面显示的位置
     * @return
     */
    public int getVisiblePosition () {
        if (mFragment != null
                && mFragment.getActivity() != null
                && mFragment.getActivity() instanceof DataSource) {
            return ((DataSource) mFragment.getActivity()).getCurrentItem();
        }
        return -1;
    }

    /**
     * 返回数据是否正常
     * @return
     */
    public boolean isDataValid () {
        return mProcessPicModel != null
                && (dataCount == 1 || (dataCount > 1 && dataCount < 5 && mProcessComposeModel != null));
    }

    /**
     * initAfterView
     */
    public void initAfterView () {
        if (!isDataValid()) {
            return;
        }
        // 加载 mProcessPicModel 到 view

        mFragment.imageView.setTouchable(false);
        mFragment.imageView.setShowClip(true);
        // 加载图片
        loadFilterAndPendantBmp();

        mFragment.getView()
                .getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        loadFilterAndPendantBmp();
        if (mFragment != null && mFragment.getView() != null) {
            mFragment.getView()
                    .getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }

    /**
     * 根据 mProcessPicModel 显示图片
     */
    private void setPolygonView () {
        if (!isDataValid()) {
            return;
        }
        //todo nightq now debug
        if (dataCount == 1 && false) {
            // clear
            mDisplayRectF = null;
            mDisplayPath = null;
            mFragment.imageView.setPadding(0, 0, 0, 0);
            // set path and Model
            mFragment.imageView.setPathAndPosModel( null, null);
            mFragment.pathCoverImageView.setPath(null);

            // 如果是单图的话，显示CENTER_INSIDE 并且不裁剪了
            // TODO NIGHTQ NOW 单图的时候 centerinside 的区域
            mFragment.imageView.setScaleForAttacher(ImageView.ScaleType.CENTER_INSIDE);
            mFragment.imageView.setPolygonPicture(false);

        } else {
            int targetWidth = ComposeUtil.getComposeWidth();
            int targetHeight = ComposeUtil.getComposeWidth();
            if (mFragment.imageView.getWidth() > 0
                    && mFragment.imageView.getHeight() > 0) {
                // 使用实际的size
                targetWidth = mFragment.imageView.getWidth();
                targetHeight = mFragment.imageView.getHeight();
            }
            // 取到要显示的框
            try {
                mDisplayPath = mProcessComposeModel.mComposeModel.getPolygonPath(
                        targetWidth,
                        targetHeight,
                        mFragment.position);
            } catch (Exception e) {

            }

            if (mDisplayPath == null) {
                return;
            }
            mDisplayRectF = new RectF();
            mDisplayPath.computeBounds(mDisplayRectF, true);
            mFragment.imageView.setPadding(
                    (int)mDisplayRectF.left,
                    (int)mDisplayRectF.top,
                    targetWidth - (int) mDisplayRectF.right,
                    targetHeight - (int) mDisplayRectF.bottom);

            // set path and Model
            mFragment.imageView.setPathAndPosModel(
                    mDisplayPath,
                    mProcessPicModel
            );
            mFragment.pathCoverImageView.setPath(mDisplayPath);
        }
    }

    /**
     * 返回显示区域的长宽
     * @return
     */
    public float getDisplayRectWidth () {
        return mDisplayRectF != null ? mDisplayRectF.width() : ComposeUtil.getComposeWidth();
    }
    public float getDisplayRectHeight () {
        return mDisplayRectF != null ? mDisplayRectF.height() : ComposeUtil.getComposeWidth();
    }

    /**
     * 加载滤镜贴纸 bmp
     * UIThread
     */
    public void loadFilterAndPendantBmp () {
        setPolygonView();
        if (mFragment == null) {
            return;
        }
        if (getVisiblePosition() != mFragment.position) {
            // 显示的不是当前界面，就不加载
            return;
        } 
        // load filter pic
        refreshFilterToView();
    }

    /**
     * 刷新滤镜
     */
    public void refreshFilterToView() {
        if (getVisiblePosition() != mFragment.position) {
            // 显示的不是当前界面，就不加载
            recycle();
            return;
        }
        if (!isDataValid()) {
            return;
        }
        // get from cache
        Bitmap bmp = BitmapCacheUtil.getValidBmpFromCache(mProcessPicModel);
        if (bmp != null && !bmp.isRecycled()) {
            loadImageToView(new FilterPicResult(null, bmp), false);
            return;
        }
        // get from tmp file
        mFragment.pacProgressBar.setVisibility(View.VISIBLE);
        NormalEngine.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                if (mProcessPicModel != null
                        && mFragment != null
                        && mFragment.getActivity() != null) {
                    // 首先得到 生成后的key，用来辨别是否是同一个图片。
                    final FilterPicResult filterPicResult = getFilterPicResult(false);
                    NightQAppLib.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadImageToView(filterPicResult, true);
                        }
                    });
                }
            }
        });
    }

    /**
     * 滤镜处理
     * @BackgroundThread
     * @param needSave true:需要保存到文件，并且不需要bmp
     *                 false:需要取到bmp，不需要文件
     * @return
     */
    @Nullable
    private FilterPicResult getFilterPicResult(boolean needSave) {
        final FilterPicResult filterPicResult;
        // 一定有滤镜，并且没缓存。
        String originPath = mProcessPicModel.getCutedPath();
        // 先看强引用
        Bitmap originBmp;
        if ((originBmp=bmpCache.get(originPath)) == null
                || originBmp.isRecycled()) {
            // TODO NIGHTQ NOW 然后弱引用。 如果使用相同尺寸就可以同时使用软引用缓存，不然就只能使用强引用缓存
            originBmp = BitmapCacheUtil.getValidBmpFromCache(originPath);
            if (originBmp == null
                    || originBmp.isRecycled()) {
                // 然后加载
                originBmp = NightQImageLoader.loadBmpFromUriForBigPicture(
                        mFragment.getActivity(),
                        ComposeUtil.getComposeVisibleWidth(),
                        ComposeUtil.getComposeVisibleWidth(),
                        originPath);
                // TODO NIGHTQ NOW 然后弱引用。 如果使用相同尺寸就可以同时使用软引用缓存，不然就只能使用强引用缓存
                BitmapCacheUtil.saveValidBmpToCache(originPath, originBmp);
            }
        }
        bmpCache.clear();
        if (originBmp != null
                && !originBmp.isRecycled()
                && mFragment != null
                && mFragment.isResume()) {
            bmpCache.put(originPath, originBmp);
        }
        filterPicResult = new FilterPicResult(originPath, originBmp);
        return filterPicResult;
    }

    /**
     * 正确的加载对应的图片
     * 加载bmp 到view 中
     */
    public void loadImageToView (FilterPicResult filterPicResult, boolean needCompare) {
        if (mFragment == null
                || mFragment.getActivity() == null) {
            return;
        }
        mFragment.pacProgressBar.setVisibility(View.GONE);
        if (needCompare
                // 需要比较的话就一个一个比较
                && (filterPicResult == null
                || filterPicResult.result == null
                || TextUtils.isEmpty(filterPicResult.key)
                || !filterPicResult.key.equalsIgnoreCase(
                mProcessPicModel.getCutedPath()))
                ) {
            // 图片变了或者数据不对了就不显示
            //Failed neeed reload
            if (retryFilterForFailed < RetryMax) {
                retryFilterForFailed ++;
                NightQAppLib.getInstance().getHandler().removeCallbacks(retryRunnable);
                NightQAppLib.getInstance().getHandler().postDelayed(retryRunnable, 100);
            }
            return;
        }
        retryFilterForFailed = 0;
        // 不需要校验或者校验成功就 显示
        mFragment.imageView.setImageBitmap(filterPicResult.result);
        if (dataCount == 1) {
            // TODO NIGHTQ NOW 单图的时候 centerinside 的区域
            mFragment.pathCoverImageView.setValidArea(
//                filterPicResult.result,
                    getDisplayRectWidth(),
                    getDisplayRectHeight());
        } else {
            mFragment.pathCoverImageView.setValidArea(
//                filterPicResult.result,
                    getDisplayRectWidth(),
                    getDisplayRectHeight());
        }
    }

    /**
     * 重新加载的 runnable
     */
    Runnable retryRunnable = new Runnable() {

        @Override
        public void run() {
            refreshFilterToView();
        }
    };

    public void destory() {
        mFragment = null;
    }


    public void showLoading() {
        NightQAppLib.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mFragment != null && mFragment.pacProgressBar != null)
                    mFragment.pacProgressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    public void hideLoading() {
        NightQAppLib.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mFragment != null && mFragment.pacProgressBar != null)
                    mFragment.pacProgressBar.setVisibility(View.GONE);
            }
        });
    }


    /**
     * for bitmap recycle
     */
    public void recycle () {
        if (bmpCache != null) {
            bmpCache.clear();
        }
        if (mFragment == null) {
            return;
        }
    }

}
