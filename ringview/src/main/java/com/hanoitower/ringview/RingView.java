package com.hanoitower.ringview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RingView extends View {

    private final int[] STATE_AT_POINTER = {R.attr.state_pointer};

    private ColorStateList ringColor = ColorStateList.valueOf(Color.BLUE);
    private int ringLevel = 0, ringMaxLevel = 0; // proportional to diameter
    private boolean isAtPointer = false;

    final private Paint paint = new Paint(); { paint.setStyle(Paint.Style.FILL); }

    public RingView(Context context) {
        super(context);
    }

    public RingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray attrArray = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.RingView, 0, 0);
        try {
            ringColor = attrArray.getColorStateList(R.styleable.RingView_ringColor);
            ringLevel = attrArray.getInt(R.styleable.RingView_ringLevel, ringLevel);
            ringMaxLevel = attrArray.getInt(R.styleable.RingView_ringMaxLevel, ringMaxLevel);
            isAtPointer = attrArray.getBoolean(R.styleable.RingView_state_pointer, false);
        } finally {
            attrArray.recycle();
        }
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isAtPointer)
            mergeDrawableStates(drawableState, STATE_AT_POINTER);
        return drawableState;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) { // TODO should i clear canvas before drawing?
        float
                gap = getWidth() * (ringLevel + 1) / (ringMaxLevel + 1f) / 2,
                radius = getHeight() / 2f;
        paint.setColor(ringColor.getColorForState(getDrawableState(), ringColor.getDefaultColor()));
        canvas.drawRoundRect(
                gap, 0, getWidth() - gap, getHeight() - 1,
                radius, radius,
                paint
        );
    }

    public ColorStateList getRingColor() {
        return ringColor;
    }

    public void setRingColor(ColorStateList ringColor) {
        this.ringColor = ringColor;
    }

    public int getRingLevel() {
        return ringLevel;
    }

    public void setRingLevel(int ringLevel) {
        this.ringLevel = ringLevel;
    }

    public int getRingMaxLevel() {
        return ringMaxLevel;
    }

    public void setRingMaxLevel(int ringMaxLevel) {
        this.ringMaxLevel = ringMaxLevel;
    }

    public boolean isAtPointer() {
        return isAtPointer;
    }

    public void setAtPointer(boolean atPointer) {
        this.isAtPointer = atPointer;
    }
}
