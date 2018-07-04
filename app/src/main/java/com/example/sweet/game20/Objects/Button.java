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

    public void applyScale(float xS, float yS)
    {
        regular.applyScale(xS, yS);
        hoveredOver.applyScale(xS, yS);
    }
}
