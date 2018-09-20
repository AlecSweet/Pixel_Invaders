package com.example.sweet.Pixel_Invaders.Game_Objects.Component_System;

import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.PixelGroup;
import com.example.sweet.Pixel_Invaders.Game_Objects.Pooling.ObjectNode;
import com.example.sweet.Pixel_Invaders.UI_System.ParticleSystem;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.Constants;
import com.example.sweet.Pixel_Invaders.Util.Universal_Data.GlobalInfo;
import com.example.sweet.Pixel_Invaders.Game_Objects.PixelGroup_System.Pixel;

/**
 * Created by Sweet on 3/27/2018.
 */

public class BasicGun extends Gun
{
    public BasicGun(PixelGroup pG, ParticleSystem ps, float delay, float spd)
    {
        super(delay, pG, ps, spd);
        spread = .1f;
    }

    @Override
    public boolean shoot(float cX, float cY, float angle, GlobalInfo gI, float cosA, float sinA, float slowRes)
    {
        float reduction =  (shootDelay * fireRateMod) * (1 - gI.timeSlow) * (slowRes);
        if (gI.getAugmentedTimeMillis() > lastShotTime + shootDelay * fireRateMod - reduction)
        {
            float tX = x * cosA + y * sinA + cX;
            float tY = y * cosA - x * sinA + cY;
            float arc = (numShots - 1) * .1f;

            for(int i = 0; i < numShots; i++)
            {

                if(openIndexTail.nextObject != openIndexHead)
                {
                /*if(openIndexTail.nextObject != null)
                {*/
                    float angDisp = (((float)(i + 1) / numShots) * arc) - (arc / 2);
                    angDisp += (float)(Math.random() * spread - spread / 2);
                    bullets[((Integer)openIndexTail.object)].resetBullet(tX, tY, angle + angDisp);
                    openIndexTail = openIndexTail.nextObject;
                }
                /*else
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
                }*/
                addShotParticles(shockSize, tX, tY);
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
            float sinA = (float)Math.sin(angle);
            float cosA = (float)Math.sin(angle);
            float tX = x * cosA + y * sinA + cX;
            float tY = y * cosA - x * sinA + cY;
            float arc = (numShots - 1) * .1f;

            for(int i = 0; i < numShots; i++)
            {
                /*float angDisp = (((float)(i + 1) / numShots) * arc) - (arc / 2);
                angDisp += (float)(Math.random() * spread - spread / 2);
                bullets[((Integer)openIndexTail.object)].resetBullet(tX, tY, angle + angDisp);*/
                if(openIndexTail.nextObject != openIndexHead)
                {
                    float angDisp = (((float)(i + 1) / numShots) * arc) - (arc / 2);
                    angDisp += (float)(Math.random() * spread - spread / 2);
                    bullets[((Integer)openIndexTail.object)].resetBullet(tX, tY, angle + angDisp);
                    openIndexTail = openIndexTail.nextObject;
                }
               /* else
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
                }*/
                addShotParticles(shockSize, tX, tY);
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
        /*for (Bullet b: bullets)
        {*/
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
                for(Pixel p: bullets[i].getPixels())
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
                }
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
    public void move(GlobalInfo gI, float slow)
    {
        /*for (Bullet b: bullets)
        {*/
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
                bullets[i].move(gI, slow);
                for(Pixel p: bullets[i].getPixels())
                {
                    if(p.state >= 2)
                    {
                        float cC = (float)(Math.random()) * .3f + .7f;
                        /*masterParticleSystem.addParticle(
                                p.xDisp + bullets[i].pixelGroup.getCenterX(),
                                p.yDisp + bullets[i].pixelGroup.getCenterY(),
                                bullets[i].angle + (float)(Math.random()* .2f - 1f),
                                bullets[i].pixelGroup.infoMap[p.row][p.col].r * cC,
                                bullets[i].pixelGroup.infoMap[p.row][p.col].g * cC,
                                bullets[i].pixelGroup.infoMap[p.row][p.col].b * cC,
                                .6f,
                                .2f, .02f, 4f
                        );*/
                        masterParticleSystem.addParticle(
                                p.xDisp + bullets[i].pixelGroup.getCenterX(),
                                p.yDisp + bullets[i].pixelGroup.getCenterY(),
                                bullets[i].angle,
                                bullets[i].pixelGroup.infoMap[p.row][p.col].r * cC,
                                bullets[i].pixelGroup.infoMap[p.row][p.col].g * cC,
                                bullets[i].pixelGroup.infoMap[p.row][p.col].b * cC,
                                .6f,
                                .2f, .02f, 4f
                        );
                    }
                }
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

    private void addShotParticles(float size, float x, float y)
    {
        for (float t = 0; t < Constants.twoPI; t += .04)
        {
            masterParticleSystem.addParticle(
                    x,
                    y,
                    t,
                    .2f, .2f, .2f, 1f,
                    .6f * size, .08f * size, 10f
            );
        }
    }

    @Override
    public BasicGun clone()
    {
        return new BasicGun(pixelGroupTemplate, masterParticleSystem, shootDelay, speed);
    }
}
