package com.example.sweet.game20.Objects;

/**
 * Created by Sweet on 3/26/2018.
 */

public class Collidable
{
    public volatile float
            centerX,
            centerY;

    public float
            cosA = 0,
            sinA = 0,
            speed;

    public float
            halfSquareLength,
            livablePercentage = .5f,
            knockBackFactor = .006f;

    public double
            orphanChunkCheckDelay = 100,
            lastOrphanChunkCheck;

    public volatile double angle = 0;

    protected boolean
            enableOrphanChunkDeletion,
            enableLocationChain = false;

    protected LocationHistory
            locationHead,
            locationTail;

    protected boolean
            collidableLive,
            needsUpdate;

    protected Pixel[] pixels;

    public Zone[] zones;

    public int
            totalPixels,
            numLivePixels;

    public Pixel lastPixelKilled;

    public Collidable(float x, float y, float hSL, Pixel[] p, boolean chunkDeletion, Zone[] z)
    {
        centerX = x;
        centerY = y;
        locationTail = new LocationHistory(centerX, centerY);
        locationHead = locationTail;
        halfSquareLength = hSL;
        pixels = p;
        totalPixels = pixels.length;
        numLivePixels = totalPixels;
        enableOrphanChunkDeletion = chunkDeletion;
        zones = z;
        collidableLive = true;
        needsUpdate = false;
        lastOrphanChunkCheck = System.currentTimeMillis();
    }

    public void move(float mX, float mY)
    {
        centerX += mX;
        centerY += mY;
        if(enableLocationChain)
        {
            locationHead.nextLocation = new LocationHistory(centerX, centerY);
            locationHead.nextLocation.chainID = locationHead.chainID++;
            LocationHistory prev = locationHead;
            locationHead = locationHead.nextLocation;
            prev.readyToBeConsumed = true;
        }

        /*for(Pixel p: pixels)
        {
            p.xDisp += mX;
            p.yDisp += mY;
        }
        for(int z = 0; z < zones.length; z++)
        {
            if (zones[z] != null)
            {
                zones[z].move(mX, mY);
                for (int cG = 0; cG < zones[z].collidableGroups.length; cG++)
                    if (zones[z].collidableGroups[cG] != null)
                        zones[z].collidableGroups[cG].move(mX, mY);
            }
        }*/
        for(Zone z: zones)
        {
            if (z != null)
            {
                z.move(mX, mY);
                boolean zoneCheck = false;
                for (CollidableGroup cG: z.collidableGroups)
                {
                    if (cG != null)
                    {
                        cG.move(mX, mY);
                        boolean groupCheck = false;
                        for(Pixel p: cG.pixels)
                        {
                            if(p.live)
                            {
                                p.xDisp = p.xOriginal * cosA + p.yOriginal * sinA;
                                p.yDisp = p.yOriginal * cosA - p.xOriginal * sinA;
                                groupCheck = true;
                            }
                        }
                        if(groupCheck)
                        {
                            zoneCheck = true;
                        }
                        cG.live = groupCheck;
                    }
                }
                z.live = zoneCheck;
            }
        }
    }

    public void rotate(double a)
    {
        angle = a;
        cosA = (float)Math.cos(angle);
        sinA = (float)Math.sin(angle);

        for(Zone z: zones)
        {
            if (z != null)
            {
                z.rotate(cosA, sinA, centerX, centerY);
                for (CollidableGroup cG: z.collidableGroups)
                {
                    if (cG != null)
                    {
                        cG.rotate(cosA, sinA, centerX, centerY);
                    }
                }
            }
        }
    }

    public void setLoc(float mX, float mY)
    {
        centerX = mX;
        centerY = mY;
        if(enableLocationChain)
        {
            locationHead.nextLocation = new LocationHistory(centerX, centerY);
            LocationHistory prev = locationHead;
            locationHead = locationHead.nextLocation;
            prev.readyToBeConsumed = true;
        }
        /*for(Pixel p: pixels)
        {
            p.xDisp = p.xOriginal*cosA + p.yOriginal*sinA + centerX;
            p.yDisp = p.yOriginal*cosA - p.xOriginal*sinA + centerY;
        }

        for(int z = 0; z < zones.length; z++)
        {
            if (zones[z] != null)
            {
                zones[z].setLoc(mX, mY);
                for (int cG = 0; cG < zones[z].collidableGroups.length; cG++)
                    if (zones[z].collidableGroups[cG] != null)
                        zones[z].collidableGroups[cG].setLoc(mX, mY);
            }
        }*/
        for(Zone z: zones)
        {
            if (z != null)
            {
                z.setLoc(mX, mY);
                //boolean zoneCheck = false;
                for (CollidableGroup cG: z.collidableGroups)
                {
                    if (cG != null)
                    {
                        cG.setLoc(mX, mY);
                        /*boolean groupCheck = false;
                        for(Pixel p: cG.pixels)
                        {
                            p.xDisp = p.xOriginal*cosA + p.yOriginal*sinA + centerX;
                            p.yDisp = p.yOriginal*cosA - p.xOriginal*sinA + centerY;
                            if(p.outside)
                                groupCheck = true;
                        }
                        if(groupCheck)
                        {
                            zoneCheck = true;
                        }
                        cG.live = groupCheck;*/
                    }
                }
                //z.live = zoneCheck;
            }
        }
    }

    public void knockBack(float tempDistX, float tempDistY)
    {
        centerX += tempDistX;
        centerY += tempDistY;

        for(Zone z: zones)
        {
            if (z != null)
            {
                z.move(tempDistX, tempDistY);
                //boolean zoneCheck = false;
                for (CollidableGroup cG: z.collidableGroups)
                {
                    if (cG != null)
                    {
                        cG.move(tempDistX, tempDistY);
                        /*boolean groupCheck = false;
                        for(Pixel p: cG.pixels)
                        {
                            p.xDisp += tempDistX;
                            p.yDisp += tempDistY;
                            if(p.outside)
                                groupCheck = true;
                        }
                        if(groupCheck)
                        {
                            zoneCheck = true;
                        }
                        cG.live = groupCheck;*/
                    }
                }
                //z.live = zoneCheck;
            }
        }
    }

    public void setNeedsUpdate(boolean n)
    {
        needsUpdate = n;
    }

    public boolean getChunkDeletion()
    {
        return enableOrphanChunkDeletion;
    }

    public float getCenterX()
    {
        return centerX;
    }

    public float getCenterY()
    {
        return centerY;
    }

    public float getHalfSquareLength()
    {
        return halfSquareLength;
    }

    public Pixel[] getPixels()
    {
        return pixels;
    }

    public boolean getCollidableLive()
    {
        return collidableLive;
    }

    public void setCollidableLive(boolean b)
    {
        collidableLive = b;
    }

    public int getTotalPixels()
    {
        return totalPixels;
    }

    public float getLivablePercentage()
    {
        return livablePercentage;
    }

}
