package freedom.nightq.baselibrary.utils;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.view.View;

/**
 * Created by H3c on 5/13/15.
 */
public class AnimUtil {
    public static void alphaHideView(final View view, long duration) {
        if(view != null) {
            view.animate().alpha(0).setDuration(duration).setListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    view.clearAnimation();
                    view.setVisibility(View.GONE);
                }
            }).start();
        }
    }

    public static void alphaShowView(final View view, long duration) {
        if(view != null) {
            view.setAlpha(0);
            view.setVisibility(View.VISIBLE);
            view.animate().alpha(1).setDuration(duration).setListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    view.clearAnimation();
                }
            }).start();
        }
    }

    public static void alphaShowView(View view, long duration, TimeInterpolator interpolator) {
        if(view != null) {
            view.setAlpha(0);
            view.animate().alpha(1).setDuration(duration).setInterpolator(interpolator).start();
        }
    }

    public static void translationView(View view, int fromX, int fromY, int toX, int toY, long duration) {
        if(view != null) {
            view.setTranslationX(fromX);
            view.setTranslationY(fromY);
            view.animate().translationX(toX).translationY(toY).setDuration(duration).start();
        }
    }

    public static class SimpleAnimatorListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {

        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }

    /**
     *
     * @param view
     */
    public static void alphaHideView(final View view) {
        if(view != null && view.getVisibility() == View.VISIBLE) {
            view.animate().alpha(0).setDuration(200)
                    .setInterpolator(Constants.gAccInterpolator)
                    .setListener(new SimpleAnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            super.onAnimationEnd(animator);
                            view.clearAnimation();
                            view.setVisibility(View.GONE);
                        }
                    }).start();
        }
    }

    public static void alphaShowView(final View view) {
        if(view != null && view.getVisibility() != View.VISIBLE) {
            view.setAlpha(0);
            view.setVisibility(View.VISIBLE);
            view.animate()
                    .setInterpolator(Constants.gAccInterpolator)
                    .alpha(1)
                    .setDuration(200).setListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    view.clearAnimation();
                }
            }).start();
        }
    }




}
