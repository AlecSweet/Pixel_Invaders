package com.example.sweet.Pixel_Invaders.Game_Objects.Component_System;

import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Pixel;

/**
 * Created by Sweet on 4/15/2018.
 */

public abstract class Component
{
    protected Pixel[] attachmentPixels;

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

    Component(PixelGroup base, Constants.DropType dT, int[] attachIndices)
    {
        if(attachIndices != null)
        {
            attachmentPixels = new Pixel[attachIndices.length / 2];
            for (int i = 0; i < attachIndices.length; i += 2)
            {
                attachmentPixels[i / 2] = base.getpMap()[attachIndices[i + 1] + 1][attachIndices[i] + 1];
            }
            //attachmentPixels = p;
        }
        type = dT;
    }

    public void checkAlive()
    {
        live = false;
        if(attachmentPixels != null)
        {
            for (Pixel p : attachmentPixels)
            {
                if (p != null && p.state > 0)
                {
                    live = true;
                    break;
                }
            }
        }
    }

    public void setAttachmentPixels(Pixel[] aP)
    {
        attachmentPixels = aP;
    }

    public Pixel[] getAttachmentPixels()
    {
        return attachmentPixels;
    }

    public void draw()
    {
    }
}
