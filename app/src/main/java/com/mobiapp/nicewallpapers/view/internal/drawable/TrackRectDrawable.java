package com.mobiapp.nicewallpapers.view.internal.drawable;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.mobiapp.nicewallpapers.view.internal.drawable.StateDrawable;

/**
 * Simple {@link org.adw.library.widgets.discreteseekbar.internal.drawable.StateDrawable} implementation
 * to draw rectangles
 *
 * @hide
 */
public class TrackRectDrawable extends StateDrawable {
    public TrackRectDrawable(@NonNull ColorStateList tintStateList) {
        super(tintStateList);
    }

    @Override
    void doDraw(Canvas canvas, Paint paint) {
        canvas.drawRect(getBounds(), paint);
    }

}
