package com.example.sweet.game20.Objects;

/**
 * Created by Sweet on 7/16/2018.
 */

public class LocationHistory
{
    public float x, y;

    public boolean readyToBeConsumed;

    public LocationHistory nextLocation;

    public int chainID;

    public LocationHistory(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    /*public void setNextLocation(LocationHistory nL)
    {
        nextLocation = nL;
        readyToBeConsumed = true;
    }*/
}

