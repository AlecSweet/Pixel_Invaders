package com.example.sweet.game20.Objects;


/**
 * Created by Sweet on 1/29/2018.
 */

public class Pixel
{

    public float
            xDisp = 0f,
            yDisp = 0f;

    public float
            xOriginal,
            yOriginal,
            depth = 0,
            r,
            g,
            b,
            a;

    public volatile int groupFlag = -1;

    public boolean live = true;

    public boolean outside = false;

    /*  Neighbor Indexes:
        0-Up
        1-Right
        2-Down
        3-Left
     */
    public Pixel[] neighbors = new Pixel[4];

    public Pixel(float x, float y)
    {
        xDisp = x;
        yDisp = y;
        xOriginal = x;
        yOriginal = y;
    }

    public void killPixel(float cosA, float sinA)
    {
        live = false;
        for(Pixel n: neighbors)
        {
            if (n != null)
            {
                //n.xDisp = n.xOriginal * cosA + n.yOriginal * sinA;
                //n.yDisp = n.yOriginal * cosA - n.xOriginal * sinA;
                n.outside = true;
            }
        }

    }

    public void killPixel()
    {
        live = false;
        for(Pixel n: neighbors)
        {
            if (n != null)
            {
                n.outside = true;
            }
        }
    }

    /*@Override
    public boolean equals(Object obj)
    {
        return false;
    }
    @Override
    public int hashCode()
    {
        return 0;
    }*/

    @Override
    public Pixel clone()
    {
        Pixel p = new Pixel(xOriginal,yOriginal);
        //p.depth = this.depth;
        p.r = this.r;
        p.g = this.g;
        p.b = this.b;
        p.a = this.a;
        return p;
    }
}
