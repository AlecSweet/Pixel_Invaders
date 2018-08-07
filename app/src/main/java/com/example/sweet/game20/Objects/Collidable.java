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

    public Pixel[][] pMap;

    public PixelInfo[][] infoMap;

    public Zone[] zones;

    protected CollidableGroup[] totalGroups;

    public int
            totalPixels,
            numLivePixels;

    public Pixel lastPixelKilled;

    protected boolean restorable = false;

    public Collidable(float x, float y, float hSL, Pixel[] p, boolean chunkDeletion, Zone[] z, CollidableGroup[] g, PixelInfo[][] iM)
    {
        centerX = x;
        centerY = y;

        halfSquareLength = hSL;
        pixels = p;
        infoMap = iM;
        totalPixels = pixels.length;
        numLivePixels = totalPixels;
        enableOrphanChunkDeletion = chunkDeletion;
        zones = z;
        totalGroups = g;
        collidableLive = true;
        needsUpdate = false;
        lastOrphanChunkCheck = System.currentTimeMillis();
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
    }

    public Collidable(float x, float y, float hSL, Pixel[] p, boolean chunkDeletion, Zone[] z, PixelInfo[][] iM)
    {
        centerX = x;
        centerY = y;

        halfSquareLength = hSL;
        pixels = p;
        infoMap = iM;
        totalPixels = pixels.length;
        numLivePixels = totalPixels;
        enableOrphanChunkDeletion = chunkDeletion;
        zones = z;
        collidableLive = true;
        needsUpdate = false;
        lastOrphanChunkCheck = System.currentTimeMillis();

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
    }

    public void resetLocationHistory(float x, float y)
    {
        locationHead.x = x;
        locationHead.y = y;
        locationDrawTail = locationHead;
    }

    public void move(float mX, float mY)
    {
        centerX += mX;
        centerY += mY;

        for(Zone z: zones)
        {
            if (z != null)
            {
                boolean zoneCheck = false;
                for (CollidableGroup cG: z.collidableGroups)
                {
                    if (cG != null)
                    {
                        boolean groupCheck = false;
                        for(Pixel p: cG.pixels)
                        {
                            //if(p.live)
                            if(p.state >= 1)
                            {
                                p.xDisp = infoMap[p.row][p.col].xOriginal * cosA +
                                        infoMap[p.row][p.col].yOriginal * sinA;
                                p.yDisp = infoMap[p.row][p.col].yOriginal * cosA -
                                        infoMap[p.row][p.col].xOriginal * sinA;
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
        move(0,0);
    }

    public void resetPixels()
    {
        for(Pixel p: pixels)
        {
            /*p.live = true;
            p.outside = false;
            p.insideEdge = false;*/
            p.state = 1;
            p.groupFlag = -1;
            if (pMap[p.row + 1][p.col] == null)
            {
                //p.outside = true;
                p.state = 2;
            }
            else if (pMap[p.row - 1][p.col] == null)
            {
                //p.outside = true;
                p.state = 2;
            }
            else if (pMap[p.row][p.col + 1] == null)
            {
                //p.outside = true;
                p.state = 2;
            }
            else if (pMap[p.row][p.col - 1] == null)
            {
                //p.outside = true;
                p.state = 2;
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
