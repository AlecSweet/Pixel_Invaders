package com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System;


/**
 * Created by Sweet on 1/29/2018.
 */

public class Pixel
{

    public float
            xDisp = 0f,
            yDisp = 0f;

    public int
            row,
            col,
            groupFlag = -1,
            health = 1;

    public volatile int state = 1;


    public Pixel( int r, int c)
    {
        row = r;
        col = c;
    }

    @Override
    public Pixel clone()
    {
        return new Pixel(row, col);
    }
}
