package com.example.sweet.game20.Objects;

/**
 * Created by Sweet on 7/17/2018.
 */

public class TimeSlowEvent
{
    private float amplitude;

    private double duration;

    private double startTime;

    public boolean live = true;

    public TimeSlowEvent(float a, double d)
    {
        amplitude = a;
        duration = d;
        startTime = System.currentTimeMillis();
    }

    public float getSlow()
    {
        double timeRunning = (System.currentTimeMillis()-startTime);
        if(timeRunning > duration)
        {
            live = false;
            return 0;
        }
        else
        {
            float t = Math.abs((float)(timeRunning / duration));
            return t * t * t * amplitude + 1 - amplitude;
        }
    }
}
