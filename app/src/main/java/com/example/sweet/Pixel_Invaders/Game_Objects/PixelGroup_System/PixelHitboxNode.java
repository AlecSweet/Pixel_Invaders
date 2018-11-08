package com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System;

import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;

/**
 * Created by Sweet on 10/22/2018.
 */

public class PixelHitboxNode extends HitboxNode
{
    private Pixel[] children;

    private int
            startIndX,
            endIndX,
            startIndY,
            endIndY;

    PixelHitboxNode(
            HitboxNode parent,
            int startIndX,
            int endIndX,
            int startIndY,
            int endIndY)
    {
        super(parent);
        this.startIndX = startIndX;
        this.endIndX = endIndX;
        this.startIndY = startIndY;
        this.endIndY = endIndY;
        holdsPixels = true;
    }

    private PixelHitboxNode(
            HitboxNode parent,
            int startIndX,
            int endIndX,
            int startIndY,
            int endIndY,
            float xOff,
            float yOff,
            float bRad)
    {
        super(parent);
        this.startIndX = startIndX;
        this.endIndX = endIndX;
        this.startIndY = startIndY;
        this.endIndY = endIndY;
        xOffset = xOff;
        yOffset = yOff;
        boxRadius = bRad;
        holdsPixels = true;
    }
    /*
        Redefine checkCollision so that this node checks collisions with collider
        unitil collider's leaves are reached or no collision returns;
     */
    @Override
    int checkCollisionHelper(
            float cosA,
            float sinA,
            float relativeX,
            float relativeY,
            HitboxNode collider,
            Collidable c,
            Collidable c2,
            ParticleSystem particleSystem)
    {
        int numKilled = 0;

        //Assess if both nodes have at least one live, outside pixel within them.
        if(requiresCheck() && collider.requiresCheck())
        {
            // System.out.println("checkRequired");
            if(nodeAABBcheck(cosA, sinA, relativeX, relativeY, collider))
            {
                if(collider.holdsPixels)
                {
                    numKilled += checkPixelCollisons(
                            cosA,
                            sinA,
                            relativeX,
                            relativeY,
                            (PixelHitboxNode)(collider),
                            c,
                            c2,
                            particleSystem
                    );
                }
                else
                {
                    for (HitboxNode chb : collider.getChildren())
                    {

                        numKilled += checkCollisionHelper(
                                cosA,
                                sinA,
                                relativeX,
                                relativeY,
                                chb,
                                c,
                                c2,
                                particleSystem
                        );
                    }
                }
            }
        }

        return numKilled;
    }

    private int checkPixelCollisons(
            float cosA,
            float sinA,
            float relativeX,
            float relativeY,
            PixelHitboxNode pixelNode,
            Collidable c,
            Collidable c2,
            ParticleSystem particleSystem)
    {
        int numKilled = 0;
        boolean tempAlive = false;
        for(Pixel p: children)
        {
            if(p.state > 1)
            {
                tempAlive = true;

                float pX = c.infoMap[p.row][p.col].xOriginal * cosA + c.infoMap[p.row][p.col].yOriginal * sinA + relativeX;
                float pY = c.infoMap[p.row][p.col].yOriginal * cosA - c.infoMap[p.row][p.col].xOriginal * sinA + relativeY;

                boolean tempAlive2 = false;
                for (Pixel p2 : pixelNode.children)
                {
                    if (p2.state > 1)
                    {
                        if (Math.abs(c2.infoMap[p2.row][p2.col].xOriginal - pX) <= Constants.PIXEL_SIZE + .001 &&
                                Math.abs(c2.infoMap[p2.row][p2.col].yOriginal - pY) <= Constants.PIXEL_SIZE + .001)
                        {
                            if(c.hitPixel(p) == 1)
                            {
                                numKilled++;
                                //c.addPixelKillParticle(-c.angle, p, particleSystem);
                                c.addPixelKillParticle( p, particleSystem);
                            }
                            if(c2.hitPixel(p2) == 1)
                            {
                                numKilled++;
                                //c2.addPixelKillParticle(-c.angle, p2, particleSystem);
                                c2.addPixelKillParticle(p2, particleSystem);
                            }
                        }
                    }
                    if(p2.state > 0)
                    {
                        tempAlive2 = true;
                    }
                }
                pixelNode.live = tempAlive2;
            }

            if(p.state >= 1)
            {
                tempAlive = true;
            }
        }
        live = tempAlive;
        return numKilled;
    }

    @Override
    boolean requiresCheck()
    {
        return containsOuterPixel && live && children != null;
        //return live && children != null;
    }

    public void setPixels(Pixel[] children)
    {
        this.children = children;
    }

    @Override
    public void printHitbox(int l)
    {
        if(children != null)
        {
            System.out.println("level: " + l + " x: " + xOffset + " y: " + yOffset + " radius: " + boxRadius + " live: " + requiresCheck());
        }
        else
        {
            System.out.println("level: " + l + " x: " + xOffset + " y: " + yOffset + " radius: " + boxRadius + " live: " + requiresCheck());
        }
        l++;
        for(Pixel p: children)
        {
            System.out.println("state: " + p.state + "   " + p.row + ", " + p.col);
        }
    }

    public Pixel[] getPixelChildren()
    {
        return children;
    }

    @Override
    public void resetHitbox()
    {
        if(children != null)
        {
            live = true;
            boolean temp = false;
            for(Pixel p: children)
            {
                if(!temp && p.state > 1)
                {
                    p.parent.transmitNewOutsidePixel();
                    temp = true;
                }
            }
        }
    }

    @Override
    public HitboxNode clone(Pixel[][] pMap, PixelInfo[][] info)
    {
        return cloneHelper(pMap, info,null);
    }

    @Override
    HitboxNode cloneHelper(Pixel[][] pMap, PixelInfo[][] info, HitboxNode parent)
    {
        PixelHitboxNode temp = new PixelHitboxNode(
                parent,
                startIndX,
                endIndX,
                startIndY,
                endIndY,
                xOffset,
                yOffset,
                boxRadius
        );
        temp.setPixels(new Pixel[children.length]);
        int itr = 0;
        for(int r = startIndY; r <= endIndY; r++)
        {
            for(int c = startIndX; c <= endIndX; c++)
            {
                if(pMap[r][c] != null && info[r][c] != null)
                {
                    temp.children[itr] = pMap[r][c];
                    temp.children[itr].parent = temp;
                    itr++;
                }
            }
        }
        return temp;
    }
}
