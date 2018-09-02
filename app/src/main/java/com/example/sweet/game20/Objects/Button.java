package com.example.sweet.game20.Objects;

import com.example.sweet.game20.util.Constants;

/**
 * Created by Sweet on 6/20/2018.
 */

public class Button
{
    public ImageContainer
            regular,
            hoveredOver;

    public boolean cursorOnButton = false;

    public Drop drop = null;

    public Constants.DropType type;

    public boolean touchedOnDown = false;

    public Button(ImageContainer r, ImageContainer h, Constants.DropType dT)
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

    public void drawHighlight()
    {
        hoveredOver.draw();
    }

    public boolean pointOnButton(float x, float y)
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

    public void checkOnDown(float x, float y)
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

    public void checkOnMove(float x, float y)
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

    public void applyScale(float xS, float yS)
    {
        regular.applyScale(xS, yS);
        hoveredOver.applyScale(xS, yS);
    }
}
