package com.example.sweet.Pixel_Invaders.Game_Objects.Component_System;

import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Pixel;

/**
 * Created by Sweet on 4/15/2018.
 */

public class Component
{
    private Pixel[] attachmentPixels;

    public boolean live = true;

    /*  0: Thruster
        1: Gun
        2: Mod
     */
    public Constants.DropType type;

    public float
            x,
            y,
            angle;

    public float
            r,
            g,
            b;

    Component(Pixel[] p, float x, float y, float a, Constants.DropType dT)
    {
        attachmentPixels = p;
        type = dT;
        this.x = x;
        this.y = y;
        angle = a;
    }

    public void checkAlive()
    {
        live = false;
        for(Pixel p: attachmentPixels)
        {
            if(p != null && p.state > 0)
            {
                live = true;
                break;
            }
        }
    }

    public void setAttachmentPixels(Pixel[] aP)
    {
        attachmentPixels = aP;
    }

    public void draw()
    {
    }
}
