package com.example.sweet.game20.util;

import com.example.sweet.game20.GlobalInfo;

/**
 * Created by Sweet on 6/19/2018.
 */

public class ScreenShake
{
    private float
            amplitude,
            frequency;

    private double duration;

    private float[] points;

    private double startTime;

    public boolean live = true;

    private GlobalInfo globalInfo;

    public ScreenShake(float a, float f, double d, GlobalInfo gi)
    {
        amplitude = a;
        frequency = f;
        duration = d;
        globalInfo = gi;
        startTime = gi.getAugmentedTimeMillis();

        points = new float[(int)((duration/1000) * frequency)];
        float flip;
        if(Math.random() < .5)
        {
            flip = -1;
        }
        else
        {
            flip = 1;
        }

        for(int i = 0; i < points.length; i++)
        {
            points[i] = (float)(Math.random() * flip)*amplitude;
            flip *= -1;
        }
    }

    public float getShake()
    {
        double timeRunning = (globalInfo.getAugmentedTimeMillis()-startTime);
        if(timeRunning > duration)
        {
            live = false;
            return 0;
        }
        else
        {
            float mid = (float)(timeRunning / 1000 * frequency);
            int lowerIndex = (int)Math.floor(mid);
            int upperIndex = lowerIndex + 1;
            float decay =  (float)((duration - timeRunning) / duration);
            if(upperIndex >= points.length)
            {
                return 0;
            }
            else
            {
                return (points[lowerIndex] + (mid - lowerIndex)*(points[upperIndex] - points[lowerIndex])) * decay;
            }
        }
    }
}
