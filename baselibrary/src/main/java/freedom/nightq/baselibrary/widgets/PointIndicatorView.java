package freedom.nightq.baselibrary.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Nightq on 15/12/9.
 */
public class PointIndicatorView extends View {

    /**
     * dot radius
     */
    public int dotRadius = 4;
    /**
     * dot color unselected
     */
    public int dotUnSelectedColor = Color.WHITE;
    /**
     * dot color selected
     */
    public int dotSelectedColor = Color.BLUE;
    /**
     * dot divider space
     */
    public int dotDividerSpace = 4;
    /**
     * dot number
     */
    public int dotNumber = 4;

    /**
     * dot selected
     */
    public int dotSelected = 0;

    public Paint mPaint;
    public PointIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    /**
     * 初始化
     * @param dotRadius
     * @param dotUnSelectedColor
     * @param dotSelectedColor
     * @param dotDividerSpace
     * @param dotNumber
     */
    public void initView (int dotRadius,
                          int dotUnSelectedColor,
                          int dotSelectedColor,
                          int dotDividerSpace,
                          int dotNumber) {
        this.dotRadius = dotRadius;
        this.dotUnSelectedColor = dotUnSelectedColor;
        this.dotSelectedColor = dotSelectedColor;
        this.dotDividerSpace = dotDividerSpace;
        this.dotNumber = dotNumber;
        setSelected(dotSelected);
    }

    /**
     * set selected
     */
    public void setSelected (int selected) {
        if (selected < 0) {
            selected = 0;
        } else if (selected >= dotNumber) {
            selected = dotNumber -1;
        }
        dotSelected = selected;
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // 画布尺寸
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        // 小点的尺寸
        int allDotWidth = dotNumber * (dotRadius * 2 + dotDividerSpace) - dotDividerSpace;
        int allDotHeight = dotRadius * 2;

        // 偏移量
        int offsetWidth = 0;
        int offsetHeight = 0;
        if (canvasHeight >= allDotHeight) {
            offsetHeight = (canvasHeight - allDotHeight) / 2;
        } else {
            offsetHeight = 0;
        }
        if (canvasWidth >= allDotWidth) {
            offsetWidth = (canvasWidth - allDotWidth) / 2;
        } else {
            offsetWidth = 0;
        }

        // 开始画点
        for (int i=0; i<dotNumber; i++) {
            if (i == dotSelected) {
                mPaint.setColor(dotSelectedColor);
            } else {
                mPaint.setColor(dotUnSelectedColor);
            }
            canvas.drawCircle(
                    // center x
                    offsetWidth + dotRadius + i*(dotRadius*2 + dotDividerSpace),
                    // center y
                    offsetHeight + dotRadius,
                    // radius
                    dotRadius,
                    mPaint);
        }
    }
}
