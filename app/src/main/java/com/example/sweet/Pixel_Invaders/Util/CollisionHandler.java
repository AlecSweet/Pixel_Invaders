package com.example.sweet.Pixel_Invaders.Util;

import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.CollisionMethods;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.GroupNum;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Collidable;
import com.example.sweet.Pixel_Invaders.Game_Objects.Enemies.Enemy;
import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Pixel;

/**
 * Created by Sweet on 3/26/2018.
 */

public class CollisionHandler
{
    private ParticleSystem collisionParticles;
    //private int colltime;

    private int[] groupSizes = new int[100];
    private GroupNum[] groups = new GroupNum[400];

    public CollisionHandler(ParticleSystem pS)
    {
        collisionParticles = pS;
        for(int i = 0; i < groups.length; i++)
        {
            groups[i] = new GroupNum((short)-1);
        }
    }


    public int checkCollisions(PixelGroup c, PixelGroup c2)
    {
        boolean collision = false;
        int numKilled = 0;

        //long start = System.nanoTime();
        float cPCenterX = c.getPrevCenterX();
        float cPCenterY = c.getPrevCenterY();
        float cDistX = c.getCenterX() - cPCenterX;
        float cDistY = c.getCenterY() - cPCenterY;

        float c2PCenterX = c2.getPrevCenterX();
        float c2PCenterY = c2.getPrevCenterY();
        float c2DistX = c2.getCenterX() - c2PCenterX;
        float c2DistY = c2.getCenterY() - c2PCenterY;
        float length = c.halfSquareLength + c2.halfSquareLength;
        for(float cDistBtwn = 0; cDistBtwn <= c.speed; cDistBtwn += Constants.MAX_DIST_JUMP)
        {
            //if(c.speed > Constants.PIXEL_SIZE)
            if(c.speed > Constants.MAX_DIST_JUMP)
            {
                float percent;
                if (c.speed > 0)
                {
                    percent = cDistBtwn / c.speed;
                }
                else
                {
                    percent = 0;
                }
                c.tempCenterX = cPCenterX + cDistX * percent;
                c.tempCenterY = cPCenterY + cDistY * percent;
            }
            else
            {
                c.tempCenterX = c.getCenterX();
                c.tempCenterY = c.getCenterY();
            }

            for(float c2DistBtwn = 0; c2DistBtwn <= c2.speed; c2DistBtwn += Constants.MAX_DIST_JUMP)
            {
                //if(c2.speed > Constants.PIXEL_SIZE)
                if(c2.speed > Constants.MAX_DIST_JUMP)
                {
                    float percent;
                    if (c2.speed > 0)
                    {
                        percent = c2DistBtwn / c2.speed;
                    }
                    else
                    {
                        percent = 0;
                    }
                    c2.tempCenterX = c2PCenterX + c2DistX * percent;
                    c2.tempCenterY = c2PCenterY + c2DistY * percent;
                }
                else
                {
                    c2.tempCenterX = c2.getCenterX();
                    c2.tempCenterY = c2.getCenterY();
                }

                if(Math.abs(c.tempCenterX - c2.tempCenterX) < length &&
                        Math.abs(c.tempCenterY - c2.tempCenterY) < length)
                {
                    int tempKilled = CollisionMethods.checkCollision(c, c2, collisionParticles);
                    if (tempKilled > 0)
                    {
                        numKilled += tempKilled;
                        collision = true;
                    }
                }
            }
        }
        /*if(collision)
        {
            if(colltime > 0)
            {
                colltime += (System.nanoTime() - startime) / 1000;
                colltime /= 2;
            }
            else
            {
                colltime = (int)((System.nanoTime() - startime) / 1000);
            }
            System.out.println("collision: " + colltime);
        }*/
        //if(collision)
        //    System.out.println("collision: " + (System.nanoTime() - start)/1000);
        if(numKilled > 0)
        {
            if(c.knockable)
            {
                float knockFactor = ((float)c2.totalPixels / (float)(c.totalPixels + c2.totalPixels))
                        * c2.speed
                        *.2f
                        * numKilled / (float)c2.totalPixels;
                c.posKnockbackX += -c2.cosA * knockFactor;
                c.posKnockbackY += c2.sinA * knockFactor;
                c.pixelsKilled += numKilled;

               /* float colPointX = tempX / numKilled;
                float colPointY = tempY / numKilled;

                float tX = colPointX - c.centerMassX;
                float tY = colPointY - c.centerMassY;

                float dist = (float)Math.sqrt(tX * tX + tY * tY);
                float torqueAng = (float)Math.atan2(tY, tX);
                if(torqueAng < 0)
                {
                    torqueAng += Constants.twoPI;
                }
                torqueAng -= c2.angle;
                torqueAng = (float)Math.sin(torqueAng);

                torqueAng *= knockFactor * dist * 30f;
                c.angleKnockback += torqueAng;*/
            }

            if(c2.knockable)
            {
                float knockFactor = ((float)c.totalPixels / (float)(c.totalPixels + c2.totalPixels))
                        * c.speed
                        *.2f
                        * numKilled / (float)c.totalPixels;
                c2.posKnockbackX += -c.cosA * knockFactor;
                c2.posKnockbackY += c.sinA * knockFactor;
                c2.pixelsKilled += numKilled;

               /* float colPointX = tempX2 / numKilled;
                float colPointY = tempY2 / numKilled;

                float tX = colPointX - c2.centerMassX;
                float tY = colPointY - c2.centerMassY;

                float dist = (float)Math.sqrt(tX * tX + tY * tY);
                float torqueAng = (float)Math.atan2(tY, tX);
                if(torqueAng < 0)
                {
                    torqueAng += Constants.twoPI;
                }
                torqueAng -= c.angle;
                torqueAng = (float)Math.sin(torqueAng);

                torqueAng *= knockFactor * dist * 30f;
                c2.angleKnockback += torqueAng;*/
            }
        }


        if(collision)
        {
            if(c2.getChunkDeletion())
            {
                if (System.currentTimeMillis() - c2.lastOrphanChunkCheck > c2.orphanChunkCheckDelay)
                {
                    c2.lastOrphanChunkCheck = System.currentTimeMillis();
                    if(c2.totalPixels <= 1000)
                    {
                        numKilled += removeOrphanChunksRecursive(c2);
                    }
                    else
                    {
                        numKilled += removeOrphanChunks(c2);
                    }
                }
            }
            c2.setNeedsUpdate(true);

            if(c.getChunkDeletion())
            {
                if (System.currentTimeMillis() - c.lastOrphanChunkCheck > c.orphanChunkCheckDelay)
                {
                    c.lastOrphanChunkCheck = System.currentTimeMillis();
                    if(c.totalPixels <= 1000)
                    {
                        removeOrphanChunksRecursive(c);
                    }
                    else
                    {
                        removeOrphanChunks(c);
                    }
                }
            }
            c.setNeedsUpdate(true);
            //System.out.println("Orphan: " + (System.nanoTime() - start)/1000);
        }

        return numKilled;
    }

    public static void preventOverlap(Enemy e, Enemy e2)
    {
        float cX = e2.getPixelGroup().getCenterX() - e.getPixelGroup().getCenterX();
        float cY = e2.getPixelGroup().getCenterY() - e.getPixelGroup().getCenterY();
        float radius = e2.getPixelGroup().getHalfSquareLength() + e.getPixelGroup().getHalfSquareLength();

        if (cX * cX + cY * cY < radius * radius)
        {
            e.knockBack((float) Math.atan2(-cY, -cX), 1, e.getPixelGroup().speed/2);
        }
    }

    private static boolean overlapHelper(Collidable c, Collidable c2)
    {
        /*for(Zone z: c.zones)
        {
            if (z.live)
            {
                for (Zone z2 : c2.zones)
                {
                    if (z2.live &&
                        Math.abs((z.xDisp + c.getCenterX()) - (z2.xDisp + c2.getCenterX())) <=
                                z.getHalfSquareLength() + z2.getHalfSquareLength() &&
                        Math.abs((z.yDisp + c.getCenterY()) - (z2.yDisp + c2.getCenterY())) <=
                                z.getHalfSquareLength() + z2.getHalfSquareLength())
                    {
                        for (CollidableGroup cG : z.collidableGroups)
                        {
                            if(cG.live)
                            {
                                for (CollidableGroup cG2 : z2.collidableGroups)
                                {
                                    if (cG2.live &&
                                        Math.abs((cG.xDisp + c.getCenterX()) - (cG2.xDisp + c2.getCenterX())) <=
                                                cG.getHalfSquareLength() + cG2.getHalfSquareLength() &&
                                        Math.abs((cG.yDisp + c.getCenterY()) - (cG2.yDisp + c2.getCenterY())) <=
                                                cG.getHalfSquareLength() + cG2.getHalfSquareLength())
                                    {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }*/
        return false;
    }

    public void destroyCollidable(PixelGroup c)
    {
        for (Pixel p : c.getPixels())
        {
            if (p.state > 0)
            {
                c.killPixel(p);
                c.addPixelKillParticle(p, collisionParticles);
            }
            p.groupFlag = -1;
        }
    }

    private int removeOrphanChunksRecursive(PixelGroup c)
    {
        short groupNumberIterator = 0;
        int highestSize = 0;
        int largestGroup = -1;

        for(Pixel p: c.getPixels())
        {
            if (p.state > 0)
            {
                if (p.groupFlag == -1)
                {
                    orphanChunkHelper(p, groupNumberIterator, c.pMap);

                    if (groupSizes[groupNumberIterator] > highestSize)
                    {
                        highestSize = groupSizes[groupNumberIterator];
                        largestGroup = groupNumberIterator;
                    }
                    groupNumberIterator++;
                }
            }
        }

        int numKilled = 0;
        float angle = c.infoMap[c.lastPixelKilled.row][c.lastPixelKilled.col].angleOriginal + (float)Math.PI;
        float speedRand = (float)Math.random() * .1f + .1f;
        float distRand = (float)Math.random() * .2f + .1f;

        for(Pixel p: c.getPixels())
        {
            if (p.state > 0 && p.groupFlag != largestGroup)
            {
                c.killPixel(p);
                if(groupSizes[p.groupFlag] < 300)
                {
                    c.addChunkKillParticle(
                            angle + (float) Math.random() * .2f - .1f,
                            speedRand,
                            distRand,
                            p,
                            collisionParticles);
                }
                else
                {
                    c.addPixelKillParticle(p, collisionParticles);
                }
                numKilled++;
            }
            p.groupFlag = -1;
        }

        if(highestSize < c.getTotalPixels() * c.getLivablePercentage())
        {
            c.setCollidableLive(false);
            destroyCollidable(c);
            c.pixelsKilled += c.getTotalPixels();
        }

        return numKilled;
    }

    private void orphanChunkHelper(Pixel p, short groupNum, Pixel[][] pMap)
    {
        p.groupFlag = groupNum;
        groupSizes[groupNum]++;

        Pixel curNeighbor = pMap[p.row + 1][p.col];
        if (curNeighbor != null &&
                curNeighbor.state >= 1 &&
                curNeighbor.groupFlag == -1)
        {
            orphanChunkHelper(curNeighbor, groupNum, pMap);
        }
        curNeighbor = pMap[p.row - 1][p.col];
        if (curNeighbor != null &&
                curNeighbor.state >= 1 &&
                curNeighbor.groupFlag == -1)
        {
            orphanChunkHelper(curNeighbor, groupNum, pMap);
        }
        curNeighbor = pMap[p.row][p.col + 1];
        if (curNeighbor != null &&
                curNeighbor.state >= 1 &&
                curNeighbor.groupFlag == -1)
        {
            orphanChunkHelper(curNeighbor, groupNum, pMap);
        }
        curNeighbor = pMap[p.row][p.col - 1];
        if (curNeighbor != null &&
                curNeighbor.state >= 1 &&
                curNeighbor.groupFlag == -1)
        {
            orphanChunkHelper(curNeighbor, groupNum, pMap);
        }
    }

    public int removeOrphanChunks(PixelGroup c)
    {
        short groupNumberIterator = 0;
        for (Pixel p : c.getPixels())
        {
            if (p.state > 0)
            {
                Pixel curNeighbor;
                if (p.groupFlag == -1)
                {
                    curNeighbor = c.pMap[p.row - 1][p.col];
                    if (curNeighbor != null &&
                            curNeighbor.state >= 1 &&
                            curNeighbor.groupFlag != -1)
                    {
                        p.groupFlag = curNeighbor.groupFlag;
                    }

                    curNeighbor = c.pMap[p.row][p.col - 1];
                    if (curNeighbor != null &&
                            curNeighbor.state >= 1 &&
                            curNeighbor.groupFlag != -1)
                    {
                        p.groupFlag = curNeighbor.groupFlag;
                    }
                }

                if (p.groupFlag == -1)
                {
                    groups[groupNumberIterator].size = 0;
                    groups[groupNumberIterator].num = -1;
                    p.groupFlag = groupNumberIterator++;
                }

                groups[p.groupFlag].size++;

                curNeighbor = c.pMap[p.row - 1][p.col];
                if (curNeighbor != null &&
                        curNeighbor.groupFlag > -1 &&
                        curNeighbor.groupFlag != p.groupFlag)
                {
                    reducePairing(groups, curNeighbor.groupFlag, p.groupFlag);
                }

                curNeighbor = c.pMap[p.row][p.col - 1];
                if (curNeighbor != null &&
                        curNeighbor.groupFlag > -1 &&
                        curNeighbor.groupFlag != p.groupFlag)
                {
                    reducePairing(groups, curNeighbor.groupFlag, p.groupFlag);
                }
            }
        }

        /*
            Reduce all keys to their smallest value at the end of the path and add
            their size to that value
         */
        int highestSize = 0;
        short largestGroup = -1;
        for(int i = 0; i < groupNumberIterator; i++)
        {
            GroupNum currentVal = groups[i];
            short currentKey = currentVal.num;

            while (currentVal.num != -1)
            {
                currentKey = currentVal.num;
                currentVal = groups[currentKey];
            }

            if (currentKey != -1)
            {
                groups[i].num = currentKey;
                groups[currentKey].size += groups[i].size;
                if(groups[currentKey].size > highestSize)
                {
                    highestSize = groups[currentKey].size;
                    largestGroup = currentKey;
                }
            }
        }

        int numKilled = 0;
        float angle = c.infoMap[c.lastPixelKilled.row][c.lastPixelKilled.col].angleOriginal + (float)Math.PI;
        float speedRand = (float)Math.random() * .1f + .1f;
        float distRand = (float)Math.random() * .2f + .1f;
        for (Pixel p : c.getPixels())
        {
            if (p.state > 0 &&
                    groups[p.groupFlag].num != largestGroup &&
                    p.groupFlag != largestGroup)
            {
                c.killPixel(p);
                if(groups[p.groupFlag].size < 300)
                {
                    c.addChunkKillParticle(
                            angle + (float) Math.random() * .2f - .1f,
                            speedRand,
                            distRand,
                            p,
                            collisionParticles);
                }
                else
                {
                    c.addPixelKillParticle(p, collisionParticles);
                }
                numKilled++;
            }
            p.groupFlag = -1;
        }

        if(highestSize < c.getTotalPixels() * c.getLivablePercentage())
        {
            c.setCollidableLive(false);
            destroyCollidable(c);
            c.pixelsKilled += c.getTotalPixels();
        }

        return numKilled;
    }

    private void reducePairing(GroupNum[] groups, short flag1, short flag2)
    {
        if(flag1 > flag2)
        {
            if(groups[flag1].num == -1)
            {
                groups[flag1].num = flag2;
            }
            else if(groups[flag1].num > flag2)
            {
                reducePairing(groups, groups[flag1].num, flag2);
                groups[flag1].num = flag2;
            }
            else if(groups[flag1].num < flag2)
            {
                reducePairing(groups, groups[flag1].num, flag2);
            }
        }
        else if(flag2 > flag1)
        {
            if(groups[flag2].num == -1)
            {
                groups[flag2].num = flag1;
            }
            else if(groups[flag2].num > flag1)
            {
                reducePairing(groups, groups[flag2].num, flag1);
                groups[flag2].num = flag1;
            }
            else if(groups[flag2].num < flag1)
            {
                reducePairing(groups, groups[flag2].num, flag1);
            }
        }
    }
}
