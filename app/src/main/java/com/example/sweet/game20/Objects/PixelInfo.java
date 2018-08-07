package com.example.sweet.game20.Objects;

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

    public PixelInfo(float x, float y, float d, float r, float g, float b, float a)
    {
        xOriginal = x;
        yOriginal = y;
        depth = d;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
}
