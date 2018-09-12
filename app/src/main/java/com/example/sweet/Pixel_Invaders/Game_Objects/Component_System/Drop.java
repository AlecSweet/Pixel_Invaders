package com.example.sweet.Pixel_Invaders.Game_Objects.Component_System;

import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;

/**
 * Created by Sweet on 6/12/2018.
 */

public class Drop
{
    public PixelGroup pixelGroup;

    private double
            creationTime,
            liveTime;

    public float
            x,
            y,
            tiltAngle = 0,
            angle;

    public volatile boolean
            live = true,
            inPullRange = false,
            onScreen = false;

    public boolean
            held = false,
            consumable;

    /*
    0: Health Drop
    1: Component Drop
     */
    public Constants.DropType type;

    public Component component = null;

    public Drop(PixelGroup p, float x, float y, double lT, Constants.DropType dT)
    {
         pixelGroup = p;
         this.x = x;
         this.y = y;
         type = dT;
         liveTime = lT;
         creationTime = System.currentTimeMillis();
         consumable = true;
    }

    public Drop(PixelGroup p, float x, float y, double lT, Component c, Constants.DropType dT)
    {
        pixelGroup = p;
        this.x = x;
        this.y = y;
        type = dT;
        component = c;
        liveTime = lT;
        creationTime = System.currentTimeMillis();
        //pixelGroup.setWhiteToColor(component.r, component.g, component.b);
        consumable = false;
    }

    public void draw()
    {
        tiltAngle += .01;
        if(tiltAngle >= Math.PI)
        {
            tiltAngle -= Constants.twoPI;
        }
        pixelGroup.softDraw(x, y, 1, 0, tiltAngle);
    }

    public void menuDraw(float mx, float my, float mag, float pS)
    {
        tiltAngle += .01;
        if(tiltAngle >= Math.PI)
        {
            tiltAngle -= Constants.twoPI;
        }
        pixelGroup.softDraw(mx, my, 1, 0, tiltAngle, mag, pS);
    }

    public double checkAlive()
    {
        double timeLeft = System.currentTimeMillis() - creationTime;
        if(timeLeft > liveTime && !held)
        {
            live = false;
        }
        return timeLeft;
    }

    public void checkOnScreen(GlobalInfo gI)
    {
        float difX = Math.abs(x - gI.screenShiftX) * gI.getScaleX();
        float difY = Math.abs(y - gI.screenShiftY) * gI.getScaleY();
        if (difX <= (1 + pixelGroup.getHalfSquareLength()) &&
                difY <= (1 + pixelGroup.getHalfSquareLength()))
        {
            onScreen = true;
        }
        else
        {
            onScreen = false;
        }
    }

    public void move(float mX, float mY)
    {
        x += mX;
        y += mY;
    }

    public void freeMemory()
    {
        if (component.type == Constants.DropType.GUN && ((GunComponent)component).gun != null)
        {
            ((GunComponent)component).gun.freeMemory();
        }
    }
}
