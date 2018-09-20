package com.example.sweet.Pixel_Invaders.Engine_Events;

/**
 * Created by Sweet on 7/17/2018.
 */

public class TimeSlowEvent
{
    private float amplitude;

    private float duration;

    private long startTime;

    private SlowPatternFunction slowFunc;

    boolean active = true;

    TimeSlowEvent()
    {
        active = false;
    }

    void resetEvent(float a, float d, SlowPatternFunction sF)
    {
        slowFunc = sF;
        amplitude = a;
        duration = d;
        startTime = System.currentTimeMillis();
        active = true;
    }

    public float getSlow()
    {
        float timeRunning = (System.currentTimeMillis() - startTime);
        if(timeRunning > duration)
        {
            active = false;
            return 0;
        }
        else
        {
            return slowFunc.getSlow(amplitude, duration, timeRunning);
        }
    }

    float getRemainingPercentTime()
    {
        return (System.currentTimeMillis()-startTime) / duration;
    }
}
