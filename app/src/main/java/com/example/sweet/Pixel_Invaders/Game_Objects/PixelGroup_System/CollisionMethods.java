package com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System;

import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;

import java.util.ArrayList;

/**
 * Created by Sweet on 10/24/2018.
 */

public class CollisionMethods
{
    private final static float ROTATE_SCALING = 1.41f;
    private final static int SIZE = 4;

    public static HitboxNode setupNewHitboxTree(PixelInfo[][] infoMap, Pixel[][] pMap)
    {
        HitboxNode temp = createChildrenHelper(1, infoMap[0].length - 1, 1, infoMap.length - 1, null, infoMap, pMap);
        calculateDimensionsHelper(temp);
        return temp;
    }

    private static HitboxNode createChildrenHelper(int startX, int endX, int startY, int endY, HitboxNode parent, PixelInfo[][] infoMap, Pixel[][] pMap)
    {
        int numCols = endX - startX + 1;
        int numRows = endY - startY + 1;
        int midC = numCols / 2;
        int midR = numRows / 2;

        //if(midC > 1 && midR > 1)
        if(numCols > SIZE && numRows > SIZE)
        {
            HitboxNode current = new HitboxNode(parent);
            ArrayList<HitboxNode> tempArr = new ArrayList<>();
            addToArrHelper(
                    createChildrenHelper(startX + midC, endX, startY, startY + midR - 1, current, infoMap, pMap),
                    tempArr
            );
            addToArrHelper(
                    createChildrenHelper(startX, startX + midC - 1, startY, startY + midR - 1, current, infoMap, pMap),
                    tempArr
            );
            addToArrHelper(
                    createChildrenHelper(startX + midC, endX, startY + midR, endY, current, infoMap, pMap),
                    tempArr
            );
            addToArrHelper(
                    createChildrenHelper(startX, startX + midC - 1, startY + midR, endY, current, infoMap, pMap),
                    tempArr
            );
            if(tempArr.size() > 0)
            {
                current.setChildren(tempArr.toArray(new HitboxNode[tempArr.size()]));
                for(HitboxNode c: current.getChildren())
                {
                    calculateDimensionsHelper(c);
                }
                return current;
            }
            else
            {
                return null;
            }
        }
        //else if(midC > 1 && midR <= 1)
        else if(numCols > SIZE && numRows <= SIZE)
        {
            HitboxNode current = new HitboxNode(parent);
            ArrayList<HitboxNode> tempArr = new ArrayList<>();
            addToArrHelper(
                    createChildrenHelper(startX + midC, endX, startY, endY, current, infoMap, pMap),
                    tempArr
            );
            addToArrHelper(
                    createChildrenHelper(startX, startX + midC - 1, startY, endY, current, infoMap, pMap),
                    tempArr
            );
            if(tempArr.size() > 0)
            {
                current.setChildren(tempArr.toArray(new HitboxNode[tempArr.size()]));
                for(HitboxNode c: current.getChildren())
                {
                    calculateDimensionsHelper(c);
                }
                return current;
            }
            else
            {
                return null;
            }
        }
        else if(numCols <= SIZE && numRows > SIZE)
        {
            HitboxNode current = new HitboxNode(parent);
            ArrayList<HitboxNode> tempArr = new ArrayList<>();
            addToArrHelper(
                    createChildrenHelper(startX, endX, startY, startY + midR - 1, current, infoMap, pMap),
                    tempArr
            );
            addToArrHelper(
                    createChildrenHelper(startX, endX, startY + midR, endY, current, infoMap, pMap),
                    tempArr
            );
            if(tempArr.size() > 0)
            {
                current.setChildren(tempArr.toArray(new HitboxNode[tempArr.size()]));
                for(HitboxNode c: current.getChildren())
                {
                    calculateDimensionsHelper(c);
                }
                return current;
            }
            else
            {
                return null;
            }
        }
        else
        {
            float avgX = 0;
            float avgY = 0;
            int itr = 0;
            boolean tempLive = false;
            ArrayList<Pixel> tempArr = new ArrayList<>();
            for(int r = startY; r <= endY; r++)
            {
                for(int c = startX; c <= endX; c++)
                {
                    if(infoMap[r][c] != null && pMap[r][c] != null)
                    {
                        tempLive = true;
                        avgX += infoMap[r][c].xOriginal;
                        avgY += infoMap[r][c].yOriginal;
                        tempArr.add(pMap[r][c]);
                        itr++;
                    }
                }
            }
            if(tempLive)
            {
                float radius = Constants.PIXEL_SIZE * ROTATE_SCALING * ((endX - startX > endY - startY) ? endX - startX : endY - startY);
                //radius *= 2;
                avgX /= itr;
                avgY /= itr;
                PixelHitboxNode pTemp = new PixelHitboxNode(parent, startX, endX, startY, endY);
                for(Pixel p: tempArr)
                {
                    p.parent = pTemp;
                    if(p.state > 1)
                    {
                        pTemp.transmitNewOutsidePixel();
                    }
                }
                pTemp.setPixels(tempArr.toArray(new Pixel[tempArr.size()]));
                pTemp.setBoxDimensions(avgX, avgY, radius);
                return pTemp;
            }
            else
            {
                return null;
            }
        }
    }

    private static void addToArrHelper(HitboxNode node, ArrayList<HitboxNode> arr)
    {
        if(node != null)
        {
            arr.add(node);
        }
    }

    private static void calculateDimensionsHelper(HitboxNode node)
    {
        if(node.getChildren() != null && node.getChildren().length > 0)
        {
            float len = node.getChildren().length;
            float avgX = 0;
            float avgY = 0;
            float radius = node.getChildren()[0].getBoxRadius() * ROTATE_SCALING * ((len > 1) ? 2 : 1);
            for (HitboxNode ch : node.getChildren())
            {
                avgX += ch.getxOffset();
                avgY += ch.getyOffset();
            }
            avgX /= len;
            avgY /= len;
            node.setBoxDimensions(avgX, avgY, radius);
        }
        else
        {
            node.setLive(false);
        }
    }

    public static int checkCollision(Collidable collider1, Collidable collider2, ParticleSystem particleSystem)
    {
        if(collider1.getTotalPixels() < collider2.getTotalPixels())
        {
            return collider1.getHitBox().checkCollision(collider1, collider2, particleSystem);
        }
        else
        {
            return collider2.getHitBox().checkCollision(collider2, collider1, particleSystem);
        }
    }
}
