package com.example.sweet.Pixel_Invaders.Util;

import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Collidable;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.CollidableGroup;
import com.example.sweet.Pixel_Invaders.Game_Objects.Enemies.Enemy;
import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Pixel;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Zone;

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

    private int[] groupSizes =  new int[100];

    public int checkCollisions(PixelGroup c, PixelGroup c2)
    {
        boolean collision = false;
        int numKilled = 0;
        /*float tempX = 0;
        float tempY = 0;
        float tempX2 = 0;
        float tempY2 = 0;*/

        float cCenterX = c.getCenterX();
        float cCenterY = c.getCenterY();

        float cPCenterX = c.getPrevCenterX();
        float cPCenterY = c.getPrevCenterY();

        float cDistX = cCenterX - cPCenterX;
        float cDistY = cCenterY - cPCenterY;

        float c2CenterX = c2.getCenterX();
        float c2CenterY = c2.getCenterY();

        float c2PCenterX = c2.getPrevCenterX();
        float c2PCenterY = c2.getPrevCenterY();

        float c2DistX = c2CenterX - c2PCenterX;
        float c2DistY = c2CenterY - c2PCenterY;

        for(float cDistBtwn = 0; cDistBtwn <= c.speed; cDistBtwn += Constants.MAX_DIST_JUMP)
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

            for(float c2DistBtwn = 0; c2DistBtwn <= c2.speed; c2DistBtwn += Constants.MAX_DIST_JUMP)
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
                                    if (z2.live &&
                                            Math.abs((z.xDisp + cCenterX) - (z2.xDisp + c2CenterX)) <=
                                                    z.getHalfSquareLength() + z2.getHalfSquareLength() &&
                                            Math.abs((z.yDisp + cCenterY) - (z2.yDisp + c2CenterY)) <=
                                                    z.getHalfSquareLength() + z2.getHalfSquareLength())
                                    {
                                        for (CollidableGroup cG : z.collidableGroups)
                                        {
                                            if (cG.live)
                                            {
                                                for (CollidableGroup cG2 : z2.collidableGroups)
                                                {
                                                    if (cG2.live &&
                                                            Math.abs((cG.xDisp + cCenterX) - (cG2.xDisp + c2CenterX)) <=
                                                                    cG.getHalfSquareLength() + cG2.getHalfSquareLength() &&
                                                            Math.abs((cG.yDisp + cCenterY) - (cG2.yDisp + c2CenterY)) <=
                                                                    cG.getHalfSquareLength() + cG2.getHalfSquareLength())
                                                    {
                                                        for (Pixel p : cG.pixels)
                                                        {
                                                            if (p.state >= 2)
                                                            {
                                                                for (Pixel p2 : cG2.pixels)
                                                                {
                                                                    if (p2.state >= 2)
                                                                    {
                                                                        if (Math.abs((p.xDisp + cCenterX) - (p2.xDisp + c2CenterX)) <=
                                                                                Constants.PIXEL_SIZE + .001 &&
                                                                            Math.abs((p.yDisp + cCenterY) - (p2.yDisp + c2CenterY)) <=
                                                                                Constants.PIXEL_SIZE + .001)
                                                                        {
                                                                            c.hitPixel(p);
                                                                            if(p.state == 0)
                                                                            {
                                                                                addParticleHelper(p, c, cCenterX, cCenterY);
                                                                                c.numLivePixels--;
                                                                                c.lastPixelKilled = p;
                                                                            }
                                                                            c.gotHit = true;
                                                                            /*tempX += p.xDisp;
                                                                            tempY += p.yDisp;*/

                                                                            c2.hitPixel(p2);
                                                                            if(p2.state == 0)
                                                                            {
                                                                                addParticleHelper(p2, c2, c2CenterX, c2CenterY);
                                                                                c2.numLivePixels--;
                                                                                c2.lastPixelKilled = p2;
                                                                                numKilled++;
                                                                            }
                                                                            c2.gotHit = true;
                                                                            /*tempX2 += p2.xDisp;
                                                                            tempY2 += p2.yDisp;*/

                                                                            collision = true;
                                                                            stillNeedsChecks = true;
                                                                            break;
                                                                        }
                                                                    }
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
            }
        }
        if(numKilled > 0)
        {
            if(c.knockable)
            {
                float knockFactor = ((float)c2.totalPixels / (float)(c.totalPixels + c2.totalPixels))
                        * c2.speed
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
                    numKilled += removeOrphanChunksRecursive(c2);
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
        for(Zone z: c.zones)
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
        }
        return false;
    }

    public void destroyCollidableAnimation(Collidable c)
    {
        for (Pixel p : c.getPixels())
        {
            if(p.state >= 1)
            {
                p.xDisp = c.infoMap[p.row][p.col].xOriginal * c.cosA + c.infoMap[p.row][p.col].yOriginal * c.sinA;
                p.yDisp = c.infoMap[p.row][p.col].yOriginal * c.cosA - c.infoMap[p.row][p.col].xOriginal * c.sinA;
                addParticleHelper(p, c);
                c.killPixel(p);
                c.numLivePixels--;
                c.pixelsKilled++;
            }
        }
    }

    private int removeOrphanChunksRecursive(PixelGroup c)
    {
        int groupNumberIterator = 0;
        int highestSize = 0;
        int numKilled = 0;

        for(Pixel p: c.getPixels())
        {
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
                    p.xDisp = c.infoMap[p.row][p.col].xOriginal * c.cosA + c.infoMap[p.row][p.col].yOriginal * c.sinA;
                    p.yDisp = c.infoMap[p.row][p.col].yOriginal * c.cosA - c.infoMap[p.row][p.col].xOriginal * c.sinA;
                    c.killPixel(p);
                    c.updatePixel(p);
                    numKilled++;
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
                if(p.state >= 1)
                {
                    p.xDisp = c.infoMap[p.row][p.col].xOriginal * c.cosA + c.infoMap[p.row][p.col].yOriginal * c.sinA;
                    p.yDisp = c.infoMap[p.row][p.col].yOriginal * c.cosA - c.infoMap[p.row][p.col].xOriginal * c.sinA;
                    c.killPixel(p);
                    numKilled++;
                    c.pixelsKilled++;
                    c.numLivePixels--;
                    addParticleHelper(p, c);
                }
                p.groupFlag = -1;
            }
        }

        return numKilled;
    }

    private int orphanChunkHelper(Pixel p, int groupNum, int total, Pixel[][] pMap)
    {
        p.groupFlag = groupNum;
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
        //float angle = (float)(Math.atan2(p.yDisp, p.xDisp) + Math.random() * .2 - .1);

        collisionParticles.addParticle(
                p.xDisp + centerX,
                p.yDisp + centerY,
                -c.angle - c.infoMap[p.row][p.col].angleOriginal + (float) Math.random() * .2f - .1f,
                //c.angle + (float) Math.random() * .2f - .1f,
                c.infoMap[p.row][p.col].r,
                c.infoMap[p.row][p.col].g,
                c.infoMap[p.row][p.col].b,
                1.4f,
                (float)(Math.random())+.1f,
                (float)(Math.random()*.5)+.01f,
                (float)(Math.random()*40)-20
        );
    }

    private void addParticleHelper(Pixel p, Collidable c)
    {
        //float angle = (float)(Math.atan2(p.yDisp, p.xDisp) + Math.random() * .2 - .1);
        collisionParticles.addParticle(
                p.xDisp + c.getCenterX(),
                p.yDisp + c.getCenterY(),
                c.angle - c.infoMap[p.row][p.col].angleOriginal + (float) Math.random() * .2f - .1f,
                c.infoMap[p.row][p.col].r,
                c.infoMap[p.row][p.col].g,
                c.infoMap[p.row][p.col].b,
                1.4f,
                (float)(Math.random())+.1f,
                (float)(Math.random()*.5)+.01f,
                (float)(Math.random()*40)-20
        );
    }

    private static void addParticleHelper(Pixel p, Collidable c, ParticleSystem particleSystem)
    {
        //float angle = (float)(Math.atan2(p.yDisp, p.xDisp) + Math.random() * .2 - .1);
        particleSystem.addParticle(
                p.xDisp + c.getCenterX(),
                p.yDisp + c.getCenterY(),
                c.angle - c.infoMap[p.row][p.col].angleOriginal + (float) Math.random() * .2f - .1f,
                c.infoMap[p.row][p.col].r,
                c.infoMap[p.row][p.col].g,
                c.infoMap[p.row][p.col].b,
                1.4f,
                (float)(Math.random())+.1f,
                (float)(Math.random()*.5)+.01f,
                (float)(Math.random()*40)-20
        );
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
}
