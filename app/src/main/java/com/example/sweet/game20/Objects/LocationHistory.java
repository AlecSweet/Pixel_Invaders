package com.example.sweet.game20.Objects;

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
        //uiConsumed = false;
        //collisionConsumed = false;
    }

    public void setPrevLocation(float x, float y)
    {
        prevX = x;
        prevY = y;
        //uiConsumed = false;
        //collisionConsumed = false;
    }
    /*public void setNextLocation(LocationHistory nL)
    {
        nextLocation = nL;
        readyToBeConsumed = true;
    }*/
}

