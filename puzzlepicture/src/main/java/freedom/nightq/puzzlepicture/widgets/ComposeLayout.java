package freedom.nightq.puzzlepicture.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import freedom.nightq.baselibrary.NightQAppLib;
import freedom.nightq.baselibrary.utils.imageLoader.NightQImageLoader;
import freedom.nightq.puzzlepicture.R;
import freedom.nightq.puzzlepicture.enums.ComposeType;
import freedom.nightq.puzzlepicture.model.ProcessComposeModel;
import freedom.nightq.puzzlepicture.model.ProcessPicModel;
import freedom.nightq.puzzlepicture.utils.BitmapCacheUtil;
import freedom.nightq.puzzlepicture.utils.ComposeImageLoader;
import freedom.nightq.puzzlepicture.utils.ComposeUtil;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Nightq on 15/12/5.
 */
public class ComposeLayout
        extends RelativeLayout
        implements OnPicLoadListener {

    /**
     * 版式 key
     */
    public ComposeType mComposeType;
    /**
     * 几个view
     */
    public List<PolygonImageView> imageViews;
    /**
     * 蒙板view
     */
    public ImageView imgCover;
    /**
     * 所有数据
     */
    public ProcessComposeModel mProcessComposeModel;
    /**
     * 当前版式的path
     */
    public List<Path> currentPath;

    /**
     * 图片加载监听
     */
    public OnLoadFinishListener mOnLoadFinishListener;
    // 加载结果
    public SparseArrayCompat<Boolean> mOnPicLoadResult = new SparseArrayCompat<>();

    private PhotoViewAttacher.OnPhotoTapListener mOnPhotoTapListener;

    public ComposeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置图片加载监听
     * @param onLoadFinishListener
     */
    public void setOnLoadFinishListener(OnLoadFinishListener onLoadFinishListener) {
        this.mOnLoadFinishListener = onLoadFinishListener;
    }

    /**
     * 设置点击事件
     * @param mOnPhotoTapListener
     */
    public void setOnPhotoTapListener(PhotoViewAttacher.OnPhotoTapListener mOnPhotoTapListener) {
        this.mOnPhotoTapListener = mOnPhotoTapListener;
    }

    /**
     * 设置数据来源
     * 1.先设置有几张图片
     * 2.设置版式
     * 3.显示图片
     * @param processComposeModel  mComposeModel 和 piclist 不为空
     */
    public boolean setProcessComposeModel (
            ComposeType composeType,
            ProcessComposeModel processComposeModel) {
        setComposeType(composeType);
        mProcessComposeModel = processComposeModel;
        // 先设置变化 这里耗时
        refreshComposeModel();
        // 设置图片
        return showPictures(true, null);
    }

    @Override
    public void onPicLoad(
            final PolygonImageView view,
            final String localPath,
            @Nullable Bitmap result,
            final boolean isSuccess) {
        NightQAppLib.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!ComposeImageLoader.checkImageViewValid(view, localPath)) {
                    // 如果无效就直接不处理结果
                    return;
                }
                if (imageViews == null && imageViews.size() <= 0) {
                    return;
                }
                // 设置结果
                int position = (Integer) view.getTag(R.id.item_view_tag_position);
                mOnPicLoadResult.put(position, isSuccess);
                // 没加载完就不通知
                if (mProcessComposeModel == null
                        || mProcessComposeModel.mPicList == null) {
                    return;
                } else {
                    for (int i=0; i<mProcessComposeModel.mPicList.size(); i++) {
                        if (mOnPicLoadResult.get(i) == null) {
                            // 为空表示还没加载完
                            return;
                        }
                    }
                }
                // 加载完了通知
                if (mOnLoadFinishListener != null) {
                    mOnLoadFinishListener.onLoadFinish(isLoadedSucceed());
                }
            }
        });
    }

    /**
     * 加载成功了吗
     * @return
     */
    public boolean isLoadedSucceed () {
        if (mProcessComposeModel == null
                || mProcessComposeModel.mPicList == null
                || mOnPicLoadResult.size() < mProcessComposeModel.mPicList.size()) {
            return false;
        }
        boolean isSuccess = true;
        for (int i=0; i<mProcessComposeModel.mPicList.size(); i++) {
            isSuccess &= mOnPicLoadResult.get(i, false);
        }
        return isSuccess;
    }

    /**
     * 设置有几张图
     * @param composeType
     */
    private void setComposeType(ComposeType composeType) {
        if (imageViews == null) {
            imageViews = new ArrayList<>();
            addPolygonImageViewToLayout(getViewByPosition(getContext(), 0));
            addPolygonImageViewToLayout(getViewByPosition(getContext(), 1));
            addPolygonImageViewToLayout(getViewByPosition(getContext(), 2));
            addPolygonImageViewToLayout(getViewByPosition(getContext(), 3));
        }
        if (imgCover == null) {
            imgCover = new ImageView(getContext(), null);
            imgCover.setId(R.id.compose_image_cover);
            addViewToLayout(imgCover);
        }
        mComposeType = composeType;
        switch (composeType) {
            case ComposeFourPic:
                imageViews.get(0).setVisibility(View.VISIBLE);
                imageViews.get(1).setVisibility(View.VISIBLE);
                imageViews.get(2).setVisibility(View.VISIBLE);
                imageViews.get(3).setVisibility(View.VISIBLE);
                break;
            case ComposeThreePic:
                imageViews.get(0).setVisibility(View.VISIBLE);
                imageViews.get(1).setVisibility(View.VISIBLE);
                imageViews.get(2).setVisibility(View.VISIBLE);
                imageViews.get(3).setVisibility(View.GONE);
                break;
            case ComposeTwoPic:
                imageViews.get(0).setVisibility(View.VISIBLE);
                imageViews.get(1).setVisibility(View.VISIBLE);
                imageViews.get(2).setVisibility(View.GONE);
                imageViews.get(3).setVisibility(View.GONE);
                break;
            default:
                imageViews.get(0).setVisibility(View.VISIBLE);
                imageViews.get(1).setVisibility(View.GONE);
                imageViews.get(2).setVisibility(View.GONE);
                imageViews.get(3).setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 加载版式 里面 的大图
     * @param showAll 显示全部
     * @param showPosition 显示部分时显示哪些
     */
    public boolean showPictures (boolean showAll, int[] showPosition) {
        if (imageViews == null
                || mProcessComposeModel == null
                || mProcessComposeModel.mPicList == null) {
            return false;
        }
        if (showAll) {
            // 显示图片
            mOnPicLoadResult.clear();
        } else if (showPosition != null) {
            for (int tmp : showPosition) {
                mOnPicLoadResult.delete(tmp);
            }
        }
//        long start = System.currentTimeMillis();
        for (int i=0; i<mProcessComposeModel.mPicList.size(); i++) {
            if (!showAll
                    && showPosition != null && showPosition.length > 0) {
                // 部分显示
                boolean needShow = false;
                for (int position : showPosition) {
                    if (position == i) {
                        needShow = true;
                        break;
                    }
                }
                if (!needShow) {
                    // 不能显示，那就下一个
                    continue;
                }
            }
            // 显示图片
            showImageFromData(
                    i, mProcessComposeModel.mPicList.get(i),
                    imageViews.get(i));
        }
//        LogUtils.e("nightq", " showPictures time = " + (System.currentTimeMillis() - start));
        return true;
    }

    /**
     * 显示图片，并设置数据和 位置到tag 中，为了点击处理
     * @param position
     * @param bean
     * @param imageView
     */
    private void showImageFromData (
            int position,
            ProcessPicModel bean,
            PolygonImageView imageView) {
        if (imageView != null) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setTag(R.id.item_view_tag_position, position);
        }

        String localPath = bean.getCutedPath();
        Bitmap bitmap = BitmapCacheUtil.getValidBmpFromCache(localPath);
        if (bitmap != null
                && !bitmap.isRecycled()) {
            imageView.setTag(R.id.item_view_tag, localPath);
            imageView.setImageBitmap(bitmap);
            onPicLoad(
                    imageView,
                    localPath,
                    null,
                    true);
        } else {
            ComposeImageLoader.loadLocalPictureToComposeLayout(bean.getCutedPath(),
                    imageView, this);
        }
    }
    /**
     * update 每个图片所显示的位置
     * 设置每一个小图应该在的位置
     * 所以必须先 初始化有几个图了。
     */
    public void refreshComposeModel () {
        if (mProcessComposeModel == null
                || mProcessComposeModel.mComposeModel == null) {
            throw new ExceptionInInitializerError("mProcessComposeModel is EMPTY error!");
        }
        currentPath = mProcessComposeModel.mComposeModel.getPolygonsPath(
                ComposeUtil.getComposeWidth(),
                ComposeUtil.getComposeWidth());
        RectF rectF;
        for (int i=0; i<currentPath.size(); i++) {
            rectF = new RectF();
            currentPath.get(i).computeBounds(rectF, true);
            setViewLayoutParam(imageViews.get(i), rectF);
            // set path and Model
            imageViews.get(i).setPathAndPosModel(
                    currentPath.get(i),
                    mProcessComposeModel.mPicList.get(i)
            );
        }
        if (imgCover != null) {
            int resId = mProcessComposeModel.mComposeModel.getFrameDrawableId();
            if (resId <= 0) {
                imgCover.setImageBitmap(null);
            } else {
                imgCover.setImageResource(resId);
            }
        }
    }

    /**
     * 加 PolygonImageView
     * @param view
     */
    public void addPolygonImageViewToLayout (PolygonImageView view) {
        // 先设置tap
        view.setOnPhotoTapListener(mOnPhotoTapListener);
        addViewToLayout(view);
        imageViews.add(view);
    }

    /**
     * 加view
     * @param view
     */
    public void addViewToLayout (ImageView view) {
        // 先设置tap
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(
                ComposeUtil.getComposeWidth(),
                ComposeUtil.getComposeWidth());
        addView(view, rl);
    }

    public void setViewLayoutParam (PolygonImageView view,
                                    RectF rectF) {
        if (rectF != null) {
            view.setPadding(
                    (int)rectF.left,
                    (int)rectF.top,
                    ComposeUtil.getComposeWidth() - (int) rectF.right,
                    ComposeUtil.getComposeWidth() - (int) rectF.bottom);
        }
    }
    /**
     * 通过位置获取view
     * @param position
     * @return
     */
    public static PolygonImageView getViewByPosition (Context context, int position) {
        PolygonImageView view = new PolygonImageView(context, null);
        view.setTag(R.id.item_view_tag_position, position);
        switch (position) {
            case 0:
                view.setId(R.id.compose_image_0);
                break;
            case 1:
                view.setId(R.id.compose_image_1);
                break;
            case 2:
                view.setId(R.id.compose_image_2);
                break;
            case 3:
                view.setId(R.id.compose_image_3);
                break;
            case 4:
                view.setId(R.id.compose_image_4);
                break;
            case 5:
                view.setId(R.id.compose_image_5);
                break;
        }
        return view;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    public interface OnLoadFinishListener {
        void onLoadFinish(boolean isSuccess);
    }

}
