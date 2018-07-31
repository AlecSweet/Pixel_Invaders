package com.example.sweet.game20.Objects;

/**
 * Created by Sweet on 7/17/2018.
 */

public class TimeSlowEvent
{
    private float amplitude;

    private double duration;

    private double startTime;

    public boolean active = true;

    public TimeSlowEvent(float a, double d)
    {
        amplitude = a;
        duration = d;
        startTime = System.currentTimeMillis();
    }

    public TimeSlowEvent()
    {
        active = false;
    }

    public void resetEvent(float a, double d)
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

    public float getRemainingPercentTime()
    {
        return (float)((System.currentTimeMillis()-startTime) / duration);
    }
}
