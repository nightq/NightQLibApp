package freedom.nightq.baselibrary.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.Interpolator;

/**
 * Created by H3c on 7/17/15.
 */
public class AnimTranslationUtil {
    public static void translationY(View view, float fromY, float toY, long duration, Animator.AnimatorListener listener) {
        view.setVisibility(View.VISIBLE);
        view.setTranslationY(fromY);
        view.animate().translationY(toY)
                .setDuration(duration).setListener(listener).start();
    }

    public static void ObjToY(View view, float fromY, float toY, long duration, Interpolator interpolator, Animator.AnimatorListener listener) {
        ObjectAnimator oa = ObjectAnimator.ofFloat(view, "y", fromY, toY);
        oa.setDuration(duration);
        if(interpolator != null) {
            oa.setInterpolator(interpolator);
        }
        if(listener != null) {
            oa.addListener(listener);
        }
        oa.start();
    }
}
