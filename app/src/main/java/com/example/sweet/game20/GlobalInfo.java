package com.example.sweet.game20;

import com.example.sweet.game20.Objects.GameSettings;

/**
 * Created by Sweet on 7/13/2018.
 */

public class GlobalInfo
{
    public volatile float timeSlow = 1;

    private float previousTimeSlow;

    private volatile long totalAugmentedTime;

    private volatile long setTime;

    private volatile float scaleX, scaleY;

    public GameSettings gameSettings;

    private volatile boolean pause = false;

    public GlobalInfo(long globalStart, GameSettings gameSettings)
    {
        totalAugmentedTime = System.currentTimeMillis() - globalStart;
        setTime = System.currentTimeMillis();
        this.gameSettings = gameSettings;
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

    public boolean getPause()
    {
        return pause;
    }
}
