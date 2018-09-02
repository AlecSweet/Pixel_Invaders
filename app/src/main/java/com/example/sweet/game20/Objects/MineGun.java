package com.example.sweet.game20.Objects;

import com.example.sweet.game20.GlobalInfo;
import com.example.sweet.game20.util.Constants;

/**
 * Created by Sweet on 8/31/2018.
 */

public class MineGun extends Gun
{
    public MineGun(PixelGroup pG, ParticleSystem ps, double delay, int num)
    {
        super(delay, pG, ps, 0, num);
        spread = 0f;
    }

    /*public BasicGun(ParticleSystem ps)
    {
        super(1000, ps, .018f);
        //ImageParser.parseImage(context, R.drawable.basicbullet, R.drawable.basicbullet_light, tID, sL)
        spread = .06f;
    }*/

    /*@Override
    public boolean shoot(float x, float y, float angle)
    {
        if (System.currentTimeMillis() > lastShotTime + shootDelay * fireRateMod)
        {
            float arc = (numShots - 1) * .1f;

            for(int i = 0; i < numShots; i++)
            {
                float angDisp = (((float)(i + 1) / numShots) * arc) - (arc / 2);
                angDisp += (float)(Math.random() * spread - spread / 2);
                lastShotTime = System.currentTimeMillis();
                bulletPool.pop().resetBullet(x, y, angle + angDisp);
                addShotParticles(angle, x, y);
            }
            return true;
        }
        else
            return false;
    }*/

    /*@Override
    public boolean shoot(float x, float y, float angle, long currentFrame, float slow)
    {
        if ((currentFrame - lastShotFrame) * slow > shotFrameDelay * fireRateMod)
        {
            float arc = (numShots - 1) * .1f;

            for(int i = 0; i < numShots; i++)
            {
                float angDisp = (((float)(i + 1) / numShots) * arc) - (arc / 2);
                angDisp += (float)(Math.random() * spread - spread / 2);
                //bulletPool.pop().resetBullet(x, y, angle + angDisp);
                bullets[((Integer)openIndexTail.object)].resetBullet(x, y, angle + angDisp);
                openIndexTail = openIndexTail.nextObject;
                addShotParticles(angle, x, y);
            }
            lastShotFrame = currentFrame;
            return true;
        }
        else
            return false;
    }
*/
    @Override
    public boolean shoot(float cX, float cY, float angle, GlobalInfo gI, float cosA, float sinA)
    {
        /*p.xDisp = infoMap[p.row][p.col].xOriginal * cosA +
                infoMap[p.row][p.col].yOriginal * sinA;
        p.yDisp = infoMap[p.row][p.col].yOriginal * cosA -
                infoMap[p.row][p.col].xOriginal * sinA;*/
        if (gI.getAugmentedTimeMillis() > lastShotTime + shootDelay * fireRateMod)
        {
            float tX = x * cosA + y * sinA + cX;
            float tY = y * cosA - x * sinA + cY;
            float arc = (numShots - 1) * .1f;

            for(int i = 0; i < numShots; i++)
            {
                float angDisp = (((float)(i + 1) / numShots) * arc) - (arc / 2);
                angDisp += (float)(Math.random() * spread - spread / 2);
                //bulletPool.pop().resetBullet(x, y, angle + angDisp);
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
        /*p.xDisp = infoMap[p.row][p.col].xOriginal * cosA +
                infoMap[p.row][p.col].yOriginal * sinA;
        p.yDisp = infoMap[p.row][p.col].yOriginal * cosA -
                infoMap[p.row][p.col].xOriginal * sinA;*/
        if (gI.getAugmentedTimeMillis() > lastShotTime + shootDelay * fireRateMod)
        {
            float tX = x * (float)Math.cos(angle) + y * (float)Math.sin(angle) + cX;
            float tY = y * (float)Math.cos(angle) - x * (float)Math.sin(angle) + cY;
            float arc = (numShots - 1) * .1f;

            for(int i = 0; i < numShots; i++)
            {
                float angDisp = (((float)(i + 1) / numShots) * arc) - (arc / 2);
                angDisp += (float)(Math.random() * spread - spread / 2);
                //bulletPool.pop().resetBullet(x, y, angle + angDisp);
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
            return false;
    }

    @Override
    public void move(float slow)
    {
        /*for (Bullet b: bullets)
        {*/
        int l = bullets.length;
        for (int i = 0; i < l; i++)
        {
            float centerX;
            float centerY;
            if(bullets[i].pixelGroup.enableLocationChain)
            {
                /*centerX = bullets[i].pixelGroup.locationDrawTail.x;
                centerY = bullets[i].pixelGroup.locationDrawTail.y;*/
                centerX = bullets[i].pixelGroup.centerX - bullets[i].cosA * .01f;
                centerY = bullets[i].pixelGroup.centerY - bullets[i].sinA * .01f;
            }
            else
            {
                centerX = bullets[i].pixelGroup.centerX - bullets[i].cosA * .01f;
                centerY = bullets[i].pixelGroup.centerY - bullets[i].sinA * .01f;
            }
            if(bullets[i].live)
            {
                bullets[i].move(slow);
                /*for(int i = 0; i < 4; i ++)
                {
                    masterParticleSystem.addParticle(
                            centerX, centerY,
                            bullets[i].angle + (float) (Math.random() * .3 - .15),
                            bullets[i].pixelGroup.pixels[0].r, bullets[i].pixelGroup.pixels[0].g, bullets[i].pixelGroup.pixels[0].b, 1f,
                            (float)(Math.random()*.03f) + .01f, (float)(Math.random()*.003f) + .002f, 10f
                    );
                }*/
                for(Pixel p: bullets[i].pixelGroup.pixels)
                {
                    //float cC = (float)(Math.random()) * .1f -.5f + 1;
                    //if (p.live && p.outside)
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
                //bulletPool.add(b);
                indexNodes[i].nextObject = null;
                openIndexHead.nextObject = indexNodes[i];
                openIndexHead = openIndexHead.nextObject;
                bullets[i].active = false;
            }
        }
    }

    public void addShotParticles(float angle, float x, float y)
    {
        for (float t = 0; t < Constants.twoPI; t += .04)
        {
            masterParticleSystem.addParticle(
                    x, y,
                    t,
                    0f, 0f, 0f, .1f,
                    .4f, .1f, 10f
            );
        }
    }

    @Override
    public MineGun clone()
    {
        return new MineGun(pixelGroupTemplate, masterParticleSystem, shootDelay, totalBullets);
    }
}
