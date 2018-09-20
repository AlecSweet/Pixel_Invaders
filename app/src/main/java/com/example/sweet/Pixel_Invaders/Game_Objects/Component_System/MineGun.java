package com.example.sweet.Pixel_Invaders.Game_Objects.Component_System;

import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.Game_Objects.Pooling.ObjectNode;
import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Pixel;

/**
 * Created by Sweet on 8/31/2018.
 */

public class MineGun extends Gun
{
    public MineGun(PixelGroup pG, ParticleSystem ps, float delay, int num)
    {
        super(delay, pG, ps, 0, num);
        spread = 0f;
    }

    @Override
    public boolean shoot(float cX, float cY, float angle, GlobalInfo gI, float cosA, float sinA, float slowRed)
    {
        if (gI.getAugmentedTimeMillis() > lastShotTime + shootDelay * fireRateMod)
        {
            float tX = x * cosA + y * sinA + cX;
            float tY = y * cosA - x * sinA + cY;
            float arc = (numShots - 1) * .1f;

            for(int i = 0; i < numShots; i++)
            {
                float angDisp = (((float)(i + 1) / numShots) * arc) - (arc / 2);
                angDisp += (float)(Math.random() * spread - spread / 2);
                bullets[((Integer)openIndexTail.object)].resetBullet(tX, tY, angle + angDisp);
                if(openIndexTail.nextObject != null)
                {
                    openIndexTail = openIndexTail.nextObject;
                }
                else
                {
                    for(ObjectNode oN: indexNodes)
                    {
                        if(oN != openIndexTail)
                        {
                            oN.nextObject = null;
                            openIndexTail = oN;
                            break;
                        }
                    }
                }
            }
            lastShotTime = gI.getAugmentedTimeMillis();
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean shoot(float cX, float cY, float angle, GlobalInfo gI)
    {
        if (gI.getAugmentedTimeMillis() > lastShotTime + shootDelay * fireRateMod)
        {
            float tX = x * (float)Math.cos(angle) + y * (float)Math.sin(angle) + cX;
            float tY = y * (float)Math.cos(angle) - x * (float)Math.sin(angle) + cY;
            float arc = (numShots - 1) * .1f;

            for(int i = 0; i < numShots; i++)
            {
                float angDisp = (((float)(i + 1) / numShots) * arc) - (arc / 2);
                angDisp += (float)(Math.random() * spread - spread / 2);
                bullets[((Integer)openIndexTail.object)].resetBullet(tX, tY, angle + angDisp);
                if(openIndexTail.nextObject != null)
                {
                    openIndexTail = openIndexTail.nextObject;
                }
                else
                {
                    for(ObjectNode oN: indexNodes)
                    {
                        if(oN != openIndexTail)
                        {
                            oN.nextObject = null;
                            openIndexTail = oN;
                        }
                    }
                }
            }
            lastShotTime = gI.getAugmentedTimeMillis();
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void move(GlobalInfo gI)
    {
        int l = bullets.length;
        for (int i = 0; i < l; i++)
        {
            /*float centerX;
            float centerY;
            if(bullets[i].pixelGroup.enableLocationChain)
            {
                *//*centerX = bullets[i].pixelGroup.locationDrawTail.x;
                centerY = bullets[i].pixelGroup.locationDrawTail.y;*//*
                centerX = bullets[i].pixelGroup.centerX - bullets[i].cosA * .01f;
                centerY = bullets[i].pixelGroup.centerY - bullets[i].sinA * .01f;
            }
            else
            {
                centerX = bullets[i].pixelGroup.centerX - bullets[i].cosA * .01f;
                centerY = bullets[i].pixelGroup.centerY - bullets[i].sinA * .01f;
            }*/
            if(bullets[i].live)
            {
                bullets[i].move(gI);
                /*for(Pixel p: bullets[i].getPixels())
                {
                    if(p.state >= 2)
                    {
                        float cC = (float)(Math.random()) * 1f + .5f;
                        masterParticleSystem.addParticle(
                                p.xDisp + bullets[i].pixelGroup.getCenterX(),
                                p.yDisp + bullets[i].pixelGroup.getCenterY(),
                                bullets[i].angle + (float)(Math.random()* .2f - 1f),
                                bullets[i].pixelGroup.infoMap[p.row][p.col].r * cC,
                                bullets[i].pixelGroup.infoMap[p.row][p.col].g * cC,
                                bullets[i].pixelGroup.infoMap[p.row][p.col].b * cC,
                                .6f,
                                .2f, .02f, 4f
                        );
                    }
                }*/
            }
            if(!bullets[i].live && bullets[i].active)
            {
                indexNodes[i].nextObject = null;
                openIndexHead.nextObject = indexNodes[i];
                openIndexHead = openIndexHead.nextObject;
                bullets[i].active = false;
            }
        }
    }

    @Override
    public MineGun clone()
    {
        return new MineGun(pixelGroupTemplate, masterParticleSystem, shootDelay, totalBullets);
    }
}
