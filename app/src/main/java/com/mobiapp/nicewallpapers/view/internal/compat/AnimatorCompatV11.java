package com.mobiapp.nicewallpapers.view.internal.compat;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;

/**
 * Class to wrap a {@link ValueAnimator}
 * for use with AnimatorCompat
 *
 * @see
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class AnimatorCompatV11 extends AnimatorCompat {

    ValueAnimator animator;

    AnimatorCompatV11(float start, float end, final AnimationFrameUpdateListener listener) {
        super();
        animator = ValueAnimator.ofFloat(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                listener.onAnimationFrame((Float) animation.getAnimatedValue());
            }
        });
    }

    @Override
    public void cancel() {
        animator.cancel();
    }

    @Override
    public boolean isRunning() {
        return animator.isRunning();
    }

    @Override
    public void setDuration(int duration) {
        animator.setDuration(duration);
    }

    @Override
    public void start() {
        animator.start();
    }
}
