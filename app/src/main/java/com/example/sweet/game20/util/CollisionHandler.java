package com.example.sweet.game20.util;

import com.example.sweet.game20.Objects.Collidable;
import com.example.sweet.game20.Objects.CollidableGroup;
import com.example.sweet.game20.Objects.Enemy;
import com.example.sweet.game20.Objects.ParticleSystem;
import com.example.sweet.game20.Objects.Pixel;
import com.example.sweet.game20.Objects.Zone;

/**
 * Created by Sweet on 3/26/2018.
 */

public class CollisionHandler
{
    private ParticleSystem collisionParticles;

    public CollisionHandler(ParticleSystem pS)
    {
        collisionParticles = pS;
    }

    public int[] groupSizes =  new int[100];

    public int checkCollisions(Collidable c, Collidable c2)
    {
        boolean collision = false;
        //boolean lastItr = false;
        int numCheck = 0;
        int numKilled = 0;
        float tempX = 0;
        float tempY = 0;
        float tempX2 = 0;
        float tempY2 = 0;

        float cCenterX = c.getCenterX();
        float cCenterY = c.getCenterY();

        //float cPCenterX = c.getPrevColCenterX();
        //float cPCenterY = c.getPrevColCenterY();

        float cPCenterX = c.getPrevCenterX();
        float cPCenterY = c.getPrevCenterY();
        /*float cPCenterX = cCenterX;
        float cPCenterY = cCenterY;
        if(c.getPrevLocation() != null)
        {
            cPCenterX = cCenterX + c.cosA * c.speed;
            cPCenterY = cCenterY - c.sinA * c.speed;
        }*/

        float cDistX = cCenterX - cPCenterX;
        float cDistY = cCenterY - cPCenterY;
        /*float cDistX = cPCenterX - cCenterX;
        float cDistY = cPCenterY - cCenterY;*/
        //float cMaxDist = VectorFunctions.getMagnitude(cDistX, cDistY);

        float c2CenterX = c2.getCenterX();
        float c2CenterY = c2.getCenterY();

        float c2PCenterX = c2.getPrevCenterX();
        float c2PCenterY = c2.getPrevCenterY();
        /*float c2PCenterX = c2CenterX;
        float c2PCenterY = c2CenterY;
        if(c2.getPrevLocation() != null)
        {
            c2PCenterX = c2CenterX + c2.cosA * c2.speed;
            c2PCenterY = c2CenterY - c2.sinA * c2.speed;
        }*/
        float c2DistX = c2CenterX - c2PCenterX;
        float c2DistY = c2CenterY - c2PCenterY;
        /*float c2DistX = c2PCenterX - c2CenterX;
        float c2DistY = c2PCenterY - c2CenterY;*/
        //float c2MaxDist = VectorFunctions.getMagnitude(c2DistX, c2DistY);
        /*float cCenterX = c.getColCenterX();
        float cCenterY = c.getColCenterY();
        float cPCenterX = c.getPrevColCenterX();
        float cPCenterY = c.getPrevColCenterY();
        float cDistX = cCenterX - cPCenterX;
        float cDistY = cCenterY - cPCenterY;
        float cMaxDist = VectorFunctions.getMagnitude(cDistX, cDistY);

        float c2CenterX = c2.getColCenterX();
        float c2CenterY = c2.getColCenterY();
        float c2PCenterX = c2.getPrevColCenterX();
        float c2PCenterY = c2.getPrevColCenterY();
        float c2DistX = c2CenterX - c2PCenterX;
        float c2DistY = c2CenterY - c2PCenterY;
        float c2MaxDist = VectorFunctions.getMagnitude(c2DistX, c2DistY);*/

        numCheck++;
        for(float cDistBtwn = 0; cDistBtwn <= c.speed; cDistBtwn += Constants.PIXEL_SIZE/2f)
        {
            if(c.speed > Constants.PIXEL_SIZE)
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
                cCenterX = cPCenterX + cDistX * percent;
                cCenterY = cPCenterY + cDistY * percent;
            }

            for(float c2DistBtwn = 0; c2DistBtwn <= c2.speed; c2DistBtwn += Constants.PIXEL_SIZE)
            {
                if(c2.speed > Constants.PIXEL_SIZE)
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
                    c2CenterX = c2PCenterX + c2DistX * percent;
                    c2CenterY = c2PCenterY + c2DistY * percent;
                }

                if (Math.abs(cCenterX - c2CenterX) <=
                        c.getHalfSquareLength() + c2.getHalfSquareLength() &&
                    Math.abs(cCenterY - c2CenterY) <=
                        c.getHalfSquareLength() + c2.getHalfSquareLength())
                {
                    boolean stillNeedsChecks = true;
                    while(stillNeedsChecks)
                    {
                        stillNeedsChecks = false;
                        for (Zone z : c.zones)
                        {
                            if (z.live)
                            {
                                for (Zone z2 : c2.zones)
                                {
                                    numCheck++;
                                    if (z2.live &&
                                            Math.abs((z.xDisp + cCenterX) - (z2.xDisp + c2CenterX)) <=
                                                    z.halfSquareLength + z2.halfSquareLength &&
                                            Math.abs((z.yDisp + cCenterY) - (z2.yDisp + c2CenterY)) <=
                                                    z.halfSquareLength + z2.halfSquareLength)
                                    {
                                        for (CollidableGroup cG : z.collidableGroups)
                                        {
                                            if (cG.live)
                                            //if(true)
                                            {
                                                for (CollidableGroup cG2 : z2.collidableGroups)
                                                {
                                                    numCheck++;
                                                    if (cG2.live &&
                                                            Math.abs((cG.xDisp + cCenterX) - (cG2.xDisp + c2CenterX)) <=
                                                                    cG.halfSquareLength + cG2.halfSquareLength &&
                                                            Math.abs((cG.yDisp + cCenterY) - (cG2.yDisp + c2CenterY)) <=
                                                                    cG.halfSquareLength + cG2.halfSquareLength)
                                                    {
                                                        for (Pixel p : cG.pixels)
                                                        {
                                                            //if (p.live && p.outside)
                                                            if (p.state >= 2)
                                                            {
                                                                for (Pixel p2 : cG2.pixels)
                                                                {
                                                                    //if (p2.live && p2.outside)
                                                                    if (p2.state >= 2)
                                                                    {
                                                                        numCheck++;
                                                                        if (Math.abs((p.xDisp + cCenterX) - (p2.xDisp + c2CenterX)) <=
                                                                                Constants.PIXEL_SIZE + .001 &&
                                                                            Math.abs((p.yDisp + cCenterY) - (p2.yDisp + c2CenterY)) <=
                                                                                Constants.PIXEL_SIZE + .001)
                                                                        {
                                                                            addParticleHelper(p, c, cCenterX, cCenterY);
                                                                            //p.killPixel(c.cosA, c.sinA);
                                                                            p.killPixel(c.pMap);
                                                                            c.numLivePixels--;
                                                                            c.lastPixelKilled = p;
                                                                            tempX += p.xDisp;
                                                                            tempY += p.yDisp;

                                                                            addParticleHelper(p2, c2, c2CenterX, c2CenterY);
                                                                            //p2.killPixel(c2.cosA, c2.sinA);
                                                                            p2.killPixel(c2.pMap);
                                                                            c2.numLivePixels--;
                                                                            c2.lastPixelKilled = p2;
                                                                            tempX2 += p2.xDisp;
                                                                            tempY2 += p2.yDisp;

                                                                            numKilled++;
                                                                            collision = true;
                                                                            stillNeedsChecks = true;
                                                                            break;
                                                                        }
                                                                    }
                                                                    //if (!p.live)
                                                                    if (p.state == 0)
                                                                    {
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                /*if(lastItr)
                {
                    lastItr = false;
                    break;
                }

                c2DistBtwn += Constants.MAX_DIST_JUMP;
                if(c2DistBtwn > c2MaxDist)
                {
                    c2DistBtwn = c2MaxDist;
                    lastItr = true;
                }*/
            }


            /*if(lastItr)
            {
                break;
            }

            cDistBtwn += Constants.MAX_DIST_JUMP;
            if(cDistBtwn > cMaxDist)
            {
                cDistBtwn = cMaxDist;
                lastItr = true;
            }*/
        }
        /*numCheck++;
        if (Math.abs(c.getCenterX() - c2.getCenterX()) <=
                c.getHalfSquareLength() + c2.getHalfSquareLength() &&
            Math.abs(c.getCenterY() - c2.getCenterY()) <=
                c.getHalfSquareLength() + c2.getHalfSquareLength())
        {
            for(Zone z: c.zones)
            {
                if (z.live)
                {
                    for (Zone z2 : c2.zones)
                    {
                        numCheck++;
                        if (z2.live &&
                            Math.abs((z.xDisp + c.getCenterX()) - (z2.xDisp + c2.getCenterX())) <=
                                    z.halfSquareLength + z2.halfSquareLength &&
                            Math.abs((z.yDisp + c.getCenterY()) - (z2.yDisp + c2.getCenterY())) <=
                                    z.halfSquareLength + z2.halfSquareLength)
                        {
                            for (CollidableGroup cG : z.collidableGroups)
                            {
                                if(cG.live)
                                //if(true)
                                {
                                    for (CollidableGroup cG2 : z2.collidableGroups)
                                    {
                                        numCheck++;
                                        if (cG2.live &&
                                            Math.abs((cG.xDisp + c.getCenterX()) - (cG2.xDisp + c2.getCenterX())) <=
                                                    cG.halfSquareLength + cG2.halfSquareLength &&
                                            Math.abs((cG.yDisp + c.getCenterY()) - (cG2.yDisp + c2.getCenterY())) <=
                                                    cG.halfSquareLength + cG2.halfSquareLength)
                                        {
                                            for (Pixel p : cG.pixels)
                                            {
                                                //if (p.live && p.outside)
                                                if(p.state >= 2)
                                                {
                                                    for (Pixel p2 : cG2.pixels)
                                                    {
                                                        //if (p2.live && p2.outside)
                                                        if(p2.state >= 2)
                                                        {
                                                            numCheck++;
                                                            if (Math.abs((p.xDisp + c.getCenterX()) - (p2.xDisp + c2.getCenterX())) <=
                                                                        Constants.PIXEL_SIZE + .01 &&
                                                                Math.abs((p.yDisp + c.getCenterY()) - (p2.yDisp + c2.getCenterY())) <=
                                                                        Constants.PIXEL_SIZE + .01)
                                                            {
                                                                addParticleHelper(p, c);
                                                                //p.killPixel(c.cosA, c.sinA);
                                                                p.killPixel(c.pMap);
                                                                c.numLivePixels--;
                                                                c.lastPixelKilled = p;
                                                                tempX += p.xDisp;
                                                                tempY += p.yDisp;

                                                                addParticleHelper(p2, c2);
                                                                //p2.killPixel(c2.cosA, c2.sinA);
                                                                p2.killPixel(c2.pMap);
                                                                c2.numLivePixels--;
                                                                c2.lastPixelKilled = p2;
                                                                tempX2 += p2.xDisp;
                                                                tempY2 += p2.yDisp;

                                                                numKilled++;
                                                                collision = true;
                                                                break;
                                                            }
                                                        }
                                                        //if (!p.live)
                                                        if(p.state == 0)
                                                        {
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }*/

            if(numKilled > 0)
            {
                if(c.knockable)
                {
                    float knockFactor = ((float)c2.totalPixels / (float)(c.totalPixels + c2.totalPixels))* c2.speed;
                    c.posKnockbackX += -c2.cosA * knockFactor;
                    c.posKnockbackY += c2.sinA * knockFactor;
                    c.pixelsKilled += numKilled;

                    float colPointX = tempX / numKilled;
                    float colPointY = tempY / numKilled;

                    float tX = colPointX - c.centerMassX;
                    float tY = colPointY - c.centerMassY;
                    //float tX = c.centerMassX - colPointX;
                    //float tY = c.centerMassY - colPointY;

                    float dist = (float)Math.sqrt(tX * tX + tY * tY);
                    float torqueAng = (float)Math.atan2(tY, tX);
                    if(torqueAng < 0)
                    {
                        torqueAng += Constants.twoPI;
                    }
                    torqueAng -= c2.angle;
                    //torqueAng = (float)((torqueAng + Math.PI) % (Math.PI * 2) - Math.PI);
                    //System.out.println(torqueAng);
                    torqueAng = (float)Math.sin(torqueAng);

                    torqueAng *= knockFactor * dist * 30f;
                    c.angleKnockback += torqueAng;
                }

                if(c2.knockable)
                {
                    float knockFactor = ((float)c.totalPixels / (float)(c.totalPixels + c2.totalPixels)) * c.speed;
                    c2.posKnockbackX += -c.cosA * knockFactor;
                    c2.posKnockbackY += c.sinA * knockFactor;
                    c2.pixelsKilled += numKilled;

                    float colPointX = tempX2 / numKilled;
                    float colPointY = tempY2 / numKilled;

                    float tX = colPointX - c2.centerMassX;
                    float tY = colPointY - c2.centerMassY;

                    /*float tX = c2.centerMassX - colPointX;
                    float tY = c2.centerMassY - colPointY;*/

                    float dist = (float)Math.sqrt(tX * tX + tY * tY);
                    float torqueAng = (float)Math.atan2(tY, tX);
                    if(torqueAng < 0)
                    {
                        torqueAng += Constants.twoPI;
                    }
                    //System.out.println(torqueAng + " - " + c.angle + " = " + (torqueAng - c.angle));
                    torqueAng -= c.angle;
                    //torqueAng = (float)((torqueAng + Math.PI) % (Math.PI * 2) - Math.PI);
                    //System.out.println(torqueAng);
                    torqueAng = (float)Math.sin(torqueAng);

                    torqueAng *= knockFactor * dist * 30f;
                    System.out.println(torqueAng);
                    c2.angleKnockback += torqueAng;
                }
            }
        //}

        /*if(collision)
        {
            if(c2.getChunkDeletion())
            {
                if (c2.totalPixels > 1000)
                {
                    if (System.currentTimeMillis() - c2.lastOrphanChunkCheck > c2.orphanChunkCheckDelay)
                    {
                        //long startTime = System.currentTimeMillis();
                        //int numLive = c2.numLivePixels;
                        c2.lastOrphanChunkCheck = System.currentTimeMillis();
                        removeOrphanChunks(c2);
                        //System.out.println(System.currentTimeMillis() - startTime + "ms   size: " + numLive);
                    }
                }
                else
                {
                    removeOrphanChunksRecursive(c2);
                }
            }

            c2.setNeedsUpdate(true);

            if(c.getChunkDeletion())
            {
                if (c.totalPixels > 1000)
                {
                    if (System.currentTimeMillis() - c.lastOrphanChunkCheck > c.orphanChunkCheckDelay)
                    {
                        //long startTime = System.currentTimeMillis();
                        //int numLive = c2.numLivePixels;
                        c.lastOrphanChunkCheck = System.currentTimeMillis();
                        removeOrphanChunks(c);
                        //System.out.println(System.currentTimeMillis() - startTime + "ms   size: " + numLive);
                    }
                }
                else
                {
                    removeOrphanChunksRecursive(c);
                }
            }
            c.setNeedsUpdate(true);
        }*/
        if(collision)
        {
            if(c2.getChunkDeletion())
            {
                if (System.currentTimeMillis() - c2.lastOrphanChunkCheck > c2.orphanChunkCheckDelay)
                {
                    removeOrphanChunksRecursive(c2);
                }
            }

            c2.setNeedsUpdate(true);

            if(c.getChunkDeletion())
            {
                if (System.currentTimeMillis() - c.lastOrphanChunkCheck > c.orphanChunkCheckDelay)
                {
                    removeOrphanChunksRecursive(c);
                }
            }
            c.setNeedsUpdate(true);
        }
        return numCheck;
    }

    public static void preventOverlap(Enemy e, Enemy e2)
    {
        float cX = e2.getPixelGroup().getCenterX() - e.getPixelGroup().getCenterX();
        float cY = e2.getPixelGroup().getCenterY() - e.getPixelGroup().getCenterY();
        float radius = e2.getPixelGroup().getHalfSquareLength() + e.getPixelGroup().getHalfSquareLength();

        if (cX * cX + cY * cY < radius * radius)
        {
            if(e.getPixelGroup().totalPixels <= e2.getPixelGroup().totalPixels)
            {
                if(overlapHelper(e.getPixelGroup(), e2.getPixelGroup()))
                {
                    e.knockBack((float) Math.atan2(-cY, -cX), 1, .008f);
                }
            }
        }
    }

    private static boolean overlapHelper(Collidable c, Collidable c2)
    {
        for(Zone z: c.zones)
        {
            if (z.live)
            {
                for (Zone z2 : c2.zones)
                {
                    if (z2.live &&
                        Math.abs((z.xDisp + c.getCenterX()) - (z2.xDisp + c2.getCenterX())) <=
                                z.halfSquareLength + z2.halfSquareLength &&
                        Math.abs((z.yDisp + c.getCenterY()) - (z2.yDisp + c2.getCenterY())) <=
                                z.halfSquareLength + z2.halfSquareLength)
                    {
                        for (CollidableGroup cG : z.collidableGroups)
                        {
                            if(cG.live)
                            {
                                for (CollidableGroup cG2 : z2.collidableGroups)
                                {
                                    if (cG2.live &&
                                        Math.abs((cG.xDisp + c.getCenterX()) - (cG2.xDisp + c2.getCenterX())) <=
                                                cG.halfSquareLength + cG2.halfSquareLength &&
                                        Math.abs((cG.yDisp + c.getCenterY()) - (cG2.yDisp + c2.getCenterY())) <=
                                                cG.halfSquareLength + cG2.halfSquareLength)
                                    {
                                        /*for (Pixel p : cG.pixels)
                                        {
                                            //if (p.live && p.outside)
                                            if(p.state >= 2)
                                            {
                                                for (Pixel p2 : cG2.pixels)
                                                {
                                                    //if (p2.live && p2.outside)
                                                    if(p2.state >= 2)
                                                    {
                                                        if (Math.abs((p.xDisp + c.getCenterX()) - (p2.xDisp + c2.getCenterX())) <=
                                                                Constants.PIXEL_SIZE + .012 &&
                                                            Math.abs((p.yDisp + c.getCenterY()) - (p2.yDisp + c2.getCenterY())) <=
                                                                Constants.PIXEL_SIZE + .012)
                                                        {
                                                            return true;
                                                        }
                                                    }
                                                }
                                            }
                                        }*/
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
   /* public void removeOrphanChunks(Collidable c)
    {
        int groupNumberIterator = 0;
        int highestSize = 0;

        for (Pixel p : c.getPixels())
        {
            if (p.live)
            {
                if (p.groupFlag == -1)
                {
                    for (Pixel n : p.neighbors)
                    {
                        if (n != null && n.live && n.groupFlag != -1)
                        {
                            p.groupFlag = n.groupFlag;
                        }
                    }
                }
                if (p.groupFlag == -1)
                {
                    p.groupFlag = groupNumberIterator;
                    groupNumberIterator++;
                }

                for (Pixel n : p.neighbors)
                {
                    if (n != null && n.live)
                    {
                        n.groupFlag = p.groupFlag;
                    }
                }
            }
        }

        if(groupNumberIterator > 0)
        {
            Mapping[] mappings = new Mapping[groupNumberIterator];
            for (Pixel p : c.getPixels())
            {
                if (p.live && p.groupFlag != -1)
                {
                    if (mappings[p.groupFlag] == null)
                    {
                        mappings[p.groupFlag] = new Mapping(p.groupFlag);
                        mappings[p.groupFlag].size = 1;
                    }
                    else
                    {
                        mappings[p.groupFlag].size += 1;
                    }

                    for (Pixel n : p.neighbors)
                    {
                        if (n != null && n.live && n.groupFlag != -1 && n.groupFlag != p.groupFlag)
                        {
                            if (mappings[n.groupFlag] == null)
                            {
                                mappings[n.groupFlag] = new Mapping(n.groupFlag);
                                mappings[n.groupFlag].size = 1;
                            }
                            mappings[p.groupFlag].connections.add(n.groupFlag);
                            mappings[n.groupFlag].connections.add(p.groupFlag);
                        }
                    }
                }
            }

            for (Mapping m : mappings)
            {
                if (m != null)
                {
                    for (Mapping m1 : mappings)
                    {
                        if (m1 != null && m != m1 && m1.connections != m.connections)
                        {
                            for (Integer i : m.connections)
                            {
                                if (m1.connections.contains(i))
                                {
                                    m1.connections.addAll(m.connections);
                                    m.connections = m1.connections;
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            for (Mapping mapping : mappings)
            {
                if (mapping != null)
                {
                    for (Integer connection : mapping.connections)
                    {
                        mapping.totalSize += mappings[connection].size;
                    }
                    if (mapping.totalSize > highestSize)
                    {
                        highestSize = mapping.totalSize;
                    }
                }
            }

            if(c.getCollidableLive())
            {
                for (Pixel p : c.getPixels())
                {
                    if (p.groupFlag != -1 && mappings[p.groupFlag].totalSize < highestSize)
                    {
                        p.xDisp = p.xOriginal * c.cosA + p.yOriginal * c.sinA;
                        p.yDisp = p.yOriginal * c.cosA - p.xOriginal * c.sinA;
                        addParticleHelper(p, c);
                        p.killPixel(c.pMap);
                        c.numLivePixels--;
                    }
                    p.groupFlag = -1;
                }
            }
        }
        else
        {
            for (Pixel p : c.getPixels())
            {
                p.groupFlag = -1;
            }
        }

    }*/

    public void destroyCollidableAnimation(Collidable c)
    {
        for (Pixel p : c.getPixels())
        {
            //if(p.live)
            if(p.state >= 1)
            {
                p.xDisp = c.infoMap[p.row][p.col].xOriginal * c.cosA + c.infoMap[p.row][p.col].yOriginal * c.sinA;
                p.yDisp = c.infoMap[p.row][p.col].yOriginal * c.cosA - c.infoMap[p.row][p.col].xOriginal * c.sinA;
                addParticleHelper(p, c);
                p.killPixel(c.pMap);
                c.numLivePixels--;
                c.pixelsKilled++;
            }
        }
    }

    public void removeOrphanChunksRecursive(Collidable c)
    {
        int groupNumberIterator = 0;
        int highestSize = 0;

        /*ArrayList<Integer> groupSizes = new ArrayList<>();
        for(Pixel p: c.getPixels())
        {
            if(p != null && p.live && p.groupFlag == -1)
            {
                groupSizes.add(orphanChunkHelper(p, groupNumberIterator, 0));
                if(groupSizes.get(groupNumberIterator) > highestSize)
                {
                    highestSize = groupSizes.get(groupNumberIterator);
                }
                groupNumberIterator++;
            }
        }*/
        for(Pixel p: c.getPixels())
        {
            //if(p != null && p.live && p.groupFlag == -1)
            if(p != null && p.state >= 1 && p.groupFlag == -1)
            {
                groupSizes[groupNumberIterator] = (orphanChunkHelper(p, groupNumberIterator, 0, c.pMap));
                if(groupSizes[groupNumberIterator] > highestSize)
                {
                    highestSize = groupSizes[groupNumberIterator];
                }
                groupNumberIterator++;
            }
        }

        if(highestSize < c.getTotalPixels() * c.getLivablePercentage())
        {
            c.setCollidableLive(false);
        }

        if(c.getCollidableLive())
        {
            for (Pixel p : c.getPixels())
            {
                if (p.groupFlag != -1 && groupSizes[p.groupFlag] < highestSize)
                {
                    p.killPixel(c.pMap);
                    c.pixelsKilled++;
                    c.numLivePixels--;
                    addParticleHelper(p, c);
                }
                p.groupFlag = -1;
            }
        }
        else
        {
            for (Pixel p : c.getPixels())
            {
                //if(p.live)
                if(p.state >= 1)
                {
                    p.killPixel(c.pMap);
                    c.pixelsKilled++;
                    c.numLivePixels--;
                    addParticleHelper(p, c);
                }
                p.groupFlag = -1;
            }
        }
    }

    private int orphanChunkHelper(Pixel p, int groupNum, int total, Pixel[][] pMap)
    {
        p.groupFlag = groupNum;
        /*for(int i = 0; i < 4; i++)
        {
            if (p.neighbors[i] != null && p.neighbors[i].live == true && p.neighbors[i].groupFlag == -1)
            {
                total = orphanChunkHelper(p.neighbors[i], groupNum, total);
            }
        }*/
        if (pMap[p.row + 1][p.col] != null &&
                pMap[p.row + 1][p.col].state >= 1 &&
                pMap[p.row + 1][p.col].groupFlag == -1)
        {
            total = orphanChunkHelper(pMap[p.row + 1][p.col], groupNum, total, pMap);
        }

        if (pMap[p.row - 1][p.col] != null &&
                pMap[p.row - 1][p.col].state >= 1 &&
                pMap[p.row - 1][p.col].groupFlag == -1)
        {
            total = orphanChunkHelper(pMap[p.row - 1][p.col], groupNum, total, pMap);
        }

        if (pMap[p.row][p.col + 1] != null &&
                pMap[p.row][p.col + 1].state >= 1 &&
                pMap[p.row][p.col + 1].groupFlag == -1)
        {
            total = orphanChunkHelper(pMap[p.row][p.col + 1], groupNum, total, pMap);
        }

        if (pMap[p.row][p.col - 1] != null &&
                pMap[p.row][p.col - 1].state >= 1 &&
                pMap[p.row][p.col - 1].groupFlag == -1)
        {
            total = orphanChunkHelper(pMap[p.row][p.col - 1], groupNum, total, pMap);
        }

        return 1 + total;
    }

    private void addParticleHelper(Pixel p, Collidable c, float centerX, float centerY)
    {
        float angle = (float)(Math.atan2(p.yDisp, p.xDisp) + Math.random() * .2 - .1);
        collisionParticles.addParticle(
                p.xDisp + centerX,
                p.yDisp + centerY,
                angle,
                c.infoMap[p.row][p.col].r,
                c.infoMap[p.row][p.col].g,
                c.infoMap[p.row][p.col].b,
                2f,
                (float)(Math.random())+.1f,
                (float)(Math.random()*.5)+.01f,
                (float)(Math.random()*40)-20
        );
    }

    private void addParticleHelper(Pixel p, Collidable c)
    {
        float angle = (float)(Math.atan2(p.yDisp, p.xDisp) + Math.random() * .2 - .1);
        collisionParticles.addParticle(
                p.xDisp + c.getCenterX(),
                p.yDisp + c.getCenterY(),
                angle,
                c.infoMap[p.row][p.col].r,
                c.infoMap[p.row][p.col].g,
                c.infoMap[p.row][p.col].b,
                2f,
                (float)(Math.random())+.1f,
                (float)(Math.random()*.5)+.01f,
                (float)(Math.random()*40)-20
        );
    }
}
