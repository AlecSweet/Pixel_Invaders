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
            halfSquareLength;

    public  volatile boolean live = true;

    public Zone(float x, float y, float halfSquareLength)
    {
        c = new ArrayList<>();
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
    }

    public void setLoc(float mX, float mY)
    {
       /* x = mX;
        y = mY;*/
    }

    public void initCollidableGroupArray()
    {
        collidableGroups = c.toArray(new CollidableGroup[c.size()]);
    }

    @Override
    public Zone clone()
    {
        Zone z = new Zone(xOriginal, yOriginal, halfSquareLength);
        z.collidableGroups = new CollidableGroup[collidableGroups.length];
        for(int i = 0; i < collidableGroups.length; i++)
        {
            z.collidableGroups[i] = collidableGroups[i].clone();
        }
        return z;
    }
}
