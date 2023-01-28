package com.moutamid.mynotes.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.moutamid.mynotes.R;

// This is a custom editText with notes like lines
public class AppCompatLineEditText extends AppCompatEditText {

    private Rect mRect;
    private Paint mPaint;
    private boolean showLines = true;

    public AppCompatLineEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mRect = new Rect();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(getResources().getColor(R.color.gray));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int height = canvas.getHeight();
        int curHeight = 0;
        int baseline = getLineBounds(0, mRect);
        if (showLines) {
            for (curHeight = baseline + 3; curHeight < height; curHeight += getLineHeight()) {
                canvas.drawLine(mRect.left, curHeight, mRect.right, curHeight, mPaint);
            }
        }
        super.onDraw(canvas);
    }
}
