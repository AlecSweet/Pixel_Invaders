package com.example.sweet.game20.Objects;

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
    }

    public void draw()
    {
        tiltAngle += .01;
        pixelGroup.softDraw(x, y, angle, tiltAngle);
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
}
