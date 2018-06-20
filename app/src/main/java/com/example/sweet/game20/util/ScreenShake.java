package com.example.sweet.game20.util;

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

    public ScreenShake(float a, float f, double d)
    {
        amplitude = a;
        frequency = f;
        duration = d;
        startTime = System.currentTimeMillis();

        points = new float[(int)((duration/1000) * frequency)];
        for(int i = 0; i < points.length; i++)
        {
            points[i] = (float)(Math.random() * 2 - 1)*amplitude;
        }
    }

    public float getShake()
    {
        double timeRunning = (System.currentTimeMillis()-startTime);
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
