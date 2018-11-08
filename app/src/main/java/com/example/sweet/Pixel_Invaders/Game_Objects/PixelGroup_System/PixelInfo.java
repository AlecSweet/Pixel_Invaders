package com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System;

/**
 * Created by Sweet on 8/2/2018.
 */

public class PixelInfo
{
    public float
            xOriginal,
            yOriginal,
            cosAOriginal,
            sinAOriginal,
            angleOriginal,
            depth = 0,
            r,
            g,
            b,
            a;

    public int
            index;

    public byte
            originalState = 1;

    public PixelInfo(float x, float y, float d, float r, float g, float b, float a, int i)
    {
        float angle = (float)(Math.atan2(x, y) + Math.PI/2 + Math.PI);
        angleOriginal = angle;
        cosAOriginal = (float)Math.cos(angle);
        sinAOriginal = (float)Math.sin(angle);
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
