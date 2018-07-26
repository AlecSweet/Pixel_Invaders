package com.example.sweet.game20;

/**
 * Created by Sweet on 7/13/2018.
 */

public class GlobalInfo
{
    public volatile float timeSlow = 1;

    private volatile long totalAugmentedTime;

    private volatile long setTime;

    private volatile float scaleX, scaleY;


    public GlobalInfo(long globalStart)
    {
        totalAugmentedTime = System.currentTimeMillis() - globalStart;
        setTime = System.currentTimeMillis();
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
}
