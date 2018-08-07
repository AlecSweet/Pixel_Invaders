package com.example.sweet.game20.Objects;

/**
 * Created by Sweet on 7/16/2018.
 */

public class LocationHistory
{
    public float x, y;

    public long frame;

    public volatile boolean
            readyToBeConsumed = false;

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
        //uiConsumed = false;
        //collisionConsumed = false;
    }
    /*public void setNextLocation(LocationHistory nL)
    {
        nextLocation = nL;
        readyToBeConsumed = true;
    }*/
}

