package com.example.sweet.Pixel_Invaders.Game_Objects.Component_System;

import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Pixel;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;

/**
 * Created by Sweet on 3/25/2018.
 */


public class Bullet
{
    private float
            speed,
            maxDistance,
            distance;

    public boolean onScreen = false;

    float
            angle,
            cosA,
            sinA;

    boolean
            live = false,
            active = false;

    public PixelGroup pixelGroup;

    Bullet(float cx, float cy, float a, float spd, float mD, PixelGroup pG)
    {
        pixelGroup = pG;
        pixelGroup.setEnableOrphanChunkDeletion(false);
        pixelGroup.restorable = true;
        pixelGroup.orphanChunkCheckDelay = 10;
        pixelGroup.knockable = false;
        speed = spd;
        pixelGroup.speed = speed;
        angle = a;
        cosA = (float)Math.cos(angle);
        sinA = (float)Math.sin(angle);
        pixelGroup.move(cx,cy);
        pixelGroup.rotate(a);
        maxDistance = mD;
        distance = 0;
        pixelGroup.livablePercentage = .1f;
    }


    public void move(GlobalInfo gI)
    {
        float dist = speed * gI.timeSlow;
        checkOnScreen(gI);
        pixelGroup.move((dist * -cosA), (dist * sinA));
        distance += dist;
        if(maxDistance < distance)
        {
            live = false;
        }
    }

    public void move(GlobalInfo gI, float slow)
    {
        float dist = speed * slow;
        checkOnScreen(gI);
        pixelGroup.move((dist * -cosA), (dist * sinA));
        distance += dist;
        if(maxDistance < distance)
        {
            live = false;
        }
    }

    public void rotate(float a)
    {
        angle = a;
        cosA = (float)Math.cos(angle);
        sinA = (float)Math.sin(angle);
    }

    public void draw()
    {
        pixelGroup.draw();
    }

    public void checkOnScreen(GlobalInfo gI)
    {
        float difX = Math.abs(pixelGroup.centerX - gI.screenShiftX) * gI.getScaleX();
        float difY = Math.abs(pixelGroup.centerY - gI.screenShiftY) * gI.getScaleY();
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

    public void freeResources()
    {
        pixelGroup.freeMemory();
    }

    void resetBullet(float x, float y, float a)
    {
        if(pixelGroup.gotHit)
        {
            pixelGroup.resetPixels();
        }
        pixelGroup.resetLocationHistory(x, y);
        pixelGroup.setLoc(x,y);
        rotate(a);
        pixelGroup.rotate(a);
        distance = 0;
        pixelGroup.gotHit = false;
        live = true;
        active = true;
    }

    void setSpeed(float spd)
    {
        speed = spd;
        pixelGroup.speed = spd;
    }

    public boolean getLive()
    {
        return live;
    }

    public void setLive(boolean b)
    {
        live = b;
    }

    public Pixel[] getPixels()
    {
        return pixelGroup.getPixels();
    }
}
