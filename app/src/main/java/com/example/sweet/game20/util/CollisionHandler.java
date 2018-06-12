package com.example.sweet.game20.util;

import com.example.sweet.game20.Objects.Collidable;
import com.example.sweet.game20.Objects.CollidableGroup;
import com.example.sweet.game20.Objects.Enemy;
import com.example.sweet.game20.Objects.ParticleSystem;
import com.example.sweet.game20.Objects.Pixel;
import com.example.sweet.game20.Objects.Zone;

import java.util.ArrayList;
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

    public int checkCollisions(Collidable c, Collidable c2)
    {
        boolean collision = false;
        int numCheck = 0;

        numCheck++;
        if (Math.abs(c.getCenterX() - c2.getCenterX()) <= c.getHalfSquareLength() + c2.getHalfSquareLength() &&
                Math.abs(c.getCenterY() - c2.getCenterY()) <= c.getHalfSquareLength() + c2.getHalfSquareLength())
        {
            for(Zone z: c.zones)
            {
                if (z.live)
                {
                    for (Zone z2 : c2.zones)
                    {
                        numCheck++;
                        if (z2.live && Math.abs(z.x - z2.x) <= z.halfSquareLength + z2.halfSquareLength &&
                                Math.abs(z.y - z2.y) <= z.halfSquareLength + z2.halfSquareLength)
                        {
                            for (CollidableGroup cG : z.collidableGroups)
                            {
                                if(cG.live)
                                {
                                    for (CollidableGroup cG2 : z2.collidableGroups)
                                    {
                                        numCheck++;
                                        if (cG2.live && Math.abs(cG.x - cG2.x) <= cG.halfSquareLength + cG2.halfSquareLength &&
                                                Math.abs(cG.y - cG2.y) <= cG.halfSquareLength + cG2.halfSquareLength)
                                        {
                                            for (Pixel p : cG.pixels)
                                            {
                                                if (p.live && p.outside)
                                                {
                                                    for (Pixel p2 : cG2.pixels)
                                                    {
                                                        if (p2.live && p2.outside)
                                                        {
                                                            numCheck++;
                                                            if (Math.abs((p.xDisp + c.centerX) - (p2.xDisp + c2.centerX)) - .002 <= Constants.PIXEL_SIZE + .004 &&
                                                                    Math.abs((p.yDisp + c.centerY) - (p2.yDisp + c2.centerY)) - .002 <= Constants.PIXEL_SIZE + .004)
                                                            {
                                                                addParticleHelper(p, c);
                                                                p.killPixel(c.cosA, c.sinA);
                                                                c.numLivePixels--;

                                                                addParticleHelper(p2, c2);
                                                                p2.killPixel(c2.cosA, c2.sinA);
                                                                c2.numLivePixels--;

                                                                collision = true;
                                                                break;
                                                            }
                                                        }
                                                        if (!p.live)
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

        if(collision)
        {
            if(c2.getChunkDeletion())
            {
                if (c2.totalPixels > 800)
                {
                    if (System.currentTimeMillis() - c2.lastOrphanChunkCheck > c2.orphanChunkCheckDelay)
                    {
                        c2.lastOrphanChunkCheck = System.currentTimeMillis();
                        removeOrphanChunks(c2);
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
                if (c.totalPixels > 800)
                {
                    if (System.currentTimeMillis() - c.lastOrphanChunkCheck > c.orphanChunkCheckDelay)
                    {
                        c.lastOrphanChunkCheck = System.currentTimeMillis();
                        removeOrphanChunks(c);
                    }
                }
                else
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
                e.knockBack((float) Math.atan2(-cY, -cX), 1, .003f);
            }
        }
    }

    public void removeOrphanChunks(Collidable c)
    {
        int groupNumberIterator = 0;
        int highestSize = 0;

        for (Pixel p : c.getPixels())
        {
            if (p.live)
            {
                if (p.groupFlag == -1)
                    for (Pixel n : p.neighbors)
                        if (n != null && n.live && n.groupFlag != -1)
                            p.groupFlag = n.groupFlag;
                if (p.groupFlag == -1)
                {
                    p.groupFlag = groupNumberIterator;
                    groupNumberIterator++;
                }

                for (Pixel n : p.neighbors)
                    if (n != null && n.live)
                        n.groupFlag = p.groupFlag;

            }
        }

        Mapping[] mappings = new Mapping[groupNumberIterator];
        for(Pixel p: c.getPixels())
        {
            if (p.live && p.groupFlag != -1)
            {
                if(mappings[p.groupFlag]==null)
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
                        if(mappings[n.groupFlag]==null)
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

        for(Mapping m: mappings)
        {
            if( m != null)
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

        for(Mapping mapping: mappings)
        {
            if(mapping!= null)
            {
                for (Integer connection : mapping.connections)
                {
                    mapping.totalSize += mappings[connection].size;
                }
                if (mapping.totalSize > highestSize)
                    highestSize = mapping.totalSize;
            }
        }

        if(highestSize < c.getTotalPixels() * c.getLivablePercentage())
            c.setCollidableLive(false);

        if(c.getCollidableLive())
        {
            for (Pixel p : c.getPixels())
            {
                if (p.groupFlag != -1 && mappings[p.groupFlag].totalSize < highestSize)
                {
                    p.xDisp = p.xOriginal * c.cosA + p.yOriginal * c.sinA;
                    p.yDisp = p.yOriginal * c.cosA - p.xOriginal * c.sinA;
                    addParticleHelper(p, c);
                    p.killPixel();
                    c.numLivePixels--;
                }
                p.groupFlag = -1;
            }
        }
        else
        {
            for (Pixel p : c.getPixels())
            {
                if(p.live)
                {
                    p.xDisp = p.xOriginal * c.cosA + p.yOriginal * c.sinA;
                    p.yDisp = p.yOriginal * c.cosA - p.xOriginal * c.sinA;
                    addParticleHelper(p, c);
                    p.killPixel();
                    c.numLivePixels--;
                }
            }
        }
    }

    public void removeOrphanChunksRecursive(Collidable c)
    {
        int groupNumberIterator = 0;
        int highestSize = 0;

        ArrayList<Integer> groupSizes = new ArrayList<>();
        for(Pixel p: c.getPixels())
        {
            if(p != null && p.live && p.groupFlag == -1)
            {
                groupSizes.add(orphanChunkHelper(p, groupNumberIterator, 0));
                if(groupSizes.get(groupNumberIterator) > highestSize)
                    highestSize = groupSizes.get(groupNumberIterator);
                groupNumberIterator++;
            }
        }

        if(highestSize < c.getTotalPixels() * c.getLivablePercentage())
            c.setCollidableLive(false);

        if(c.getCollidableLive())
        {
            for (Pixel p : c.getPixels())
            {
                if (p.groupFlag != -1 && groupSizes.get(p.groupFlag) < highestSize)
                {
                    p.killPixel();
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
                if(p.live)
                {
                    p.killPixel();
                    c.numLivePixels--;
                    addParticleHelper(p, c);
                }
                p.groupFlag = -1;
            }
        }
    }

    private int orphanChunkHelper(Pixel p, int groupNum, int total)
    {
        p.groupFlag = groupNum;
        for(int i = 0; i < 4; i++)
            if(p.neighbors[i] != null && p.neighbors[i].live == true && p.neighbors[i].groupFlag == -1)
                total = orphanChunkHelper(p.neighbors[i], groupNum, total);
        return 1 + total;
    }

    private void addParticleHelper(Pixel p, Collidable c)
    {
        float angle = (float)(Math.atan2(p.yDisp, p.xDisp) + Math.random() * .2 - .1);
        collisionParticles.addParticle(p.xDisp + c.centerX, p.yDisp + c.centerY,
                angle,
                p.r,p.g,p.b,1,
                (float)(Math.random())+.1f,
                (float)(Math.random()*.5)+.01f,
                (float)(Math.random()*40)-20);
    }
}
