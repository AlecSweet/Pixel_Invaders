package com.example.sweet.game20.Objects;

import java.util.ArrayList;

/**
 * Created by Sweet on 4/6/2018.
 */

public class CollidableGroup
{
    public ArrayList<Pixel> p;

    public Pixel[] pixels;

    public float
            x,
            y;

    public float
            xOriginal,
            yOriginal,
            halfSquareLength;

    public boolean live = true;

    public CollidableGroup(float x, float y, float halfSquareLength)
    {
        p = new ArrayList<>();
        this.x = x;
        this.y = y;
        xOriginal = x;
        yOriginal = y;
        live = true;
        this.halfSquareLength = halfSquareLength;
    }

    public void move(float mX, float mY)
    {
        x += mX;
        y += mY;
    }

    public void rotate(float c, float s, float cX, float cY)
    {
        x = xOriginal*c + yOriginal*s + cX;
        y = yOriginal*c - xOriginal*s + cY;
    }

    public void setLoc(float mX, float mY)
    {
        x = mX;
        y = mY;
    }

    public void initPixelArray()
    {
        pixels = p.toArray(new Pixel[p.size()]);
    }

    @Override
    public CollidableGroup clone()
    {
        return new CollidableGroup(xOriginal, yOriginal, halfSquareLength);
    }
}
