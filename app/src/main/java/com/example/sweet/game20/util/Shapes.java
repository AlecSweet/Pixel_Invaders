package com.example.sweet.game20.util;

/**
 * Created by Sweet on 2/15/2018.
 */

public class Shapes
{
    public static float[] makeCircle(int points, float radius, float centerX, float centerY)
    {
        float[] circle = new float[points*2+4];
        circle[0] = centerX;
        circle[1] = centerY;
        for(int i = 2; i < circle.length-2; i+=2)
        {
            double angle = ((double)i / ((double)circle.length-4)) * 2 * Math.PI;
            circle[i] = (float)(centerX + radius * Math.cos(angle));
            circle[i+1] = (float)(centerY + radius * Math.sin(angle));
        }
        circle[circle.length-2] = circle[2];
        circle[circle.length-1] = circle[3];
        return circle;
    }

    public static float[] makeCircle2(int points, float radius, float centerX, float centerY)
    {
        float[] circle = new float[points*2+4];
        circle[0] = centerX;
        circle[1] = centerY;
        for(int i = 2; i < circle.length-2; i+=2)
        {
            double angle = ((double)i / ((double)circle.length-4)) * 2 * Math.PI;
            circle[i] = (float)(centerX + radius * Math.cos(angle));
            circle[i+1] = (float)(centerY + radius * Math.sin(angle));
        }
        circle[circle.length-2] = circle[2];
        circle[circle.length-1] = circle[3];
        return circle;
    }

}
