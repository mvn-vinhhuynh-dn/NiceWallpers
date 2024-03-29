package com.mobiapp.nicewallpapers.view.internal.compat;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewParent;
import android.widget.TextView;

import com.mobiapp.nicewallpapers.view.internal.drawable.MarkerDrawable;

/**
 * Wrapper compatibility class to call some API-Specific methods
 * And offer alternate procedures when possible
 */
@TargetApi(21)
class SeekBarCompatDontCrash {
    static void setOutlineProvider(View marker, final MarkerDrawable markerDrawable) {
        marker.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setConvexPath(markerDrawable.getPath());
            }
        });
    }

    static Drawable getRipple(ColorStateList colorStateList) {
        return new RippleDrawable(colorStateList, null, null);
    }

    static void setBackground(View view, Drawable background) {
        view.setBackground(background);
    }

    static void setTextDirection(TextView number, int textDirection) {
        number.setTextDirection(textDirection);
    }

    static boolean isInScrollingContainer(ViewParent p) {
        while (p != null && p instanceof ViewGroup) {
            if (((ViewGroup) p).shouldDelayChildPressedState()) {
                return true;
            }
            p = p.getParent();
        }
        return false;
    }

    static boolean isHardwareAccelerated(View view) {
        return view.isHardwareAccelerated();
    }
}
