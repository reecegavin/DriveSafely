/*
This file is an adapted version of the googlesamples/android-vision file found here:
https://tinyurl.com/22kpsafw
 */

/**
 * Author : Reece Gavin
 * ID Number : 17197589
 * Date last modified: 17/02/2021
 */

package com.example.drivesafely;

//Import necessary dependencies

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import java.util.List;

//Class used to draw the bounding box around the users face and landmark positions(for testing only)
class FaceGraphic extends GraphicOverlay.Graphic {

    //Declare private variables
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;
    private static final int[] COLOR_CHOICES = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.RED,
            Color.WHITE,
            Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;
    private final Paint mFacePositionPaint;
    private final Paint mBoxPaint;
    private volatile Face mFace;

    FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        //Define parameters for colour and style etc. of bounding box
        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);
        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }


    void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;

        if (face == null) {
            return;
        }


        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);


        // Draws a bounding box around the face.
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        canvas.drawRect(left, top, right, bottom, mBoxPaint);


        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);


        if ((contains(face.getLandmarks(), 0) != 99)
                && (contains(face.getLandmarks(), 1) != 99)
                && (contains(face.getLandmarks(), 2) != 99)
                && (contains(face.getLandmarks(), 3) != 99)
                && (contains(face.getLandmarks(), 4) != 99)
                && (contains(face.getLandmarks(), 5) != 99)
                && (contains(face.getLandmarks(), 6) != 99)
                && (contains(face.getLandmarks(), 7) != 99)
                && (contains(face.getLandmarks(), 8) != 99)
                && (contains(face.getLandmarks(), 9) != 99)
                && (contains(face.getLandmarks(), 10) != 99)
                && (contains(face.getLandmarks(), 11) != 99)) {

//            Get position of each landmark and draw a circle at the detected x,y coordinate
//            int cBottomMouthX;
//            int cBottomMouthY;
//
//            cBottomMouthX = (int) translateX(face.getLandmarks().get(contains(face.getLandmarks(), 0)).getPosition().x);
//            cBottomMouthY = (int) translateY(face.getLandmarks().get(contains(face.getLandmarks(), 0)).getPosition().y);
//            canvas.drawCircle(cBottomMouthX, cBottomMouthY, 5, paint);
//
//
//            int cLeftCheekX;
//            int cLeftCheekY;
//
//            cLeftCheekX = (int) translateX(face.getLandmarks().get(contains(face.getLandmarks(), 1)).getPosition().x);
//            cLeftCheekY = (int) translateY(face.getLandmarks().get(contains(face.getLandmarks(), 1)).getPosition().y);
//            canvas.drawCircle(cLeftCheekX, cLeftCheekY, 5, paint);
//
//
//            int cLeftEarX;
//            int cLeftEarY;
//
//            cLeftEarX = (int) translateX(face.getLandmarks().get(contains(face.getLandmarks(), 2)).getPosition().x);
//            cLeftEarY = (int) translateY(face.getLandmarks().get(contains(face.getLandmarks(), 2)).getPosition().y);
//            canvas.drawCircle(cLeftEarX, cLeftEarY, 5, paint);
//
//            int cLeftEarTipX;
//            int cLeftEarTipY;
//
//            cLeftEarTipX = (int) translateX(face.getLandmarks().get(contains(face.getLandmarks(), 3)).getPosition().x);
//            cLeftEarTipY = (int) translateY(face.getLandmarks().get(contains(face.getLandmarks(), 3)).getPosition().y);
//            canvas.drawCircle(cLeftEarTipX, cLeftEarTipY, 5, paint);
//
//            int cLeftEyeX;
//            int cLeftEyeY;
//
//            cLeftEyeX = (int) translateX(face.getLandmarks().get(contains(face.getLandmarks(), 4)).getPosition().x);
//            cLeftEyeY = (int) translateY(face.getLandmarks().get(contains(face.getLandmarks(), 4)).getPosition().y);
//
//            canvas.drawCircle(cLeftEyeX, cLeftEyeY, 5, paint);
//
//            int cLeftMouthX;
//            int cLeftMouthY;
//
//            cLeftMouthX = (int) translateX(face.getLandmarks().get(contains(face.getLandmarks(), 5)).getPosition().x);
//            cLeftMouthY = (int) translateY(face.getLandmarks().get(contains(face.getLandmarks(), 5)).getPosition().y);
//            canvas.drawCircle(cLeftMouthX, cLeftMouthY, 5, paint);
//
//            int cNoseBaseX;
//            int cNoseBaseY;
//
//            cNoseBaseX = (int) translateX(face.getLandmarks().get(contains(face.getLandmarks(), 6)).getPosition().x);
//            cNoseBaseY = (int) translateY(face.getLandmarks().get(contains(face.getLandmarks(), 6)).getPosition().y);
//            canvas.drawCircle(cNoseBaseX, cNoseBaseY, 5, paint);
//
//            int cRightCheekX;
//            int cRightCheekY;
//
//            cRightCheekX = (int) translateX(face.getLandmarks().get(contains(face.getLandmarks(), 7)).getPosition().x);
//            cRightCheekY = (int) translateY(face.getLandmarks().get(contains(face.getLandmarks(), 7)).getPosition().y);
//            canvas.drawCircle(cRightCheekX, cRightCheekY, 5, paint);
//
//
//            int cRightEarTipX;
//            int cRightEatTipY;
//
//            cRightEarTipX = (int) translateX(face.getLandmarks().get(contains(face.getLandmarks(), 8)).getPosition().x);
//            cRightEatTipY = (int) translateY(face.getLandmarks().get(contains(face.getLandmarks(), 8)).getPosition().y);
//            canvas.drawCircle(cRightEarTipX, cRightEatTipY, 5, paint);
//
//            int cRightEarX;
//            int cRightEarY;
//
//            cRightEarX = (int) translateX(face.getLandmarks().get(contains(face.getLandmarks(), 9)).getPosition().x);
//            cRightEarY = (int) translateY(face.getLandmarks().get(contains(face.getLandmarks(), 9)).getPosition().y);
//            canvas.drawCircle(cRightEarX, cRightEarY, 5, paint);
//
//            int cRighteyeX;
//            int cRighteyeY;
//
//
//            cRighteyeX = (int) translateX(face.getLandmarks().get(contains(face.getLandmarks(), 10)).getPosition().x);
//            cRighteyeY = (int) translateY(face.getLandmarks().get(contains(face.getLandmarks(), 10)).getPosition().y);
//
//            canvas.drawCircle(cRighteyeX, cRighteyeY, 5, paint);
//
//            int cRightMouthX;
//            int cRightMouthY;
//
//            cRightMouthX = (int) translateX(face.getLandmarks().get(contains(face.getLandmarks(), 11)).getPosition().x);
//            cRightMouthY = (int) translateY(face.getLandmarks().get(contains(face.getLandmarks(), 11)).getPosition().y);
//
//            canvas.drawCircle(cRightMouthX, cRightMouthY, 5, paint);

        }


    }

    //Method used to aid in drawing the facial landmarks
    private int contains(List<Landmark> list, int name) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getType() == name) {
                return i;
            }
        }
        return 99;
    }


}










