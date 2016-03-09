package freedom.nightq.puzzlepicture.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import freedom.nightq.puzzlepicture.BuildConfig;
import freedom.nightq.puzzlepicture.model.PositionScaleModel;
import freedom.nightq.puzzlepicture.model.ProcessPicModel;
import freedom.nightq.puzzlepicture.utils.ComposeUtil;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.view.MotionEvent.ACTION_DOWN;

/**
 * Created by Nightq on 15/12/4.
 *
 * 1.不是多边形的时候，不用 setPathAndPosModel
 * 2.是多边形，必须设置
 * @see #setPathAndPosModel(Path, ProcessPicModel) 设置裁切的路径和图片缩放偏移的位置
 * 废弃之前的这两个
 * @Deprecated
 * @see #setPath(Path)
 * @Deprecated
 * @see #setDefaultModel(ProcessPicModel)
 */
public class PolygonImageView extends ImageView
        implements View.OnTouchListener,
        PhotoViewAttacher.OnMatrixChangedListener, ViewTreeObserver.OnGlobalLayoutListener {

    public PhotoViewAttacher mAttacher;
    Paint mPaint;
    Path mPath;
    Region mRegion;
    RectF mRectF;
    PositionScaleModel mCenterCropModel;

    public ProcessPicModel mProcessPicModel;

    /**
     * 是不是多边形，如果只显示原图不裁剪，就false
     */
    private boolean isPolygonPicture = true;

    /**
     * 需要在初始化成功之后猜保存。不然不保存。
     */
    private boolean isInited = false;
    // 是否 GlobalLayout 了
    private boolean onGlobalLayouted = false;
    // 是否能touch
    private boolean canTouch = true;
    // base scale
//    private float mBaseScale = 1f;

    // 是否显示裁剪掉的部分
    private boolean showClip = false;

    public PolygonImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init (){
        // Set the Drawable displayed
//        Drawable bitmap = getResources().getDrawable(R.drawable.brannan);
//        setImageDrawable(bitmap);
        // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
        mAttacher = new PhotoViewAttacher(this);
        // 这样能保证每次记录的数据都是在撑满view 之后的 scale
        setScaleForAttacher(ScaleType.CENTER_CROP);
        // @WARN 重写 setOnTouchListener。为了单独拦截处理touch
        setOnTouchListener(this);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);

        mAttacher.setOnMatrixChangeListener(this);

        ViewTreeObserver observer = getViewTreeObserver();
        if (null != observer)
            observer.addOnGlobalLayoutListener(this);
    }

    /**
     * 设置默认的
     */
    public void setScaleForAttacher (ScaleType type) {
        mAttacher.setScaleType(type);
    }

    /**
     * 设置是否是多边形
     * @param polygonPicture
     */
    public void setPolygonPicture(boolean polygonPicture) {
        isPolygonPicture = polygonPicture;
    }

    /**
     * 设置触摸
     */
    public void setTouchable (boolean canTouch) {
        this.canTouch = canTouch;
    }

    /**
     * 设置 base scale
     */
    @Deprecated
//    public void setBaseScale(float mBaseScale) {
//        this.mBaseScale = mBaseScale;
//    }

    /**
     * 是否显示裁剪掉的部分
     * @param showClip
     */
    public void setShowClip(boolean showClip) {
        this.showClip = showClip;
    }

    @Override
    public void onGlobalLayout() {
        //
        onGlobalLayouted = true;
        // 布局完之后刷新一下。
        refreshDefaultScale();
    }

    @Override
    public void onMatrixChanged(RectF rect) {
        saveToPicModel();
    }

    /**
     * 设置裁切的路径和图片缩放偏移的位置
     */
    public void setPathAndPosModel (Path path, ProcessPicModel model) {
        setDefaultModel(model);
        setPath(path);
    }

    /**
     * 必须set path
     */
    private void setPath (Path path) {
        mPath = path;
        if (path != null) {
            mRectF = new RectF();
            path.computeBounds(mRectF, true);
            mRegion = new Region();
            mRegion.setPath(path,
                    new Region(
                            (int) mRectF.left,
                            (int) mRectF.top,
                            (int) mRectF.right,
                            (int) mRectF.bottom));
        } else {
            mRegion = null;
            mRectF = null;
        }
        refreshDefaultScale();
    }

    public int bitmapWidth = -1;
    public int bitmapHeight = -1;
    @Override
    public void setImageBitmap(Bitmap bm) {
        // 翻转图片
        if(mProcessPicModel.mPosScaModel != null) {
            bm = mProcessPicModel.mPosScaModel.getProcessBmpWithTurnAndRotate(bm);
        }

        if (bm != null) {
            bitmapWidth = bm.getWidth();
            bitmapHeight = bm.getHeight();
        }

        super.setImageBitmap(bm);
        // 然后设置 之前保存的或者新生成的 matrix
        if (bm != null) {
            refreshDefaultScale();
        }
    }

    /**
     * 重新计算scale
     * 在设置 bitmap 或者 path 的时候调用，
     * 这个时候用来设置 图片显示的位置，以及设置图片可以 scale 的范围
     */
    private void refreshDefaultScale () {
        if (getWidth() <= 0 && getHeight() <= 0
                && !onGlobalLayouted) {
            // 不然没有 layout 的话，还是会被 reset
            return;
        }
        // 不是多边形直接显示
        if (!isPolygonPicture) {
            mAttacher.update();
            return;
        }
        // get center crop model, for set scale level
        mCenterCropModel = ComposeUtil.getCenterCropModel(
                getImageViewWidth(), getImageViewHeight(), bitmapWidth, bitmapHeight);

        // 设置合适大小
        if (mProcessPicModel == null
                || mCenterCropModel == null) {
            // nothing
        } else {
            isInited = false;
            // 先居中算出base matrix,并且会重置所有 matrix
            mAttacher.update();
            // 设置scale 范围
            // 计算最大可以多大，默认为2f, 至少可以两倍，或者原图缩小多少可以放大到原图大小
            // 但是也不能放太大 最多4f result = {2.5-4}这个区间
            float maxScaleFromBmp = Math.max(2.5f, 1 / mCenterCropModel.scale);
            maxScaleFromBmp = Math.min(4f, maxScaleFromBmp);
            mAttacher.setScaleLevels(
                    1f,
                    1.1f,
                    maxScaleFromBmp);

            // 设置区域
            PositionScaleModel model = mProcessPicModel.mPosScaModel;
            if (!checkSetScaleValid()) {
                return;
            }
            float transfromX = getTransformX(model);
            float transfromY = getTranformY(model);

            // 1.先 base scale 居中放大
//            mAttacher.setScale(mBaseScale,
//                    getImageViewWidth() / 2,
//                    getImageViewHeight() / 2,
//                    false);

            // check scale and set 只有不能触摸的才可以重新设置。
            if (!canTouch && Float.compare(mAttacher.getMaximumScale(), model.scale) < 0) {
                mAttacher.setMaximumScale(model.scale * 1.1f);
            }

            if (!canTouch && Float.compare(mAttacher.getMinimumScale(), model.scale) > 0) {
                mAttacher.setMinimumScale(model.scale * 0.9f);
            }

            // 2.model scale 居中放大
            mAttacher.setScale(model.scale,
                    getImageViewWidth() / 2,
                    getImageViewHeight() / 2,
                    false);
            // 3.然后位移
            mAttacher.onDrag(transfromX, transfromY);
            isInited = true;
//            LogUtils.e("nightq", "refreshDefaultScale old model scale = " + model.scale
//                    + " transfromX = " + transfromX
//                    + " transfromY = " + transfromY);
            invalidate();

//            saveToPicModel();
        }
    }

    /**
     * 检查是否可以设置scale
     * @return
     */
    private boolean checkSetScaleValid () {
        return !(bitmapHeight <= 0 || bitmapWidth <= 0
                || mCenterCropModel == null);
    }

    /**
     * 获取 移动的位置
     * @param model
     * @return
     */
    private float getTransformX(PositionScaleModel model) {
        return model.getTransformX(getImageViewWidth());// * bitmapWidth * mCenterCropModel.scale;
    }

    private float getTranformY(PositionScaleModel model) {
        return model.getTransformY(getImageViewHeight());// * bitmapHeight * mCenterCropModel.scale;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPath != null
                && isPolygonPicture
                && !showClip) {
            canvas.clipPath(mPath);
//            canvas.clipPath(mPath, Region.Op.UNION);
//            canvas.drawColor(Color.RED);
        }
        super.onDraw(canvas);
        if (BuildConfig.DEBUG) {
//            if (mPath != null) {
//                mPaint.setColor(Color.RED);
//                canvas.drawPath(mPath, mPaint);
//            }
        }

//         漫画边框
//        mProcessPicModel.drawCartoonBorder(canvas);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (!canTouch) {
            return false;
        }
        if (motionEvent.getAction() == ACTION_DOWN) {
            if (mPath == null || mRegion == null
                    || mRegion.contains(
                    (int)motionEvent.getX(),
                    (int)motionEvent.getY())) {
                processTouch(view, motionEvent);
                return true;
            } else {
                return false;
            }
        } else {
            processTouch(view, motionEvent);
            return true;
        }
    }

    private void processTouch (View view, MotionEvent motionEvent) {
        mAttacher.onTouch(view, motionEvent);
    }

    public void setOnPhotoTapListener(PhotoViewAttacher.OnPhotoTapListener listener) {
        mAttacher.setOnPhotoTapListener(listener);
    }

    public void setOnViewTapListener(PhotoViewAttacher.OnViewTapListener listener) {
        mAttacher.setOnViewTapListener(listener);
    }

    /**
     * 初始化
     */
    private void setDefaultModel(ProcessPicModel processPicModel) {
        mProcessPicModel = processPicModel;
    }

    /**
     * 保存
     */
    public void saveToPicModel () {
        if (mProcessPicModel == null
                || !isInited
                || !isPolygonPicture || !canTouch) {
            return;
        }
        PositionScaleModel posHolder = getCurrentPosScaModel();
        if (posHolder != null) {
            if (Float.compare(posHolder.scale, mAttacher.getMaximumScale()) > 0) {
                // 如果放大太多就缩回来
                mAttacher.setScale(mAttacher.getMaximumScale(), true);
                return;
            }
            mProcessPicModel.mPosScaModel.savePosAndScaleModel(posHolder);
            if (!checkSetScaleValid()) {
                return;
            }
//            float x = getTransformX(posHolder);
//            float y = getTranformY(posHolder);
//            LogUtils.e("nightq",
//                    "保存的model scale = " + posHolder.scale +
//                            " x = " + x + " y = " + y
//                            + " posHolder x = " + posHolder.getTransformX(getImageViewWidth())
//                            + " posHolder y = " + posHolder.getTransformY(getImageViewHeight()));
        }
    }

    /**
     * 获取当前显示的图片偏移和scale
     * @return
     */
    public PositionScaleModel getCurrentPosScaModel () {
        if (mCenterCropModel == null) {
            return null;
        }
        return ComposeUtil.getCurrentPosScaModel(
                mAttacher,
                getImageViewWidth(), getImageViewHeight());
    }


    /**
     * getImageViewWidth
     * @return
     */
    public float getImageViewWidth() {
        return mRectF != null ? (int)mRectF.width() : getWidth() - getPaddingLeft() - getPaddingRight();
    }

    /**
     * getImageViewHeight
     * @return
     */
    public float getImageViewHeight() {
        return mRectF != null ? (int)mRectF.height() : getHeight() - getPaddingTop() - getPaddingBottom();
    }
}
