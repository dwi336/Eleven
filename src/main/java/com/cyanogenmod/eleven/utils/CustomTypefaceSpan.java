package com.cyanogenmod.eleven.utils;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class CustomTypefaceSpan extends MetricAffectingSpan{

    private final Typeface newType;

    public CustomTypefaceSpan(Typeface type) {
        super();
        newType = type;
    }

    @Override
    public void updateDrawState(TextPaint textPaint){
        apply(textPaint, newType);
    }

    @Override
    public void updateMeasureState(TextPaint textPaint){
        apply(textPaint, newType);
    }

    private static void apply(Paint paint, Typeface typeface){
        int oldStyle;

        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int fake = oldStyle & ~typeface.getStyle();

        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(typeface);
    }
}