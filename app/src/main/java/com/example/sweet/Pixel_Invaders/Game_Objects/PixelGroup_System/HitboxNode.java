package com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System;

import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;

/**
 * Created by Sweet on 10/19/2018.
 */

public class HitboxNode
{
    float
            xOffset,
            yOffset,
            boxRadius;

    boolean
            live = true,
            containsOuterPixel = false,
            holdsPixels = false;

    private HitboxNode parent;
    private HitboxNode[] children;

    public HitboxNode(HitboxNode parent)
    {
        this.parent = parent;
    }

    private HitboxNode(HitboxNode parent, float xOff, float yOff, float bRad)
    {
        this.parent = parent;
        xOffset = xOff;
        yOffset = yOff;
        boxRadius = bRad;
    }

    /*

     */
    int checkCollision(Collidable smallerCollider, Collidable largerCollider, ParticleSystem particleSystem)
    {
        float angle = largerCollider.angle - (float)(Math.floor(largerCollider.angle/(2*Math.PI))*2*Math.PI);
        float difX = smallerCollider.tempCenterX - largerCollider.tempCenterX;
        float difY = smallerCollider.tempCenterY - largerCollider.tempCenterY;
        float tCosA = (float)Math.cos(-angle);
        float tSinA = (float)Math.sin(-angle);

        return checkCollisionHelper(
                smallerCollider.cosA * tCosA - smallerCollider.sinA * tSinA,
                smallerCollider.sinA * tCosA + smallerCollider.cosA * tSinA,
                difX * tCosA + difY * tSinA,
                difY * tCosA - difX * tSinA,
                largerCollider.getHitBox(),
                smallerCollider,
                largerCollider,
                particleSystem
        );
    }

    /*
        Cross check collisions between this tree and the collider tree using AABB with this
        tree aligned to the collider tree. Traverse down the tree ignoring branches that
        contain no collidable pixels until leaves are reached and can return pixel collisions.
        When calling, the collider should always be the larger (number of nodes) tree since it
        is being aligned to.
     */
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

        //Assess if both nodes have at least one live, outside pixel bellow them.
        if(requiresCheck() && collider.requiresCheck())
        {
            //Cross check children if this node and collider are overlapping
            if(nodeAABBcheck(cosA, sinA, relativeX, relativeY, collider))
            {
                boolean tempAlive = false;
                for (HitboxNode hb : children)
                {
                    if(hb.live)
                    {
                        tempAlive = true;
                        boolean tempAlive2 = false;
                        if(!collider.holdsPixels)
                        {
                            for (HitboxNode chb : collider.children)
                            {
                                if (chb.live)
                                {
                                    tempAlive2 = true;
                                    numKilled += hb.checkCollisionHelper(
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
                        else
                        {
                            tempAlive2 = true;
                            numKilled += hb.checkCollisionHelper(
                                    cosA,
                                    sinA,
                                    relativeX,
                                    relativeY,
                                    collider,
                                    c,
                                    c2,
                                    particleSystem
                            );
                        }
                        collider.live = tempAlive2;
                    }
                }
                live = tempAlive;
            }
        }

        return numKilled;
    }


    /*
        Returns true if this node and collider are overlapping.
     */
    boolean nodeAABBcheck(float cosA, float sinA, float relativeX, float relativeY, HitboxNode collider)
    {
        return Math.abs(collider.getxOffset() - (xOffset * cosA + yOffset * sinA + relativeX)) < collider.getBoxRadius() + boxRadius &&
                Math.abs(collider.getyOffset() - (yOffset * cosA - xOffset * sinA + relativeY)) < collider.getBoxRadius() + boxRadius;
    }

    /*
        Return true if somewhere within the tree bellow this node exists
        at least one live pixel and one outside pixel.
     */
    boolean requiresCheck()
    {
        return containsOuterPixel && live && children != null;
        //return live && children != null;
    }

    /*
        Transmit, begining from a pixel, the presence of a new outside
        pixel up the tree.
     */
    public void transmitNewOutsidePixel()
    {
        if(!containsOuterPixel)
        {
            containsOuterPixel = true;
            if(parent != null)
            {
                parent.transmitNewOutsidePixel();
            }
        }
    }

    public boolean getHoldsPixels()
    {
        return holdsPixels;
    }

    public boolean getContainsOuterPixel()
    {
        return containsOuterPixel;
    }

    HitboxNode[] getChildren()
    {
        return children;
    }

    float getxOffset()
    {
        return xOffset;
    }

    float getyOffset()
    {
        return yOffset;
    }

    float getBoxRadius()
    {
        return boxRadius;
    }

    void setChildren(HitboxNode[] children)
    {
        this.children = children;
    }

    public void setLive(boolean val)
    {
        live = val;
    }

    void setBoxDimensions(float xOffset, float yOffset, float boxRadius)
    {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.boxRadius = boxRadius;
    }

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
        for(HitboxNode h: children)
        {
            h.printHitbox(l);
        }
    }

    public void resetHitbox()
    {
        if(children != null)
        {
            live = true;
            for(HitboxNode ch: children)
            {
                ch.resetHitbox();
            }
        }
    }

    public HitboxNode clone(Pixel[][] pMap, PixelInfo[][] info)
    {
        return cloneHelper(pMap, info, null);
    }

    HitboxNode cloneHelper(Pixel[][] pMap, PixelInfo[][] info, HitboxNode parent)
    {
        HitboxNode current = new HitboxNode(parent, xOffset, yOffset, boxRadius);
        if(children != null && children.length > 0)
        {
            current.children = new HitboxNode[children.length];
            for(int i = 0; i < children.length; i++)
            {
                current.children[i] = children[i].cloneHelper(pMap, info, current);
            }
        }
        return current;
    }
}
