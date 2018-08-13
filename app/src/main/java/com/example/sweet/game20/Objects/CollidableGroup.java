package com.example.sweet.game20.Objects;

import java.util.ArrayList;

/**
 * Created by Sweet on 4/6/2018.
 */

public class CollidableGroup
{
    public ArrayList<Pixel> p;

    public Pixel[] pixels;

    public int tempItr = 0;

    public volatile float
            xDisp,
            yDisp;

    public float
            xOriginal,
            yOriginal,
            halfSquareLength,
            centerMassX,
            centerMassY;

    public volatile boolean live = true;

    /*public CollidableGroup(float x, float y, float halfSquareLength, int length)
    {
        pixels = new Pixel[length];
        this.xDisp = x;
        this.yDisp = y;
        xOriginal = x;
        yOriginal = y;
        live = true;
        this.halfSquareLength = halfSquareLength;
    }*/

    public CollidableGroup(float x, float y, float halfSquareLength, ArrayList<Pixel> a)
    {
        p = a;
        this.xDisp = x;
        this.yDisp = y;
        xOriginal = x;
        yOriginal = y;
        live = true;
        this.halfSquareLength = halfSquareLength;
    }

    public CollidableGroup(float x, float y, float halfSquareLength)
    {
        //p = new ArrayList<>();
        this.xDisp = x;
        this.yDisp = y;
        xOriginal = x;
        yOriginal = y;
        live = true;
        this.halfSquareLength = halfSquareLength;
    }

    public void move(float mX, float mY)
    {
        /*x += mX;
        y += mY;*/
    }

    public void rotate(float c, float s)
    {
        xDisp = xOriginal*c + yOriginal*s;
        yDisp = yOriginal*c - xOriginal*s;
        /*float tempX = xOriginal + centerMassX;
        float tempY = yOriginal + centerMassY;
        xDisp = tempX*c + tempY*s;
        yDisp = tempY*c -  tempX*s;*/
    }

    public void setLoc(float mX, float mY)
    {
       /* x = mX;
        y = mY;*/
    }

    public void initPixelArray()
    {
        pixels = p.toArray(new Pixel[p.size()]);
        p = null;
    }

    @Override
    public CollidableGroup clone()
    {
        return new CollidableGroup(xOriginal, yOriginal, halfSquareLength);
    }
}
