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
            enableLocationChain = true;

    protected LocationHistory
            locationHead,
            locationDrawTail,
            locationCollisionTail;
            //locationPoolHead,
            //locationPoolTail;

    protected boolean
            collidableLive,
            needsUpdate;

    protected Pixel[] pixels;

    public Zone[] zones;

    public int
            totalPixels,
            numLivePixels;

    public Pixel lastPixelKilled;

    protected boolean restorable = false;

    public Collidable(float x, float y, float hSL, Pixel[] p, boolean chunkDeletion, Zone[] z)
    {
        centerX = x;
        centerY = y;

        halfSquareLength = hSL;
        pixels = p;
        totalPixels = pixels.length;
        numLivePixels = totalPixels;
        enableOrphanChunkDeletion = chunkDeletion;
        zones = z;
        collidableLive = true;
        needsUpdate = false;
        lastOrphanChunkCheck = System.currentTimeMillis();

        /*locationPoolTail = new LocationHistory(0,0);
        locationPoolHead = locationPoolTail;
        for(int i = 0; i < 4; i++)
        {
            locationPoolHead.nextLocation = new LocationHistory(0,0);
            locationPoolHead = locationPoolHead.nextLocation;
        }



        locationPoolTail.x = centerX;
        locationPoolTail.y = centerY;
        locationHead = locationPoolTail;
        locationDrawTail = locationPoolTail;
        locationCollisionTail = locationPoolTail;
        locationPoolTail = locationPoolTail.nextLocation;
        locationHead.nextLocation = null;*/
        locationDrawTail = new LocationHistory(4f, 4f);
        locationCollisionTail = locationDrawTail;
        locationHead = locationDrawTail;
        for(int i = 0; i < 9; i++)
        {
            locationHead.nextLocation = new LocationHistory(4f,4f);
            locationHead = locationHead.nextLocation;
        }

        locationHead.nextLocation = locationDrawTail;
        locationHead = locationDrawTail;

       /* int itr = 0;
        while(locationPoolTail.nextLocation != null)
        {
            itr++;
            System.out.println(itr);
            locationPoolTail = locationPoolTail.nextLocation;
        }*/
    }

    public void move(float mX, float mY)
    {
        centerX += mX;
        centerY += mY;

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
                //z.move(mX, mY);
                boolean zoneCheck = false;
                for (CollidableGroup cG: z.collidableGroups)
                {
                    if (cG != null)
                    {
                        //cG.move(mX, mY);
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
                z.rotate(cosA, sinA);
                for (CollidableGroup cG: z.collidableGroups)
                {
                    if (cG != null)
                    {
                        cG.rotate(cosA, sinA);
                    }
                }
            }
        }
    }

    public void setLoc(float mX, float mY)
    {
        centerX = mX;
        centerY = mY;
        /*if(enableLocationChain)
        {
            locationHead.nextLocation = new LocationHistory(centerX, centerY);
            locationHead.nextLocation.chainID = locationHead.chainID++;
            LocationHistory prev = locationHead;
            locationHead = locationHead.nextLocation;
            prev.readyToBeConsumed = true;
        }*/
        move(0,0);
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
        /*for(Zone z: zones)
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
                        *//*boolean groupCheck = false;
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
                        cG.live = groupCheck;*//*
                    }
                }
                //z.live = zoneCheck;
            }
        }*/
    }

    public void resetPixels()
    {
        for(Pixel p: pixels)
        {
            p.live = true;
            p.outside = false;
            p.insideEdge = false;
            p.groupFlag = -1;
            for(Pixel n: p.neighbors)
            {
                if (n == null)
                {
                    p.outside = true;
                }
            }
        }
        collidableLive = true;
        needsUpdate = true;
        numLivePixels = totalPixels;
    }

    /*public void resetLocationHistory(float x, float y)
    {
        locationDrawTail = new LocationHistory(x, y);
        locationCollisionTail = locationDrawTail;
        locationHead = locationDrawTail;
    }*/

    public void knockBack(float tempDistX, float tempDistY)
    {
        centerX += tempDistX;
        centerY += tempDistY;

        for(Zone z: zones)
        {
            if (z != null)
            {
                z.move(tempDistX, tempDistY);
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
        if(enableLocationChain)
        {
            return locationCollisionTail.x;
        }
        else
        {
            return centerX;
        }
    }

    public float getCenterY()
    {
        if(enableLocationChain)
        {
            return locationCollisionTail.y;
        }
        else
        {
            return centerY;
        }
    }
    
    public long consumeCollisionLocation(long currentFrame)
    {
        if(locationCollisionTail.nextLocation != null &&
                locationCollisionTail.nextLocation.readyToBeConsumed &&
                locationCollisionTail.frame < currentFrame)
        {
            locationCollisionTail = locationCollisionTail.nextLocation;
        }

        return locationCollisionTail.frame;
        /*f(enableLocationChain)
        {
            if(locationCollisionTail.nextLocation != null && locationCollisionTail.nextLocation.readyToBeConsumed)
            {
                locationCollisionTail.collisionConsumed = true;
                if (locationCollisionTail.uiConsumed)
                {
                    locationPoolHead.nextLocation = locationCollisionTail;
                    locationPoolHead = locationHead.nextLocation;
                    locationCollisionTail = locationCollisionTail.nextLocation;
                    locationPoolHead.reset();
                } else
                {
                    locationCollisionTail = locationCollisionTail.nextLocation;
                }
            }
        }*/
    }

    public void publishLocation(long frame)
    {
        if(enableLocationChain)
        {
            locationHead.nextLocation.setLocation(centerX,centerY);
            locationHead.nextLocation.frame = frame;
            LocationHistory prev = locationHead;
            locationHead = locationHead.nextLocation;
            prev.readyToBeConsumed = true;
            /*locationPoolTail.x = centerX;
            locationPoolTail.y = centerY;

            locationHead.nextLocation = locationPoolTail;

            locationHead = locationHead.nextLocation;
            if (locationPoolTail.nextLocation != null)
            {
                locationPoolTail.readyToBeConsumed = true;
                locationPoolTail = locationPoolTail.nextLocation;
            }*/

        }
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

    public void setRestorable(boolean b)
    {
        restorable = b;
    }
}
