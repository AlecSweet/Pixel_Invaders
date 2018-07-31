package com.example.sweet.game20.util;

import com.example.sweet.game20.GlobalInfo;

/**
 * Created by Sweet on 6/19/2018.
 */

public class ScreenShake
{
    private float amplitude;

    private int frequency;

    private double duration;

    private float[] points;

    private double startTime;

    public boolean live = true;

    private GlobalInfo globalInfo;

    public ScreenShake(float a, int f, double d, GlobalInfo gi)
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
            points[i] = (float)(Math.random() * flip);
            flip *= -1;
        }
    }

    public ScreenShake(GlobalInfo gi)
    {
        globalInfo = gi;
        live = false;
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
                return (points[lowerIndex] + (mid - lowerIndex)*(points[upperIndex] - points[lowerIndex])) * decay * amplitude;
            }
        }
    }

    public void resetScreenShake(float amp, int freq, double dur, float[] pat)
    {
        amplitude = amp;
        frequency = freq;
        duration = dur;
        points = pat;
        startTime = globalInfo.getAugmentedTimeMillis();
        live = true;
    }

    public float getRemainingPercentTime()
    {
        return (float)((duration - (globalInfo.getAugmentedTimeMillis()-startTime)) / duration);
    }
}
