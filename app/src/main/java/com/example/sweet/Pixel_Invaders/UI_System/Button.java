package com.example.sweet.Pixel_Invaders.UI_System;

import com.example.sweet.Pixel_Invaders.Game_Objects.Component_System.Drop;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;

/**
 * Created by Sweet on 6/20/2018.
 */

public class Button
{
    ImageContainer
            regular,
            hoveredOver;

    public Drop drop = null;

    Constants.DropType type;

    boolean
            touchedOnDown = false,
            cursorOnButton = false;

    Button(ImageContainer r, ImageContainer h, Constants.DropType dT)
    {
        regular = r;
        hoveredOver = h;
        type = dT;
    }

    public void draw(float x, float y)
    {
        if(Math.abs(x - regular.x) <= regular.halfLength &&
                Math.abs(-y - regular.y) <= regular.halfWidth)
        {
            hoveredOver.draw();
            cursorOnButton = true;
        }
        else
        {
            regular.draw();
            cursorOnButton = false;
        }
    }

    public void draw(float x, float y, float x2, float y2)
    {
        if((Math.abs(x - regular.x) <= regular.halfLength &&
            Math.abs(-y - regular.y) <= regular.halfWidth) ||
            (Math.abs(x2 - regular.x) <= regular.halfLength &&
            Math.abs(-y2 - regular.y) <= regular.halfWidth))
        {
            hoveredOver.draw();
            cursorOnButton = true;
        }
        else
        {
            regular.draw();
            cursorOnButton = false;
        }
    }

    void drawHighlight()
    {
        hoveredOver.draw();
    }

    boolean pointOnButton(float x, float y)
    {
        if(Math.abs(x - regular.x) <= regular.halfLength &&
                Math.abs(-y - regular.y) <= regular.halfWidth)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void setLoc(float x, float y)
    {
        regular.x = x;
        regular.y = y;
        hoveredOver.x = x;
        hoveredOver.y = y;
    }

    public void setX(float x)
    {
        regular.x = x;
        hoveredOver.x = x;
    }

    public void setY(float y)
    {
        regular.y = y;
        hoveredOver.y = y;
    }

    void checkOnDown(float x, float y)
    {
        if(Math.abs(x - regular.x) <= regular.halfLength &&
                Math.abs(-y - regular.y) <= regular.halfWidth)
        {
            touchedOnDown = true;
        }
        else
        {
            touchedOnDown = false;
        }
    }

    void checkOnMove(float x, float y)
    {
        if(Math.abs(x - regular.x) <= regular.halfLength &&
                Math.abs(-y - regular.y) <= regular.halfWidth)
        {
            cursorOnButton = true;
        }
        else
        {
            cursorOnButton = false;
        }
    }

    void applyScale(float xS, float yS)
    {
        regular.applyScale(xS, yS);
        hoveredOver.applyScale(xS, yS);
    }

    public float getX()
    {
        return regular.getX();
    }

    public float getY()
    {
        return regular.getY();
    }
}
