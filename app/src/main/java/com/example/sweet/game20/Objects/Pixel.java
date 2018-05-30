package com.example.sweet.game20.Objects;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by Sweet on 1/29/2018.
 */

public class Pixel {

    public volatile float
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

    public int groupFlag = -1;

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
        //xDisp = x;
        //yDisp = y;
        xOriginal = x;
        yOriginal = y;
    }

    public void killPixel()
    {
        live = false;
        for(int i = 0; i < neighbors.length; i++)
            if(neighbors[i] != null)
                neighbors[i].outside = true;
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
