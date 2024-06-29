package com.healthfuel.myhealthfuel.mainpage;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class RepsCountDisplay extends View {
        Rect srcRect = new Rect(0,0,480, 640);
        Rect disRect;

        Bitmap b;
        double angle = 0;
        int reps = 0;
        int sets = 0;
        String stage = "up"; // Starea (up/down) pentru miscarea de curl

    public RepsCountDisplay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public void setAngle(double angle) {
            this.angle = angle;
            invalidate();
        }

        public void setStage(String stage) {
            this.stage = stage;
            invalidate();
        }
    public void getBitmap(Bitmap bitmap) {
        this.b = bitmap;
    }


    public void setReps(int reps) {
            this.reps = reps;
            invalidate();
        }

    public void setSets(int sets) {
        this.sets = sets;
        invalidate();
    }


    @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            disRect = new Rect(0,0,getRight(),getBottom());
            if (b != null) {
                canvas.drawBitmap(b, srcRect,disRect,null);
            }

            // Afiseaza unghiul pe ecran
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(40);
            canvas.drawText("Angle: " + angle, 20, 60, paint);

            // Afiseaza starea (up/down) și counterul pe ecran
            canvas.drawText("Stage: " + stage, 20, 120, paint);
            canvas.drawText("Reps: " + reps, 20, 180, paint);
            canvas.drawText("Sets: " + sets, 20, 240, paint);
        }
    }
