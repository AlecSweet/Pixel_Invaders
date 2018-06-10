package com.example.sweet.game20.util;

import com.example.sweet.game20.Objects.Collidable;
import com.example.sweet.game20.Objects.CollidableGroup;
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
        //regular
        int numCheck = 0;
        /*if (Math.abs(c.getCenterX() - c2.getCenterX()) <= c.getHalfSquareLength() + c2.getHalfSquareLength() &&
                Math.abs(c.getCenterY() - c2.getCenterY()) <= c.getHalfSquareLength() + c2.getHalfSquareLength())
        {
            for(CollidableGroup cG : c.collidableGroups)
            {
                ArrayList<Pixel> tC = cG.pixels;
                for (CollidableGroup cG2 : c2.collidableGroups)
                {
                    if (Math.abs(cG.x - cG2.x) <= cG.halfSquareLength + cG2.halfSquareLength &&
                            Math.abs(cG.y - cG2.y) <= cG.halfSquareLength + cG2.halfSquareLength)
                    {
                        ArrayList<Pixel> tC2 = cG2.pixels;
                        for (Pixel p : tC)
                        {
                            if (p.live && p.outside)
                            {
                                for (Pixel p2 : tC2)
                                {
                                    if (p2.live && p2.outside)
                                    {
                                        if (Math.abs((p.xDisp + c.getCenterX()) - (p2.xDisp + c2.getCenterX())) - .002 <= Constants.PIXEL_SIZE + .004  &&
                                                Math.abs((p.yDisp + c.getCenterY()) - (p2.yDisp + c2.getCenterY())) - .002 <= Constants.PIXEL_SIZE + .004 )
                                        {
                                            p.killPixel();
                                            addParticleHelper(p, c);
                                            p2.killPixel();
                                            addParticleHelper(p2, c2);
                                            collision = true;
                                            break;
                                        }
                                    }
                                    if(!p.live)
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        }*/
        /*float c = (float)Math.cos(a);
        float s = (float)Math.sin(a);
        /*for(int i = 0; i < pixels.length; i++)
        {
            pixels[i].xDisp = pixels[i].xOriginal*c + pixels[i].yOriginal*s;
            pixels[i].yDisp = pixels[i].yOriginal*c - pixels[i].xOriginal*s;
        }*/



        /*if (Math.abs(c.getCenterX() - c2.getCenterX()) <= c.getHalfSquareLength() + c2.getHalfSquareLength() &&
                Math.abs(c.getCenterY() - c2.getCenterY()) <= c.getHalfSquareLength() + c2.getHalfSquareLength())
        {
            float cosC = (float)Math.cos(c.angle);
            float sinC = (float)Math.sin(c.angle);
            float cosC2 = (float)Math.cos(c2.angle);
            float sinC2 = (float)Math.sin(c2.angle);
            numCheck++;
            //for(Zone z: c.zones)
            for(int z = 0; z < c.zones.length; z++)
            {
                //for(Zone z2: c2.zones)
                for(int z2 = 0; z2 < c2.zones.length; z2++)
                {
                    if (Math.abs(c.zones[z].x - c2.zones[z2].x) <= c.zones[z].halfSquareLength + c2.zones[z2].halfSquareLength &&
                           Math.abs(c.zones[z].y - c2.zones[z2].y) <= c.zones[z].halfSquareLength + c2.zones[z2].halfSquareLength)
                    {
                        numCheck++;
                        //for (CollidableGroup cG : z.collidableGroups)
                        for(int cG = 0; cG < c.zones[z].collidableGroups.length; cG++)
                        {
                            Pixel[] tC = c.zones[z].collidableGroups[cG].pixels;
                            //for (CollidableGroup cG2 : z2.collidableGroups)
                            for(int cG2 = 0; cG2 < c2.zones[z2].collidableGroups.length; cG2++)
                            {
                                if (Math.abs(c.zones[z].collidableGroups[cG].x - c2.zones[z2].collidableGroups[cG2].x) <= c.zones[z].collidableGroups[cG].halfSquareLength + c2.zones[z2].collidableGroups[cG2].halfSquareLength &&
                                        Math.abs(c.zones[z].collidableGroups[cG].y - c2.zones[z2].collidableGroups[cG2].y) <= c.zones[z].collidableGroups[cG].halfSquareLength + c2.zones[z2].collidableGroups[cG2].halfSquareLength)
                                {
                                    numCheck++;
                                    Pixel[] tC2 = c2.zones[z2].collidableGroups[cG2].pixels;
                                    //for (Pixel p : tC)
                                    for(int p = 0; p < tC.length; p++)
                                    {
                                        //if (p.live && p.outside)
                                        if(tC[p].live && tC[p].outside)
                                        {
                                            //tC[p].xDisp = tC[p].xOriginal * cosC + tC[p].yOriginal * sinC;
                                            //tC[p].yDisp = tC[p].yOriginal * cosC - tC[p].xOriginal * sinC;
                                            float xDispP = tC[p].xOriginal * cosC + tC[p].yOriginal * sinC;
                                            float yDispP = tC[p].yOriginal * cosC - tC[p].xOriginal * sinC;

                                            for(int p2 = 0; p2 < tC2.length; p2++)
                                            {
                                                if(tC2[p2].live && tC2[p2].outside)
                                                {
                                                    float xDispP2 = tC2[p2].xOriginal * cosC2 + tC2[p2].yOriginal * sinC2;
                                                    float yDispP2 = tC2[p2].yOriginal * cosC2 - tC2[p2].xOriginal * sinC2;

                                                    if (Math.abs((xDispP + c.getCenterX()) - (xDispP2 + c2.getCenterX())) - .003 <= Constants.PIXEL_SIZE + .006 &&
                                                            Math.abs((yDispP + c.getCenterY()) - (yDispP2 + c2.getCenterY())) - .003 <= Constants.PIXEL_SIZE + .006)
                                                    {
                                                        numCheck++;
                                                        tC[p].killPixel();
                                                        c.numLivePixels--;
                                                        addCollisionParticleHelper(tC[p], c, c2, xDispP, yDispP);
                                                        tC2[p2].killPixel();
                                                        c2.numLivePixels--;
                                                        addCollisionParticleHelper(tC2[p2], c2, c, xDispP2, yDispP2);
                                                        collision = true;
                                                        break;
                                                    }
                                                }
                                                if (!tC[p].live)
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
        }*/
        
        /*if (Math.abs(c.getCenterX() - c2.getCenterX()) <= c.getHalfSquareLength() + c2.getHalfSquareLength() &&
                Math.abs(c.getCenterY() - c2.getCenterY()) <= c.getHalfSquareLength() + c2.getHalfSquareLength())
        {
            float cosC = (float)Math.cos(c.angle);
            float sinC = (float)Math.sin(c.angle);
            float cosC2 = (float)Math.cos(c2.angle);
            float sinC2 = (float)Math.sin(c2.angle);

            for(Zone z: c.zones)
            {
                for(Zone z2: c2.zones)
                {
                    if (Math.abs(z.x - z2.x) <= z.halfSquareLength + z2.halfSquareLength &&
                            Math.abs(z.y - z2.y) <= z.halfSquareLength + z2.halfSquareLength)
                    {
                        for (CollidableGroup cG : z.collidableGroups)
                        {
                            for (CollidableGroup cG2 : z2.collidableGroups)
                            {
                                if (Math.abs(cG.x - cG2.x) <= cG.halfSquareLength + cG2.halfSquareLength &&
                                        Math.abs(cG.y - cG2.y) <= cG.halfSquareLength + cG2.halfSquareLength)
                                {
                                    for (Pixel p : cG.pixels)
                                    {
                                        if (p.live && p.outside)
                                        {
                                            float xDispP = p.xOriginal * cosC + p.yOriginal * sinC;
                                            float yDispP = p.yOriginal * cosC - p.xOriginal * sinC;

                                            for (Pixel p2 : cG2.pixels)
                                            {
                                                if(p2.live && p2.outside)
                                                {
                                                    float xDispP2 = p2.xOriginal * cosC2 + p2.yOriginal * sinC2;
                                                    float yDispP2 = p2.yOriginal * cosC2 - p2.xOriginal * sinC2;
                                                    if (Math.abs((xDispP + c.getCenterX()) - (xDispP2 + c2.getCenterX())) - .003 <= Constants.PIXEL_SIZE + .006 &&
                                                            Math.abs((yDispP + c.getCenterY()) - (yDispP2 + c2.getCenterY())) - .003 <= Constants.PIXEL_SIZE + .006)
                                                    {
                                                        p.killPixel();
                                                        c.numLivePixels--;
                                                        addCollisionParticleHelper(p, c, c2, xDispP, yDispP);

                                                        p2.killPixel();
                                                        c2.numLivePixels--;
                                                        addCollisionParticleHelper(p2, c2, c, xDispP2, yDispP2);

                                                        collision = true;
                                                        break;
                                                    }
                                                }
                                                if (!p.live)
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
        }*/
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
                                                            if (Math.abs(p.xDisp - p2.xDisp) - .002 <= Constants.PIXEL_SIZE + .004 &&
                                                                    Math.abs(p.yDisp - p2.yDisp) - .002 <= Constants.PIXEL_SIZE + .004)
                                                            {
                                                                p.killPixel();
                                                                c.numLivePixels--;
                                                                //addCollisionParticleHelper(p, c, c2, p.xDisp, p.yDisp);
                                                                addParticleHelper(p, c);

                                                                p2.killPixel();
                                                                c2.numLivePixels--;
                                                                //addCollisionParticleHelper(p2, c2, c, p2.xDisp, p2.yDisp);
                                                                addParticleHelper(p2, c2);

                                                                collision = true;
                                                                break;
                                                            }
                                                        }
                                                        if (!p.live)
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
                    removeOrphanChunksRecursive(c2);
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
                    removeOrphanChunksRecursive(c);
            }

            c.setNeedsUpdate(true);
            //c2.knockBack(c2.getCenterX()-c.getCenterX(), c2.getCenterY() - c.getCenterY());
        }
        return numCheck;
    }

    public static boolean preventOverlap(Collidable c, Collidable c2)
    {
        //if(c.totalPixels <= c2.totalPixels)
        //{
            float cX = c2.getCenterX() - c.getCenterX();
            float cY = c2.getCenterY() - c.getCenterY();
            float radius = c2.getHalfSquareLength() + c.getHalfSquareLength();

            if (cX * cX + cY * cY < radius * radius)
            {
                /*float angle = (float) Math.atan2(-cY, -cX);
                float dist = c.speed + c2.speed;
                float totalSize = c.getTotalPixels() + c2.getTotalPixels();*/

                if(c.totalPixels <= c2.totalPixels)
                    c.knockBack((float) Math.atan2(-cY, -cX), 1, .003f);
                return true;
            }
            else
                return false;
        //}
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

        int[] groupSizes = new int[groupNumberIterator];
       //ArrayList<Integer> mappings = new ArrayList<Integer>[groupNumberIterator];
        /*for(Pixel p: c.getPixels())
        {
            if (p.live && p.groupFlag != -1)
            {
                for (Pixel n : p.neighbors)
                {
                    if (n != null && n.live && n.groupFlag != -1 && n.groupFlag != p.groupFlag)
                    {
                        int value;
                        int key;
                        if(p.groupFlag > n.groupFlag)
                        {
                            value = n.groupFlag;
                            key = p.groupFlag;
                        }
                        else
                        {
                            value = p.groupFlag;
                            key = n.groupFlag;
                        }
                        for(Pixel pT: c.getPixels())
                            if (pT.groupFlag == key)
                                pT.groupFlag = value;

                    }
                }
            }
        }*/
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
                    mappings[p.groupFlag].size += 1;

                for (Pixel n : p.neighbors)
                {
                    if (n != null && n.live && n.groupFlag != -1 && n.groupFlag != p.groupFlag)
                    {
                        if(mappings[n.groupFlag]==null)
                        {
                            mappings[n.groupFlag] = new Mapping(n.groupFlag);
                            mappings[n.groupFlag].size = 1;
                        }

                        //if(!mappings[n.groupFlag].connections.contains(p.groupFlag))
                            //mappings[n.groupFlag].connections.add(p.groupFlag);
                        //if(!mappings[p.groupFlag].connections.contains(n.groupFlag))
                            mappings[p.groupFlag].connections.add(n.groupFlag);
                            mappings[n.groupFlag].connections.add(p.groupFlag);
                            /*if(mappings[p.groupFlag].connections != mappings[n.groupFlag].connections)
                            {
                                mappings[p.groupFlag].connections.addAll(mappings[n.groupFlag].connections);
                                mappings[n.groupFlag].connections = mappings[p.groupFlag].connections;
                            }*/

                    }
                }
            }
        }

        for(Mapping m: mappings)
        {
            if( m != null)
                for(Mapping m1: mappings)
                {
                    if(m1 != null && m != m1 && m1.connections != m.connections)
                    {
                        for(Integer i: m.connections)
                        {
                            if(m1.connections.contains(i))
                            {
                                m1.connections.addAll(m.connections);
                                m.connections = m1.connections;
                                break;
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
                //System.out.println(mapping.connections + "   : " + mapping.totalSize);
            }
        }
        //for(Pixel p: c.getPixels())
        //    if(p.live)
         //       groupSizes[p.groupFlag] += 1;



        /*ArrayList<Integer> groupSizes = new ArrayList<>();
        for(Pixel p: c.getPixels())
        {
            if(p != null && p.live && p.groupFlag == -1)
            {
                groupSizes.add(orphanChunkHelper(p, groupNumberIterator, 0));
                if(groupSizes.get(groupNumberIterator) > highestSize)
                    highestSize = groupSizes.get(groupNumberIterator);
                groupNumberIterator++;
            }
        }*/

        /*for(int i = 0; i < groupSizes.length; i++)
            if (groupSizes[i] > highestSize)
                highestSize = groupSizes[i];*/

        /*for(Integer i: groupSizes)
            if (i > highestSize)
                highestSize = i;*/

        if(highestSize < c.getTotalPixels() * c.getLivablePercentage())
            c.setCollidableLive(false);

        /*if(c.getCollidableLive())
        {
            for (Pixel p : c.getPixels())
            {
                if (p.groupFlag != -1 && groupSizes.get(p.groupFlag) < highestSize)
                {
                    p.killPixel();
                    addParticleHelper(p, c);
                }
                p.groupFlag = -1;
            }
        }*/
        if(c.getCollidableLive())
        {
            for (Pixel p : c.getPixels())
            {
                if (p.groupFlag != -1 && mappings[p.groupFlag].totalSize < highestSize)
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

        /*for(int i = 0; i < groupSizes.length; i++)
            if (groupSizes[i] > highestSize)
                highestSize = groupSizes[i];*/

        /*for(Integer i: groupSizes)
            if (i > highestSize)
                highestSize = i;*/

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

    //recursive
    /*private int orphanChunkHelper(Pixel p, int groupNum)
    {
        if(p != null && p.live && p.groupFlag == -1)
        {
            p.groupFlag = groupNum;
            return orphanChunkHelper(p.neighbors[0], groupNum) +
                    orphanChunkHelper(p.neighbors[1], groupNum) +
                    orphanChunkHelper(p.neighbors[2], groupNum) +
                    orphanChunkHelper(p.neighbors[3], groupNum) + 1;
        }
        else
            return 0;
    }*/

    //Better recursive tail helper
    private int orphanChunkHelper(Pixel p, int groupNum, int total)
    {
        p.groupFlag = groupNum;
        for(int i = 0; i < 4; i++)
            if(p.neighbors[i] != null && p.neighbors[i].live == true && p.neighbors[i].groupFlag == -1)
                total = orphanChunkHelper(p.neighbors[i], groupNum, total);
        return 1 + total;
    }

    /*private void addParticleHelper(Pixel p, Collidable c)
    {
        float xDisp = p.xOriginal * c.cosA + p.yOriginal * c.sinA;
        float yDisp = p.yOriginal * c.cosA - p.xOriginal * c.sinA;
        double angle = Math.atan2(yDisp,xDisp) + Math.random() * .2 - .1;
        collisionParticles.addParticle(xDisp + c.getCenterX(), yDisp + c.getCenterY(),
                (float)Math.cos(angle), (float)Math.sin(angle),
                p.r,p.g,p.b,1,
                (float)(Math.random())+.1f,
                (float)(Math.random()*.5)+.01f,
                (float)(Math.random()*40)-20);
    }

    private void addCollisionParticleHelper(Pixel p, Collidable c, Collidable c2, float xDisp, float yDisp)
    {
        double angle = Math.atan2(yDisp,xDisp) + Math.random() * .2 - .1;
        collisionParticles.addParticle(xDisp + c.getCenterX(), yDisp + c.getCenterY(),
                (float)Math.cos(angle), (float)Math.sin(angle),
                p.r,p.g,p.b,1,
                (float)(Math.random())+.1f,
                (float)(Math.random()*.5)+.01f,
                (float)(Math.random()*40)-20);
    }*/

    private void addParticleHelper(Pixel p, Collidable c)
    {
        float angle = (float)(Math.atan2(p.yDisp - c.centerY, p.xDisp - c.centerX) + Math.random() * .2 - .1);
        collisionParticles.addParticle(p.xDisp, p.yDisp,
                angle,
                p.r,p.g,p.b,1,
                (float)(Math.random())+.1f,
                (float)(Math.random()*.5)+.01f,
                (float)(Math.random()*40)-20);
    }
}
