package com.mobiapp.nicewallpapers.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import com.commit451.nativestackblur.NativeStackBlur;

public class BlurUtils {

    @SuppressLint({"NewApi"})
    public static Bitmap fastblur(Bitmap bitmap, int radius) {
        try {
            bitmap = NativeStackBlur.process(bitmap, radius);
        } catch (Exception ignored) {
        }
        return bitmap;
    }

    public static Bitmap getBlurBitmap(Bitmap bitmap, int dim, int radius, int grey) {
        if (bitmap == null) {
            return null;
        }
        try {
            Bitmap bitmapNew = bitmap.copy(Bitmap.Config.RGB_565, true);
            new Canvas(bitmapNew).drawColor(Color.argb(dim, 0, 0, 0));
            Bitmap blurred = Bitmap.createScaledBitmap(fastblur(Bitmap.createScaledBitmap(bitmapNew, (int) (((float) bitmap.getWidth()) * 0.4f), (int) (((float) bitmap.getHeight()) * 0.4f), false), radius), bitmap.getWidth(), bitmap.getHeight(), false);
            if (grey == 0) {
                return blurred;
            }
            float desaturateAmount = ((float) grey) / 500.0f;
            float greyNew = 0.0f;
            if (desaturateAmount != 0.0f) {
                if (((double) desaturateAmount) > 0.5d) {
                    greyNew = 0.5f;
                } else {
                    greyNew = 0.5f - desaturateAmount;
                }
            }
            return createGrey(blurred, greyNew);
        } catch (OutOfMemoryError e) {
            System.gc();
            return bitmap;
        }
    }

    private static Bitmap createGrey(Bitmap src, float grey) {
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmOut);
        ColorMatrix ma = new ColorMatrix();
        ma.setSaturation(grey);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColorFilter(new ColorMatrixColorFilter(ma));
        canvas.drawBitmap(src, 0.0f, 0.0f, paint);
        return bmOut;
    }
}
