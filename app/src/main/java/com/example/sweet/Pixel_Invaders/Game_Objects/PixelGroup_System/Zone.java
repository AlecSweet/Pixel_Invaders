package com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System;

import java.util.ArrayList;

/**
 * Created by Sweet on 5/13/2018.
 */

public class Zone
{
    public CollidableGroup[] collidableGroups;

    public ArrayList<CollidableGroup> c;

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

    public  volatile boolean live = true;

    public Zone(float x, float y, float halfSquareLength, ArrayList<CollidableGroup> a)
    {
        c = a;
        this.xDisp = x;
        this.yDisp = y;
        xOriginal = x;
        yOriginal = y;
        live = true;
        this.halfSquareLength = halfSquareLength;
    }

    private Zone(float x, float y, float halfSquareLength, CollidableGroup[] c)
    {
        collidableGroups = c;
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

    public void initCollidableGroupArray()
    {
        collidableGroups = c.toArray(new CollidableGroup[c.size()]);
        c = null;
    }

    @Override
    public Zone clone()
    {
        Zone temp = new Zone(this.xOriginal, this.yOriginal, this.halfSquareLength, new CollidableGroup[collidableGroups.length]);
        for(int i = 0; i < this.collidableGroups.length; i++)
        {
            temp.collidableGroups[i] = this.collidableGroups[i].clone();
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
