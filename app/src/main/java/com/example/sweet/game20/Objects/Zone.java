package com.example.sweet.game20.Objects;

import java.util.ArrayList;

/**
 * Created by Sweet on 5/13/2018.
 */

public class Zone
{
    public CollidableGroup[] collidableGroups;
    public int tempItr = 0;

    public ArrayList<CollidableGroup> c;

    public volatile float
            xDisp,
            yDisp;

    public float
            xOriginal,
            yOriginal,
            halfSquareLength,
            centerMassX,
            centerMassY;

    public  volatile boolean live = true;

    /*public Zone(float x, float y, float halfSquareLength, int length)
    {
        collidableGroups = new CollidableGroup[length];
        this.xDisp = x;
        this.yDisp = y;
        xOriginal = x;
        yOriginal = y;
        live = true;
        this.halfSquareLength = halfSquareLength;
    }*/
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

    public Zone(float x, float y, float halfSquareLength, CollidableGroup[] c)
    {
        collidableGroups = c;
        this.xDisp = x;
        this.yDisp = y;
        xOriginal = x;
        yOriginal = y;
        live = true;
        this.halfSquareLength = halfSquareLength;
    }

    public Zone(float x, float y, float halfSquareLength)
    {
        //c = new ArrayList<>();
        this.xDisp = x;
        this.yDisp = y;
        xOriginal = x;
        yOriginal = y;
        live = true;
        this.halfSquareLength = halfSquareLength;
    }

    public void move(float mX, float mY)
    {
       /* x += mX;
        y += mY;*/
    }

    public void rotate(float c, float s)
    {
        xDisp = xOriginal*c + yOriginal*s;
        yDisp = yOriginal*c - xOriginal*s;

        /*float tempX = xOriginal + centerMassX;
        float tempY = yOriginal + centerMassY;
        xDisp =  tempX*c + tempY*s;
        yDisp = tempY*c -  tempX*s;*/
    }

    public void setLoc(float mX, float mY)
    {
       /* x = mX;
        y = mY;*/
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
}
