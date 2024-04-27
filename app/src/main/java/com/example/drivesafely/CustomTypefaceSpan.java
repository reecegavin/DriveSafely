/**
 * Author : Reece Gavin
 * ID Number : 17197589
 * Date last modified: 17/02/2021
 */

package com.example.drivesafely;

//Import necessary dependencies
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

//Class used to allow two different fonts to be used inside a TextView object
public class CustomTypefaceSpan extends TypefaceSpan {

    //Define private variables
    private final Typeface newType;

    //Create new CustomTypefaceSpan
    public CustomTypefaceSpan(String family, Typeface type) {
        super(family);
        newType = type;
    }

    //Method used by TypefaceSpan
    @Override
    public void updateDrawState(TextPaint ds) {
        applyCustomTypeFace(ds, newType);
    }

    //Method used by TypefaceSpan
    @Override
    public void updateMeasureState(TextPaint paint) {
        applyCustomTypeFace(paint, newType);
    }

    //Method used to apply the custom type face
    private static void applyCustomTypeFace(Paint paint, Typeface tf) {
        int oldStyle;
        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int fake = oldStyle & ~tf.getStyle();
        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }
        paint.setTypeface(tf);
    }
}