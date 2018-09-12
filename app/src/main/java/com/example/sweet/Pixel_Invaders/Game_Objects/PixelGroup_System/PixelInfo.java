package com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System;

/**
 * Created by Sweet on 8/2/2018.
 */

public class PixelInfo
{
    public float
            xOriginal,
            yOriginal,
            depth = 0,
            r,
            g,
            b,
            a;

    public int
            index,
            originalState = 1;

    public PixelInfo(float x, float y, float d, float r, float g, float b, float a, int i)
    {
        xOriginal = x;
        yOriginal = y;
        depth = d;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        index = i;
    }
}
