package com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System;

import com.example.sweet.Pixel_Invaders.Game_Objects.Pooling.LocationHistory;
import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;

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
            angleKnockback = 0,
            posKnockbackX = 0,
            posKnockbackY = 0,
            tempCenterX,
            tempCenterY;

    public volatile int pixelsKilled = 0;

    public float
            halfSquareLength,
            livablePercentage = .5f,
            knockBackFactor = .006f,
            regenDelay = 4,
            lastRegenTime = 0;

    public long
            orphanChunkCheckDelay = 0,
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

    public boolean gotHit = false;

    public int
            totalPixels,
            numLivePixels;

    public byte
            health = 1;

    public Pixel lastPixelKilled;

    public boolean
            restorable = false,
            knockable = true,
            regen = false;

    public volatile boolean
            readyToKnockback = false,
            readyToScreenShake = false;

    private HitboxNode hitBox;

    private ParticleSystem particleSystem;

    Collidable(float hSL, Pixel[] p, boolean chunkDeletion, PixelInfo[][] iM)
    {
        centerX = 5;
        centerY = 5;

        halfSquareLength = hSL;
        pixels = p;
        infoMap = iM;
        totalPixels = pixels.length;
        numLivePixels = totalPixels;
        enableOrphanChunkDeletion = chunkDeletion;
        collidableLive = true;
        needsUpdate = false;
        lastOrphanChunkCheck = System.currentTimeMillis();
        locationDrawTail = new LocationHistory(4f, 4f);
        locationCollisionTail = locationDrawTail;
        locationHead = locationDrawTail;
        for(int i = 0; i < 4; i++)
        {
            locationHead.nextLocation = new LocationHistory(4f,4f);
            locationHead = locationHead.nextLocation;
        }

        locationHead.nextLocation = locationDrawTail;
        locationHead = locationDrawTail;
    }

    Collidable(float hSL, Pixel[] p, boolean chunkDeletion, PixelInfo[][] iM, Pixel[][] pM)
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
        collidableLive = true;
        needsUpdate = false;
        lastOrphanChunkCheck = System.currentTimeMillis();

        locationDrawTail = new LocationHistory(4f, 4f);
        locationCollisionTail = locationDrawTail;
        locationHead = locationDrawTail;
        for(int i = 0; i < 4; i++)
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
    }

    public void rotate(float a)
    {
        angle = a;
        cosA = (float)Math.cos(angle);
        sinA = (float)Math.sin(angle);
    }

    public void killPixel(Pixel p)
    {
        Pixel temp;
        p.health = 0;
        p.state = 0;
        temp = pMap[p.row + 1][p.col];
        if (temp != null && temp.state == 1)
        {
            temp.state = 3;
            if(temp.parent != null)
            {
                temp.parent.transmitNewOutsidePixel();
            }
        }

        temp = pMap[p.row - 1][p.col];
        if (temp != null && temp.state == 1)
        {
            temp.state = 3;
            if(temp.parent != null)
            {
                temp.parent.transmitNewOutsidePixel();
            }
        }

        temp = pMap[p.row][p.col + 1];
        if (temp != null && temp.state == 1)
        {
            temp.state = 3;
            if(temp.parent != null)
            {
                temp.parent.transmitNewOutsidePixel();
            }
        }

        temp = pMap[p.row][p.col - 1];
        if (temp != null && temp.state == 1)
        {
            temp.state = 3;
            if(temp.parent != null)
            {
                temp.parent.transmitNewOutsidePixel();
            }
        }

        /*addPixelKillParticle(
                infoMap[p.row][p.col].xOriginal * cosA + infoMap[p.row][p.col].yOriginal * sinA,
                infoMap[p.row][p.col].yOriginal * cosA - infoMap[p.row][p.col].xOriginal * sinA,
                p,
                ps
        );*/
    }

    public int hitPixel(Pixel p)
    {
        p.health--;
        gotHit = true;
        if(p.health <= 0)
        {
            killPixel(p);
            return 1;
        }

        return 0;
    }

    public void setLoc(float mX, float mY)
    {
        centerX = mX;
        centerY = mY;
    }

    public void resetPixels()
    {
        int nL = 0;
        for(Pixel p: pixels)
        {
            p.health = health;
            p.groupFlag = -1;
            p.state = infoMap[p.row][p.col].originalState;
            if(p.state > 0)
            {
                nL++;
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
        }
        collidableLive = true;
        needsUpdate = true;
        numLivePixels = nL;
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


        if (pMap[p.row][p.col + 1] != null &&
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
        else if (pMap[p.row + 1][p.col] != null &&
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

    public void addPixelKillParticleCenter(Pixel p, ParticleSystem ps)
    {
        ps.addParticle(
                infoMap[p.row][p.col].xOriginal * cosA + infoMap[p.row][p.col].yOriginal * sinA + getCenterX(),
                infoMap[p.row][p.col].yOriginal * cosA - infoMap[p.row][p.col].xOriginal * sinA + getCenterY(),
                -infoMap[p.row][p.col].angleOriginal - angle + (float)Math.random() * .2f - .1f,
                infoMap[p.row][p.col].r,
                infoMap[p.row][p.col].g,
                infoMap[p.row][p.col].b,
                1.2f,
                (float)Math.random() + .1f,
                (float)Math.random() * .5f + .01f,
                (float)Math.random() * 40f - 20f
        );
    }

    public void addPixelKillParticle(Pixel p, ParticleSystem ps)
    {
        ps.addParticle(
                infoMap[p.row][p.col].xOriginal * cosA + infoMap[p.row][p.col].yOriginal * sinA + tempCenterX,
                infoMap[p.row][p.col].yOriginal * cosA - infoMap[p.row][p.col].xOriginal * sinA + tempCenterY,
                -infoMap[p.row][p.col].angleOriginal - angle + (float)Math.random() * .2f - .1f,
                infoMap[p.row][p.col].r,
                infoMap[p.row][p.col].g,
                infoMap[p.row][p.col].b,
                1.2f,
                (float)Math.random() + .1f,
                (float)Math.random() * .5f + .01f,
                (float)Math.random() * 40f - 20f
        );
    }

    public void addChunkKillParticle(float angle, float speedRand, float distRand, Pixel p, ParticleSystem ps)
    {
        ps.addParticle(
                infoMap[p.row][p.col].xOriginal * cosA + infoMap[p.row][p.col].yOriginal * sinA + tempCenterX,
                infoMap[p.row][p.col].yOriginal * cosA - infoMap[p.row][p.col].xOriginal * sinA + tempCenterY,
                angle,
                infoMap[p.row][p.col].r,
                infoMap[p.row][p.col].g,
                infoMap[p.row][p.col].b,
                1f,
                speedRand,
                distRand,
                0
        );
    }

    public HitboxNode getHitBox()
    {
        return hitBox;
    }

    public void setHitbox(HitboxNode hitBox)
    {
        this.hitBox = hitBox;
    }
}
