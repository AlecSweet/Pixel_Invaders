package com.example.sweet.game20.util;

/**
 * Created by Sweet on 3/16/2018.
 */

public class VectorFunctions
{
    public static float getMagnitude(float tX, float tY)
    {
        return (float)Math.sqrt((double)(tX * tX + tY * tY));
    }

    public static float getSquaredMagnitude(float x, float y, float x1, float y1)
    {
        return (x1 - x) * (x1 - x) + (y1 - y) * (y1 - y);
    }

    public static float getSquaredMagnitude(float tX, float tY)
    {
        return tX * tX + tY * tY;
    }
}
