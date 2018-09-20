package com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System;

import com.example.sweet.Pixel_Invaders.Game_Objects.Pooling.LocationHistory;

/**
 * Created by Sweet on 3/26/2018.
 */

public class Collidable
{
    public volatile float
            centerX,
            centerY;

    public float
            cosA = 1,
            sinA = 0,
            speed,
            centerMassX = 0,
            centerMassY = 0,
            angleKnockback = 0,
            posKnockbackX = 0,
            posKnockbackY = 0;

    public volatile int pixelsKilled = 0;

    public float
            halfSquareLength,
            livablePercentage = .5f,
            knockBackFactor = .006f,
            regenDelay = 4,
            lastRegenTime = 0;

    public long
            orphanChunkCheckDelay = 100,
            lastOrphanChunkCheck;

    public volatile float angle = 0;

    boolean enableOrphanChunkDeletion,
            enableLocationChain = true;

    LocationHistory locationDrawTail;

    private LocationHistory
            locationCollisionTail,
            prevColLoc,
            locationHead;

    boolean
            collidableLive,
            needsUpdate;

    public boolean isBullet = false;

    public volatile boolean aiNeedsUpdate = false;

    protected Pixel[] pixels;

    public Pixel[][] pMap;

    public PixelInfo[][] infoMap;

    public Zone[] zones;

    public boolean gotHit = false;

    CollidableGroup[] totalGroups;

    public int
            totalPixels,
            numLivePixels,
            health = 1;

    public Pixel lastPixelKilled;

    public boolean
            restorable = false,
            knockable = true,
            regen = false;

    public volatile boolean
            readyToKnockback = false,
            readyToScreenShake = false;

    Collidable(float hSL, Pixel[] p, boolean chunkDeletion, Zone[] z, CollidableGroup[] g, PixelInfo[][] iM)
    {
        centerX = 5;
        centerY = 5;

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
        for(int i = 0; i < 6; i++)
        {
            locationHead.nextLocation = new LocationHistory(4f,4f);
            locationHead = locationHead.nextLocation;
        }

        locationHead.nextLocation = locationDrawTail;
        locationHead = locationDrawTail;
    }

    Collidable(float hSL, Pixel[] p, boolean chunkDeletion, Zone[] z, PixelInfo[][] iM, Pixel[][] pM)
    {
        centerX = 5;
        centerY = 5;

        halfSquareLength = hSL;
        pixels = p;
        pMap = pM;
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
        for(int i = 0; i < 8; i++)
        {
            locationHead.nextLocation = new LocationHistory(4f,4f);
            locationHead = locationHead.nextLocation;
        }

        locationHead.nextLocation = locationDrawTail;
        locationHead = locationDrawTail;
    }

    Collidable(float hSL, Pixel[] p, boolean chunkDeletion, Zone[] z, PixelInfo[][] iM)
    {
        centerX = 5;
        centerY = 5;

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
        for(int i = 0; i < 6; i++)
        {
            locationHead.nextLocation = new LocationHistory(4f,4f);
            locationHead = locationHead.nextLocation;
        }

        locationHead.nextLocation = locationDrawTail;
        locationHead = locationDrawTail;
    }

    public void resetLocationHistory(float x, float y)
    {
        prevColLoc = locationHead;
        do{
            prevColLoc = prevColLoc.nextLocation;
            prevColLoc.setLocation(x, y);
            prevColLoc.setPrevLocation(x, y);
        } while(prevColLoc != locationHead);

        locationHead.setLocation(x, y);
        locationHead.setPrevLocation(x, y);

        locationDrawTail = locationHead;
        prevColLoc = locationHead;
        locationCollisionTail = locationHead;
    }

    public void move(float mX, float mY)
    {
        centerX += mX;
        centerY += mY;
        if(knockable && readyToKnockback)
        {
            centerX += posKnockbackX;
            centerY += posKnockbackY;
            angle += angleKnockback;

            rotate(angle);
            posKnockbackX = 0;
            posKnockbackY = 0;
            angleKnockback = 0;
            readyToKnockback = false;
        }
        float tempCMX = 0;
        float tempCMY = 0;
        int numOutside = 0;
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
                            if(p.state >= 1)
                            {
                                groupCheck = true;
                                if(p.state >= 2)
                                {
                                    p.xDisp = infoMap[p.row][p.col].xOriginal * cosA +
                                            infoMap[p.row][p.col].yOriginal * sinA;
                                    p.yDisp = infoMap[p.row][p.col].yOriginal * cosA -
                                            infoMap[p.row][p.col].xOriginal * sinA;
                                    tempCMX += infoMap[p.row][p.col].xOriginal;
                                    tempCMY += infoMap[p.row][p.col].yOriginal;
                                    numOutside++;
                                }
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
        if(numOutside > 0)
        {
            tempCMX /= numOutside;
            tempCMY /= numOutside;
            centerMassX = tempCMX;
            centerMassY = tempCMY;
        }
    }

    public void rotate(float a)
    {
        angle = a;
        cosA = (float)Math.cos(angle);
        sinA = (float)Math.sin(angle);

        for(Zone z: zones)
        {
            if (z != null)
            {
                z.centerMassX = centerMassX;
                z.centerMassY = centerMassY;
                z.rotate(cosA, sinA);
                for (CollidableGroup cG: z.collidableGroups)
                {
                    if (cG != null)
                    {
                        cG.centerMassX = centerMassX;
                        cG.centerMassY = centerMassY;
                        cG.rotate(cosA, sinA);
                    }
                }
            }
        }
    }

    public void killPixel(Pixel p)
    {
        Pixel temp;
        p.health = 0;
        p.state = 0;
        temp = pMap[p.row + 1][p.col];
        if (temp != null && temp.state == 1)
        {
            temp.xDisp = infoMap[temp.row][temp.col].xOriginal * cosA +
                    infoMap[temp.row][temp.col].yOriginal * sinA;
            temp.yDisp = infoMap[temp.row][temp.col].yOriginal * cosA -
                    infoMap[temp.row][temp.col].xOriginal * sinA;
            temp.state = 3;
        }

        temp = pMap[p.row - 1][p.col];
        if (temp != null && temp.state == 1)
        {
            temp.xDisp = infoMap[temp.row][temp.col].xOriginal * cosA +
                    infoMap[temp.row][temp.col].yOriginal * sinA;
            temp.yDisp = infoMap[temp.row][temp.col].yOriginal * cosA -
                    infoMap[temp.row][temp.col].xOriginal * sinA;
            temp.state = 3;
        }

        temp = pMap[p.row][p.col + 1];
        if (temp != null && temp.state == 1)
        {
            temp.xDisp = infoMap[temp.row][temp.col].xOriginal * cosA +
                    infoMap[temp.row][temp.col].yOriginal * sinA;
            temp.yDisp = infoMap[temp.row][temp.col].yOriginal * cosA -
                    infoMap[temp.row][temp.col].xOriginal * sinA;
            temp.state = 3;
        }

        temp = pMap[p.row][p.col - 1];
        if (temp != null && temp.state == 1)
        {
            temp.xDisp = infoMap[temp.row][temp.col].xOriginal * cosA +
                    infoMap[temp.row][temp.col].yOriginal * sinA;
            temp.yDisp = infoMap[temp.row][temp.col].yOriginal * cosA -
                    infoMap[temp.row][temp.col].xOriginal * sinA;
            temp.state = 3;
        }

    }

    public void hitPixel(Pixel p)
    {
        p.health--;
        if(p.health <= 0)
        {
            killPixel(p);
            /*Pixel temp;
            p.state = 0;
            temp = pMap[p.row + 1][p.col];
            if (temp != null && temp.state == 1)
            {
                temp.xDisp = infoMap[temp.row][temp.col].xOriginal * cosA +
                        infoMap[temp.row][temp.col].yOriginal * sinA;
                temp.yDisp = infoMap[temp.row][temp.col].yOriginal * cosA -
                        infoMap[temp.row][temp.col].xOriginal * sinA;
                temp.state = 3;
            }

            temp = pMap[p.row - 1][p.col];
            if (temp != null && temp.state == 1)
            {
                temp.xDisp = infoMap[temp.row][temp.col].xOriginal * cosA +
                        infoMap[temp.row][temp.col].yOriginal * sinA;
                temp.yDisp = infoMap[temp.row][temp.col].yOriginal * cosA -
                        infoMap[temp.row][temp.col].xOriginal * sinA;
                temp.state = 3;
            }

            temp = pMap[p.row][p.col + 1];
            if (temp != null && temp.state == 1)
            {
                temp.xDisp = infoMap[temp.row][temp.col].xOriginal * cosA +
                        infoMap[temp.row][temp.col].yOriginal * sinA;
                temp.yDisp = infoMap[temp.row][temp.col].yOriginal * cosA -
                        infoMap[temp.row][temp.col].xOriginal * sinA;
                temp.state = 3;
            }

            temp = pMap[p.row][p.col - 1];
            if (temp != null && temp.state == 1)
            {
                temp.xDisp = infoMap[temp.row][temp.col].xOriginal * cosA +
                        infoMap[temp.row][temp.col].yOriginal * sinA;
                temp.yDisp = infoMap[temp.row][temp.col].yOriginal * cosA -
                        infoMap[temp.row][temp.col].xOriginal * sinA;
                temp.state = 3;
            }*/
        }
    }

    public void setLoc(float mX, float mY)
    {
        centerX = mX;
        centerY = mY;
        //move(0,0);
    }

    public void resetPixels()
    {
        for(Pixel p: pixels)
        {
            p.state = 1;
            p.health = health;
            p.groupFlag = -1;
            if (pMap[p.row + 1][p.col] == null)
            {
                p.state = 2;
            }
            else if (pMap[p.row - 1][p.col] == null)
            {
                p.state = 2;
            }
            else if (pMap[p.row][p.col + 1] == null)
            {
                p.state = 2;
            }
            else if (pMap[p.row][p.col - 1] == null)
            {
                p.state = 2;
            }
        }
        collidableLive = true;
        needsUpdate = true;
        numLivePixels = totalPixels;
    }

    public void knockBack(float tempDistX, float tempDistY)
    {
        centerX += tempDistX;
        centerY += tempDistY;
    }

    public void setNeedsUpdate(boolean n)
    {
        aiNeedsUpdate = n;
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

    public float getPrevCenterX()
    {
        if(enableLocationChain)
        {
            return locationCollisionTail.prevX;
        }
        else
        {
            return centerX;
        }
    }

    public float getPrevCenterY()
    {
        if(enableLocationChain)
        {
            return locationCollisionTail.prevY;
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
            prevColLoc = locationCollisionTail;
            locationCollisionTail.collisionDone = true;
            locationCollisionTail = locationCollisionTail.nextLocation;
        }

        return locationCollisionTail.frame;
    }

    public void publishLocation(long frame)
    {
        if(enableLocationChain)
        {
            locationHead.nextLocation.setLocation(centerX,centerY);
            locationHead.nextLocation.setPrevLocation(locationHead.x, locationHead.y);
            locationHead.nextLocation.frame = frame;
            LocationHistory prev = locationHead;
            locationHead = locationHead.nextLocation;
            prev.readyToBeConsumed = true;
        }
    }

    public void revivePixels(int resNum)
    {
        for(Pixel p: pixels)
        {
            if(p.state > 0)
            {
                if (pMap[p.row + 1][p.col] != null &&
                        pMap[p.row + 1][p.col].state == 0 &&
                        resNum > 0)
                {
                    resNum = revivePixelHelper(pMap[p.row + 1][p.col], resNum);
                }
                else if (pMap[p.row - 1][p.col] != null &&
                        pMap[p.row - 1][p.col].state == 0 &&
                        resNum > 0)
                {
                    resNum = revivePixelHelper(pMap[p.row - 1][p.col], resNum);
                }
                else if (pMap[p.row][p.col + 1] != null &&
                        pMap[p.row][p.col + 1].state == 0 &&
                        resNum > 0)
                {
                    resNum = revivePixelHelper(pMap[p.row][p.col + 1], resNum);
                }
                else if (pMap[p.row][p.col - 1] != null &&
                        pMap[p.row][p.col - 1].state == 0 &&
                        resNum > 0)
                {
                    resNum = revivePixelHelper(pMap[p.row][p.col - 1], resNum);
                }
            }
            if(resNum <= 0)
            {
                break;
            }
        }

        for(Pixel p: pixels)
        {
            if(p.state > 0)
            {
                p.state = 1;
                if (pMap[p.row + 1][p.col] == null)
                {
                    if (p.state < 3)
                    {
                        p.state = 2;
                    }
                }
                else if (pMap[p.row + 1][p.col].state == 0)
                {
                    p.state = 3;
                }

                if (pMap[p.row - 1][p.col] == null)
                {
                    if (p.state < 3)
                    {
                        p.state = 2;
                    }
                }
                else if (pMap[p.row - 1][p.col].state == 0)
                {
                    p.state = 3;
                }

                if (pMap[p.row][p.col + 1] == null)
                {
                    if (p.state < 3)
                    {
                        p.state = 2;
                    }
                }
                else if (pMap[p.row][p.col + 1].state == 0)
                {
                    p.state = 3;
                }

                if (pMap[p.row][p.col - 1] == null)
                {
                    if (p.state < 3)
                    {
                        p.state = 2;
                    }
                }
                else if (pMap[p.row][p.col - 1].state == 0)
                {
                    p.state = 3;
                }
            }
        }
        needsUpdate = true;
    }

    private int revivePixelHelper(Pixel p, int rN)
    {
        int resNum = rN;
        p.state = 1;
        p.health = health;
        numLivePixels++;
        resNum--;

        if (pMap[p.row + 1][p.col] != null &&
                pMap[p.row + 1][p.col].state == 0 &&
                resNum > 0)
        {
            resNum = revivePixelHelper(pMap[p.row + 1][p.col], resNum);
        }
        else if (pMap[p.row - 1][p.col] != null &&
                pMap[p.row - 1][p.col].state == 0 &&
                resNum > 0)
        {
            resNum = revivePixelHelper(pMap[p.row - 1][p.col], resNum);
        }
        else if (pMap[p.row][p.col + 1] != null &&
                pMap[p.row][p.col + 1].state == 0 &&
                resNum > 0)
        {
            resNum = revivePixelHelper(pMap[p.row][p.col + 1], resNum);
        }
        else if (pMap[p.row][p.col - 1] != null &&
                pMap[p.row][p.col - 1].state == 0 &&
                resNum > 0)
        {
            resNum = revivePixelHelper(pMap[p.row][p.col - 1], resNum);
        }

        return resNum;
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

    public boolean getEnableLocationChain()
    {
        return enableLocationChain;
    }

    public void setEnableLocationChain(boolean f)
    {
        enableLocationChain = f;
    }

    public void addMaxHealth(int h)
    {
        health += h;
        for(Pixel p: pixels)
        {
            if(p.state > 0)
            {
                p.health += h;
            }
        }
    }

    public void reduceMaxHealth(int h)
    {
        health -= h;
        for(Pixel p: pixels)
        {
            if(p.state > 0)
            {
                if(p.health - h > 0)
                {
                    p.health -= h;
                }
                else
                {
                    p.health = 1;
                }
            }
        }
    }
}
