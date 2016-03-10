package freedom.nightq.widgets.tagview;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;


/**
 * Created by Nightq on 16/1/4.
 */
public class TagGroupPointView extends ImageView implements ValueAnimator.AnimatorUpdateListener{

    public TagGroupPointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init ();
    }

    ValueAnimator valueAnimator;

    private void init () {
        setImageResource(R.drawable.bg_tag_point_w_dot_b_shadow);
        valueAnimator = ValueAnimator.ofObject(new IntEvaluator(), 0, 100);
        valueAnimator.setDuration(800);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(this);
        valueAnimator.start();
    }

    /**
     * pause
     */
    public void pauseAnim () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (valueAnimator.isRunning()) {
                valueAnimator.pause();
            }
        }
    }

    /**
     * resume
     */
    public void resumeAnim () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (valueAnimator.isPaused()) {
                valueAnimator.resume();
            }
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int current = (Integer) valueAnimator.getAnimatedValue();
        level = current * 100 % 10000;
        refreshDrawable();
    }

    private int level = 0;

    private void refreshDrawable() {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            drawable.setLevel(level);
        }
    }
}
