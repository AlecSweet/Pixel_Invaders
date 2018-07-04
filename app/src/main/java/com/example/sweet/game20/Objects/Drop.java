package com.example.sweet.game20.Objects;

import com.example.sweet.game20.util.Constants;

/**
 * Created by Sweet on 6/12/2018.
 */

public class Drop
{
    public PixelGroup pixelGroup;

    public double
            creationTime,
            liveTime;

    public float
            x,
            y,
            tiltAngle = 0,
            angle;

    public volatile boolean live = true;

    public boolean held = false;

    /*
    0: Health Drop
    1: Component Drop
     */
    public int type;

    public Component component = null;

    public Drop(PixelGroup p, float x, float y, int t, double lT)
    {
         pixelGroup = p;
         this.x = x;
         this.y = y;
         type = t;
         liveTime = lT;
         creationTime = System.currentTimeMillis();
    }

    public Drop(PixelGroup p, float x, float y, int t, double lT, Component c)
    {
        pixelGroup = p;
        this.x = x;
        this.y = y;
        type = t;
        component = c;
        liveTime = lT;
        creationTime = System.currentTimeMillis();
        pixelGroup.setWhiteToColor(component.r, component.g, component.b);
    }

    public void draw()
    {
        tiltAngle += .01;
        pixelGroup.softDraw(x, y, angle, tiltAngle);
    }

    public void menuDraw(float mx, float my, float mag, float pS)
    {
        tiltAngle += .01;
        pixelGroup.softDraw(mx, my, angle, tiltAngle, mag, pS);
    }

    public void checkAlive()
    {
        if(System.currentTimeMillis() - creationTime > liveTime)
        {
            live = false;
        }
    }

    public void move(float mX, float mY)
    {
        x += mX;
        y += mY;
    }

    public void freeMemory()
    {
        if (component.type == Constants.DropType.GUN && component.gun != null)
        {
            component.gun.freeMemory();
        }
    }
}
