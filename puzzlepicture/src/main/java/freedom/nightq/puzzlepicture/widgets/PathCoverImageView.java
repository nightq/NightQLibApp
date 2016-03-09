package freedom.nightq.puzzlepicture.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.widget.ImageView;

import freedom.nightq.baselibrary.utils.ResourceUtils;
import freedom.nightq.puzzlepicture.R;

/**
 * 这里 画path  的蒙板
 * @see #setPath(Path) 设置path,只是为了画蒙板
 * @see #setValidArea(float, float) 设置 有效区域 的宽高
 */
public class PathCoverImageView extends ImageView {
    // 挂件的有效区域
    private float validWidth;
    private float validHeight;

    public int coverColorId = R.color.bg_white_95;
    public int coverFrameWidth = 2;
    public int coverFrameColor = 0x10000000;

    /**
     * 也有path
     */
    public Path mPath;

    /**
     * 构造函数
     * @param context
     */
    public PathCoverImageView(Context context) {
        this(context, null);
    }

    public PathCoverImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        resetImageSize();
    }

    /**
     * 设置path,只是为了画蒙板
     * @param mPath
     */
    public void setPath(Path mPath) {
        this.mPath = mPath;
    }

    /**
     * 设置 有效区域 的宽高
     *
     * @param validWidth
     * @param validHeight ： 可以处理的区域
     *
     */
    public void setValidArea(
            float validWidth,
            float validHeight) {
        this.validWidth = validWidth;
        this.validHeight = validHeight;
        resetImageSize();
    }

    /**
     * 重新显示
     */
    private void resetImageSize() {
        if (Float.compare(validWidth, 0) <= 0 || Float.compare(validHeight, 0) <= 0
                || getImageViewWidth() <= 0
                || getImageViewHeight() <= 0) {
            return;
        }
        setImageBitmap(null);
    }

    /**
     * 重绘函数
     * @param canvas
     */
    @Override
    public void onDraw(Canvas canvas) {
        // 画蒙板
        if (mPath != null) {
            // 画蒙板
            canvas.save();
            canvas.clipPath(mPath, Region.Op.DIFFERENCE);
            canvas.drawColor(ResourceUtils.getColorResource(coverColorId));
            canvas.restore();
            // 画路径
            Paint mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(coverFrameWidth);
            mPaint.setColor(coverFrameColor);
            canvas.drawPath(mPath, mPaint);
        }
    }

    /**
     * getImageViewWidth
     * @return
     */
    public float getImageViewWidth() {
        if (getWidth() <= 0) {
            return 0;
        }
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    /**
     * getImageViewHeight
     * @return
     */
    public float getImageViewHeight() {
        if (getHeight() <= 0) {
            return 0;
        }
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

}
