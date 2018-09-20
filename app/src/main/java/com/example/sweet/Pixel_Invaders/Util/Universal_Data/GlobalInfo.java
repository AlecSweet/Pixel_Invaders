package com.example.sweet.Pixel_Invaders.Util.Universal_Data;

import com.example.sweet.Pixel_Invaders.Game_Objects.Pooling.LocationHistory;
import com.example.sweet.Pixel_Invaders.Game_Objects.Rift;

/**
 * Created by Sweet on 7/13/2018.
 */

public class GlobalInfo
{
    public volatile float timeSlow = 1;

    private volatile long totalAugmentedTime;

    private volatile long setTime;

    private volatile float scaleX, scaleY;

    public volatile float screenShiftX, screenShiftY;

    public GameSettings gameSettings;

    public float
            extraGunChance = .02f,
            extraModChance = .05f;

    private LocationHistory screenShiftHead, screenShiftDrawTail;

    public float pointSize, particlePointSize;

    private volatile boolean pause = false;

    public GlobalInfo(long globalStart, GameSettings gameSettings)
    {
        totalAugmentedTime = System.currentTimeMillis() - globalStart;
        setTime = System.currentTimeMillis();
        this.gameSettings = gameSettings;

        screenShiftDrawTail = new LocationHistory(0, 0);
        screenShiftHead = screenShiftDrawTail;
        for(int i = 0; i < 6; i++)
        {
            screenShiftHead.nextLocation = new LocationHistory(0,0);
            screenShiftHead = screenShiftHead.nextLocation;
        }

        screenShiftHead.nextLocation = screenShiftDrawTail;
        screenShiftHead = screenShiftDrawTail;
    }

    public float getTimeSlow()
    {
        return timeSlow;
    }

    public float getAugmentedTimeSeconds()
    {
        return ((System.currentTimeMillis() - setTime) * timeSlow + totalAugmentedTime) / 1000;
    }

    public float getAugmentedTimeMillis()
    {
        return ((System.currentTimeMillis() - setTime) * timeSlow + totalAugmentedTime);
    }

    public void setTimeSlow(float tS)
    {
        totalAugmentedTime += (long)((System.currentTimeMillis() - setTime) * timeSlow);
        setTime = System.currentTimeMillis();
        timeSlow = tS;
    }

    public void pauseTime()
    {
        pause = true;
        /*totalAugmentedTime += (long)((System.currentTimeMillis() - setTime) * timeSlow);
        setTime = System.currentTimeMillis();
        previousTimeSlow = timeSlow;
        timeSlow = 0;*/
    }

    public void unpauseTime()
    {
        /*setTime = System.currentTimeMillis();
        timeSlow = previousTimeSlow;*/
        pause = false;
    }


    public float getScaleX()
    {
        return scaleX;
    }

    public float getScaleY()
    {
        return scaleY;
    }

    public void setScale(float sX, float sY)
    {
        scaleX = sX;
        scaleY = sY;
    }

   /* public void publishScreenShift(float sX, float sY)
    {
        screenShiftHead.nextLocation.setLocation(sX,sY);
        LocationHistory prev = screenShiftHead;
        screenShiftHead = screenShiftHead.nextLocation;
        prev.readyToBeConsumed = true;
    }

    public void consumeScreenShift()
    {
        while(screenShiftDrawTail.nextLocation != null && screenShiftDrawTail.nextLocation.collisionDone)
        {
            screenShiftDrawTail = screenShiftDrawTail.nextLocation;
        }
    }*/

    public float getScreenShiftX()
    {
        //return screenShiftDrawTail.x;
        return screenShiftX;
    }

    public float getScreenShiftY()
    {
        //return screenShiftDrawTail.y;
        return screenShiftY;
    }

    public boolean getPause()
    {
        return pause;
    }
}
