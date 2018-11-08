package com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System;


/**
 * Created by Sweet on 1/29/2018.
 */

public class Pixel
{
    public short
            row,
            col,
            groupFlag = -1;

    public byte
            health = 1;

    public volatile byte state = 1;

    public HitboxNode parent;

    public Pixel(short r, short c)
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
