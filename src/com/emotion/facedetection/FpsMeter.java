package com.emotion.facedetection;
import java.text.DecimalFormat;

import org.opencv.core.Core;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class FpsMeter {
    int                         step;
    int                         framesCouner;
    double                      freq;
    long                        prevFrameTime;
    String                      strfps;
    DecimalFormat               twoPlaces = new DecimalFormat("0.00");
    Paint                       paint;

    public void init() {
        step = 20;
        framesCouner = 0;
        freq = Core.getTickFrequency();
        prevFrameTime = Core.getTickCount();
        strfps = "";

        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setTextSize(60);
    }

    public void measure() {
    }

    public void draw(String string, Canvas canvas, float offsetx, float offsety) {
    	if(string.equals("SAD")){
        canvas.drawText("WHY "+string+". Please Don't be.", 20 + offsetx, 10 + 50 + offsety, paint);
    	}else{
    		canvas.drawText("Nice Smile :-) .", 20 + offsetx, 10 + 50 + offsety, paint);
    	}
    }
    
    public void drawString(Canvas canvas, float offsetx, float offsety){
    	//canvas.drawText("Why Sad!!!", 20 + offsetx, 10 + 50 + offsety, paint);
    }

	public void drawString(Canvas canvas, Bitmap bmp) {
		// TODO Auto-generated method stub
		float offsetx = (canvas.getWidth() - bmp.getWidth()) / 2;
		canvas.drawText("HAPPY ", 20 + offsetx, 10 + 50 + 0, paint);
	}

}