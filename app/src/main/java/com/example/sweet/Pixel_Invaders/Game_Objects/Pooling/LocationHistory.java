package com.example.sweet.Pixel_Invaders.Game_Objects.Pooling;

/**
 * Created by Sweet on 7/16/2018.
 */

public class LocationHistory
{
    public float
            x,
            y,
            prevX,
            prevY;

    public long frame;

    public volatile boolean
            readyToBeConsumed = false,
            collisionDone = false;

    public LocationHistory nextLocation;

    public LocationHistory(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public void setLocation(float x, float y)
    {
        this.x = x;
        this.y = y;
        readyToBeConsumed = false;
        collisionDone = false;
    }

    public void setPrevLocation(float x, float y)
    {
        prevX = x;
        prevY = y;
    }
}

