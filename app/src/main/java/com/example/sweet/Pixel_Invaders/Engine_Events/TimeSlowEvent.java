package com.example.sweet.Pixel_Invaders.Engine_Events;

/**
 * Created by Sweet on 7/17/2018.
 */

public class TimeSlowEvent
{
    private float amplitude;

    private double duration;

    private double startTime;

    boolean active = true;

    TimeSlowEvent()
    {
        active = false;
    }

    void resetEvent(float a, double d)
    {
        amplitude = a;
        duration = d;
        startTime = System.currentTimeMillis();
        active = true;
    }

    public float getSlow()
    {
        double timeRunning = (System.currentTimeMillis()-startTime);
        if(timeRunning > duration)
        {
            active = false;
            return 0;
        }
        else
        {
            float t = Math.abs((float)(timeRunning / duration));
            return t * t * t * amplitude + 1 - amplitude;
        }
    }

    float getRemainingPercentTime()
    {
        return (float)((System.currentTimeMillis()-startTime) / duration);
    }
}
