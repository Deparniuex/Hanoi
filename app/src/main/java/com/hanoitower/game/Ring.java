package com.hanoitower.game;

import androidx.annotation.ColorInt;

public class Ring {
    private final int size;
    @ColorInt
    private final int color;

    public Ring(int size, @ColorInt int color) {
        this.size = size;
        this.color = color;
    }

    public int getSize() {
        return size;
    }

    public int getColor() {
        return color;
    }
}
