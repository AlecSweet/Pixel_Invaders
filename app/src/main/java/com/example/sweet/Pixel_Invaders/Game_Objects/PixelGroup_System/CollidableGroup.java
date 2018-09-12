package com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System;

import java.util.ArrayList;

/**
 * Created by Sweet on 4/6/2018.
 */

public class CollidableGroup
{
    public ArrayList<Pixel> p;

    public Pixel[] pixels;

    public volatile float
            xDisp,
            yDisp;

     float
            centerMassX,
            centerMassY;

     private float
             xOriginal,
             yOriginal,
             halfSquareLength;

    public volatile boolean live = true;

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

    private CollidableGroup(float x, float y, float halfSquareLength, Pixel[] tP)
    {
        pixels = tP;
        this.xDisp = x;
        this.yDisp = y;
        xOriginal = x;
        yOriginal = y;
        live = true;
        this.halfSquareLength = halfSquareLength;
    }

    public void rotate(float c, float s)
    {
        xDisp = xOriginal*c + yOriginal*s;
        yDisp = yOriginal*c - xOriginal*s;
    }

    public void initPixelArray()
    {
        pixels = p.toArray(new Pixel[p.size()]);
        p = null;
    }

    @Override
    public CollidableGroup clone()
    {
        CollidableGroup temp = new CollidableGroup(this.xOriginal, this.yOriginal, this.halfSquareLength, new Pixel[pixels.length]);
        for(int i = 0; i < pixels.length; i++)
        {
            temp.pixels[i] = this.pixels[i].clone();
        }
        return temp;
    }

    public void setInfo(float xO, float yO, float hl)
    {
        xOriginal = xO;
        yOriginal = yO;
        halfSquareLength = hl;
    }

    public float getxOriginal()
    {
       return xOriginal;
    }

    public float getyOriginal()
    {
        return yOriginal;
    }

    public float getHalfSquareLength()
    {
        return halfSquareLength;
    }
}
